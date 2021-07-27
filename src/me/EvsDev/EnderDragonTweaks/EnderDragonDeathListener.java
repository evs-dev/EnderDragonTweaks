package me.EvsDev.EnderDragonTweaks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EnderDragonDeathListener implements Listener {

    private static int delayTicks = 80;
    private static String xpMode = "levels";
    private static int xpPerPlayer = 68;
    private static double eggRespawnChance = 1.0d;
    private static int orbCount = 8;
    private static int playerRadius = 128;
    private static List<String> commandsList;
    private static boolean doGiveXP = true;
    private static boolean doDecorationOrbs = true;
    private static boolean doSpawnEgg = true;
    private static boolean doDefeatAnnouncement = true;
    private static boolean doCommands = true;
    private static boolean overrideEggY = false;
    private static Vector configuredEggLocationAsVector;

    private final Random RANDOM = new Random();

    public EnderDragonDeathListener() {
        final ConfigManager configManager = Main.getConfigManager();
        delayTicks = configManager.getInt(ConfigManager.entry_delay);
        xpMode = configManager.getString(ConfigManager.entry_xpMode).toLowerCase();
        xpPerPlayer = configManager.getInt(ConfigManager.entry_xpPerPlayer);
        orbCount = configManager.getInt(ConfigManager.entry_orbCountPerPlayer);
        eggRespawnChance = configManager.getDouble(ConfigManager.entry_eggRespawnChance);
        commandsList = configManager.getStringList(ConfigManager.entry_commandsList);
        doGiveXP = configManager.getBoolean(ConfigManager.entry_enableXP);
        doDecorationOrbs = configManager.getBoolean(ConfigManager.entry_enableDecorationOrbs);
        doSpawnEgg = configManager.getBoolean(ConfigManager.entry_enableEggRespawn);
        doDefeatAnnouncement = configManager.getBoolean(ConfigManager.entry_enableDefeatAnnouncement);
        doCommands = configManager.getBoolean(ConfigManager.entry_enableCommands);
        overrideEggY = configManager.getBoolean(ConfigManager.entry_overrideEggY);
        configuredEggLocationAsVector = configManager.getEggLocationAsVector();
        playerRadius = configManager.getInt(ConfigManager.entry_playerDistanceFromOrigin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnderDragonDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof EnderDragon)) return;

        final LivingEntity dragonEntity = e.getEntity();
        final World theEnd = dragonEntity.getWorld();

        if (theEnd.getEnvironment() != Environment.THE_END) return;

        if (doGiveXP)
            e.setDroppedExp(0);

        theEnd.spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, dragonEntity.getLocation(), 1200, 2, 1, 2, 1);

        EndCrystalPlacedListener.startCooldown();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (doGiveXP)
                    giveXP(dragonEntity, theEnd);
                if (doSpawnEgg)
                    spawnEgg(theEnd);
                if (doDefeatAnnouncement)
                    sendDefeatAnnouncement(dragonEntity.getKiller(), dragonEntity.getLastDamageCause(), theEnd);
                if (doCommands) {
                    final List<Player> allParticipants = Util.getPlayersInEndCentreRadius(
                        theEnd,
                        Main.getConfigManager().getInt(ConfigManager.entry_playerDistanceFromOrigin)
                    );
                    final List<String> participantNames = allParticipants
                        .stream().map(p -> p.getName()).collect(Collectors.toList());
                    final List<String> participantDisplayNames = allParticipants
                        .stream().map(p -> p.getDisplayName()).collect(Collectors.toList());

                    if (dragonEntity.getKiller() == null && participantNames.size() == 1) {
                        // If the Dragon is defeated by a non-player and there is only one player, that player must be the killer
                        runCommands(participantNames.get(0), participantDisplayNames.get(0), participantNames, participantDisplayNames);
                    } else {
                        runCommands(
                            Util.getKillerName(dragonEntity.getKiller(), dragonEntity.getLastDamageCause(), false),
                            Util.getKillerName(dragonEntity.getKiller(), dragonEntity.getLastDamageCause(), true),
                            participantNames,
                            participantDisplayNames
                       );
                    }
                }
            }
        }.runTaskLater(Main.getPlugin(Main.class), delayTicks);
    }

    private void giveXP(LivingEntity dragonEntity, World theEnd) {
        // For every player in the End...
        for (Player player : Util.getPlayersInEndCentreRadius(theEnd, playerRadius)) {
            // Give player XP
            switch (xpMode) {
                default:
                case "levels":
                    player.giveExpLevels(xpPerPlayer);
                    break;
                case "points":
                    player.giveExp(xpPerPlayer);
                    break;
            }

            Location playerLocation = player.getEyeLocation();
            theEnd.spawnParticle(Particle.PORTAL, playerLocation, 100, 0.3f, 0.3f, 0.3f, 0.5f);

            if (!doDecorationOrbs) continue;
            // Spawn XP orbs above the player (for decoration)
            for (int i = 0; i < orbCount; i++) {
                Location orbLocation = playerLocation.clone();
                orbLocation.add(0, i * 4f + 3, 0);
                theEnd.spawn(orbLocation, ExperienceOrb.class).setExperience(1);
            }
        }
    }

    private void spawnEgg(World theEnd) {
        // The game automatically spawns an Egg when the Dragon is first killed
        // This plugin shouldn't spawn another one
        if (!theEnd.getEnderDragonBattle().hasBeenPreviouslyKilled()) return;
        if (RANDOM.nextDouble() > eggRespawnChance) return;

        Location eggLocation = findSpawnEggLocation(theEnd);
        if (eggLocation == null) {
            Util.logWarning("Unable to find suitable position for Dragon Egg"
                    + "(if this the Dragon's first death, this may be because the game has already spawned an Egg)"
                );
            return;
        }
        eggLocation.getBlock().setType(Material.DRAGON_EGG);
        Util.logInfo("Spawned Dragon Egg at " +
            String.format("%s %s %s in world %s",
                eggLocation.getBlockX(),
                eggLocation.getBlockY(),
                eggLocation.getBlockZ(),
                eggLocation.getWorld().getName()
            )
        );
    }

    private void sendDefeatAnnouncement(Player killer, EntityDamageEvent damage, World theEnd) {
        final String killerName = Util.getKillerName(killer, damage, true);
        final List<String> fightParticipantNames = Util.getPlayersInEndCentreRadius(
            theEnd,
            Main.getConfigManager().getInt(ConfigManager.entry_playerDistanceFromOrigin)
        ).stream().map(p -> p.getDisplayName()).collect(Collectors.toList());

        Bukkit.broadcastMessage(
            Util.formatDefeatAnnouncementMessage(killerName, killer != null, fightParticipantNames, Main.getConfigManager())
        );
    }

    private void runCommands(String killerName, String killerDisplayName, List<String> participantNames, List<String> participantDisplayNames) {
        if (commandsList.size() == 0) return;
        // Remove the killer from participants to allow maximum flexibility in the config
        participantNames.remove(killerName);
        participantDisplayNames.remove(killerDisplayName);
        final String participantsString = participantDisplayNames.size() > 0 ?
            Util.formatParticipantsList(participantDisplayNames) : Main.getConfigManager().getString(ConfigManager.entry_commandsNoParticipantsFiller);

        Util.logInfo("Executing " + commandsList.size() + " command(s)");

        for (String command : commandsList) {
            final String cmd = command
                .replace("<killer>", killerName)
                .replace("<killer-display-name>", killerDisplayName)
                .replace("<participants-list>", participantsString);
            if (cmd.contains("<each-participant>")) {
                // Run the command for each participant individually
                for (String participantName : participantNames) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<each-participant>", participantName));
                }
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }
    }

    private Location findSpawnEggLocation(World theEnd) {
        final Location searchLocation = new Location(
            theEnd,
            configuredEggLocationAsVector.getBlockX(),
            0,
            configuredEggLocationAsVector.getBlockZ()
        );

        if (overrideEggY) {
            searchLocation.setY(configuredEggLocationAsVector.getBlockY());
            return searchLocation;
        }

        final int startY = searchLocation.getBlockY();
        final int worldHeight = theEnd.getMaxHeight();
        // Search through the Y value of (0, 0) to find a place for the egg
        for (int i = startY; i <= worldHeight; i++) {
            searchLocation.setY(i);
            if (searchLocation.getBlock().getType() != Material.BEDROCK) continue;

            // The block is bedrock, so...
            Location aboveLocation = searchLocation.clone().add(0, i == worldHeight ? 0 : 1, 0);
            // Check if the above block is air
            if (aboveLocation.getBlock().isEmpty()) {
                // An air block above the portal bedrock pillar has been found
                return aboveLocation;
            }
        }
        // Unable to find anywhere for the Egg to go
        return null;
    }
}