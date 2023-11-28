package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EnderDragonDeathListener extends AbstractEnderDragonTweaksListener {

    private static int delayTicks;
    private static int playerRadius;

    private static boolean doGiveXP;
    private static String xpInterpretation;
    private static int xpAmount;
    private static String xpDistribution;

    private static boolean doDecorationOrbs;
    private static int orbCount;

    private static boolean doSpawnEgg;
    private static double eggRespawnChance;
    private static String eggRespawnAnnouncement;
    private static Vector configuredEggLocationAsVector;
    private static boolean overrideEggY;

    private static boolean doDefeatAnnouncement;

    private static boolean doCommands;
    private static List<String> commandsList;

    private final Random RANDOM = new Random();

    public EnderDragonDeathListener() {
        final ConfigManager configManager = Main.getConfigManager();
        delayTicks = configManager.MAIN_SECTION.getInt("delay");
        playerRadius = configManager.MAIN_SECTION.getInt("max-player-distance-from-end-centre");

        doGiveXP = configManager.FEATURE_XP_DROP.isEnabled();
        if (doGiveXP) {
            xpInterpretation = configManager.FEATURE_XP_DROP.getString("interpretation").toLowerCase();
            xpAmount = configManager.FEATURE_XP_DROP.getInt("amount");
            if (xpAmount < 0) xpAmount = 0;
            xpDistribution = configManager.FEATURE_XP_DROP.getString("distribution").toLowerCase();
        }

        doDecorationOrbs = configManager.FEATURE_DECORATION_ORBS.isEnabled();
        if (doDecorationOrbs) {
            orbCount = configManager.FEATURE_DECORATION_ORBS.getInt("orb-count-per-player");
        }

        doSpawnEgg = configManager.FEATURE_EGG_RESPAWN.isEnabled();
        if (doSpawnEgg) {
            eggRespawnChance = configManager.FEATURE_EGG_RESPAWN.getDouble("chance");
            eggRespawnAnnouncement = configManager.FEATURE_EGG_RESPAWN.getString("announcement");
            configuredEggLocationAsVector = new Vector(
                configManager.FEATURE_EGG_RESPAWN.getInt("position.x"),
                configManager.FEATURE_EGG_RESPAWN.getInt("position.y"),
                configManager.FEATURE_EGG_RESPAWN.getInt("position.z")
            );
            overrideEggY = configManager.FEATURE_EGG_RESPAWN.getBoolean("position.override-y");
        }

        doDefeatAnnouncement = configManager.FEATURE_DEFEAT_ANNOUNCEMENT.isEnabled();

        doCommands = configManager.FEATURE_CUSTOM_COMMANDS.isEnabled();
        if (doCommands) {
            commandsList = configManager.FEATURE_CUSTOM_COMMANDS.getStringList("commands");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnderDragonDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof EnderDragon)) return;

        final LivingEntity dragonEntity = e.getEntity();
        final World theEnd = dragonEntity.getWorld();

        if (theEnd.getEnvironment() != Environment.THE_END) return;

        if (doGiveXP)
            e.setDroppedExp(0);

        if (Main.getConfigManager().FEATURE_STATISTICS.isEnabled())
            setStats(dragonEntity);

        theEnd.spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, dragonEntity.getLocation(), 1200, 2, 1, 2, 1);

        EndCrystalPlacedListener.startCooldown();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (doDefeatAnnouncement)
                    sendDefeatAnnouncement(dragonEntity.getKiller(), dragonEntity.getLastDamageCause(), theEnd);
                if (doGiveXP)
                    giveXP(dragonEntity, theEnd);
                if (doSpawnEgg)
                    spawnEgg(theEnd);
                if (doCommands) {
                    final List<Player> allParticipants = Util.getPlayersInEndCentreRadius(
                        theEnd,
                        playerRadius
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

    public void setStats(final LivingEntity dragonEntity) {
        Main.getStatisticsManager().setStat("dragonDeathCount", Main.getStatisticsManager().getStatInt("dragonDeathCount") + 1);
        if (dragonEntity.getKiller() == null) return;

        // Update killer leaderboard
        final String killerName = dragonEntity.getKiller().getName();
        final String path = "dragonKillers." + killerName;
        Main.getStatisticsManager().incrementStatInt(path, 1);

        // Set top dragon killer
        MemorySection killersSection = null;

        try {
            killersSection = ((MemorySection) Main.getStatisticsManager().getStatObject("dragonKillers"));
        } catch (ClassCastException e1) {
            return;
        }

        String topKillerName = killerName;
        int topKills = Main.getStatisticsManager().getStatInt("topDragonKillerKills");
        for (Map.Entry<String, Object> playerAndKills : killersSection.getValues(false).entrySet()) {
            int kills;
            try {
                kills = (int) playerAndKills.getValue();
            } catch (ClassCastException e2) {
                Util.logWarning("Could not get " + path);
                continue;
            }
            if (kills >= topKills) {
                topKills = kills;
                topKillerName = playerAndKills.getKey();
            }
        }

        Main.getStatisticsManager().setStat("topDragonKillerName", topKillerName);
        Main.getStatisticsManager().setStat("topDragonKillerKills", topKills);
    }

    private void giveXP(LivingEntity dragonEntity, World theEnd) {
        // For every player in the End...
        final List<Player> players = Util.getPlayersInEndCentreRadius(theEnd, playerRadius);
        for (Player player : players) {
            int xpToGive = 0;
            switch (xpDistribution) {
                default:
                case "equal":
                    xpToGive = xpAmount;
                    break;
                case "split":
                    xpAmount = (int) Math.floor(xpAmount / players.size());
                    break;
                case "killer-bias":
                    xpToGive = player == dragonEntity.getKiller() ? xpAmount * 2 : xpAmount;
                    break;
                case "killer-only":
                    if (player == dragonEntity.getKiller()) xpToGive = xpAmount;
                    else continue;
                    break;
            }

            // Give player XP
            switch (xpInterpretation) {
                default:
                case "levels":
                    player.giveExpLevels(xpToGive);
                    break;
                case "points":
                    player.giveExp(xpToGive);
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
        Main.getStatisticsManager().incrementStatInt("totalXPGained", players.size() * xpAmount);
    }

    private void spawnEgg(World theEnd) {
        // The game automatically spawns an Egg when the Dragon is first killed
        // This plugin shouldn't spawn another one
        if (!theEnd.getEnderDragonBattle().hasBeenPreviouslyKilled()) {
            Main.getStatisticsManager().incrementStatInt("eggsSpawned", 1);
            return;
        };
        if (RANDOM.nextDouble() > eggRespawnChance) return;

        Location eggLocation = findSpawnEggLocation(theEnd);
        if (eggLocation == null) {
            Util.logWarning("Unable to find a suitable position for the Dragon Egg");
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
        Main.getStatisticsManager().incrementStatInt("eggsSpawned", 1);
        if (eggRespawnAnnouncement != null && eggRespawnAnnouncement.length() > 0)  {
            Bukkit.broadcastMessage(
                new ConfigStringParser()
                    .addPlaceholder("<position>", Util.formatCoordinates(eggLocation))
                    .parse(eggRespawnAnnouncement)
            );
        }
    }

    private void sendDefeatAnnouncement(Player killer, EntityDamageEvent damage, World theEnd) {
        final String killerName = Util.getKillerName(killer, damage, true);
        final List<String> fightParticipantNames = Util.getPlayersInEndCentreRadius(
            theEnd,
            playerRadius
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
            Util.formatParticipantsList(participantDisplayNames) : Main.getConfigManager().FEATURE_CUSTOM_COMMANDS.getString("no-participants-filler");

        Util.logInfo("Executing " + commandsList.size() + " command(s)");

        final ConfigStringParser cmdParser = new ConfigStringParser()
            .addPlaceholder("<killer>", killerName)
            .addPlaceholder("<killer-display-name>", killerDisplayName)
            .addPlaceholder("<participants-list>", participantsString);

        for (String command : commandsList) {
            final String cmd = cmdParser.parse(command);
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
            theEnd.getEnderDragonBattle().getEndPortalLocation().getY(),
            configuredEggLocationAsVector.getBlockZ()
        );

        if (overrideEggY) {
            searchLocation.setY(configuredEggLocationAsVector.getBlockY());
            return searchLocation;
        }

        final int startY = searchLocation.getBlockY();
        final int worldHeight = theEnd.getMaxHeight();
        // Search through the Y value of (0, 0) to find a place for the egg
        for (int i = startY; i < worldHeight; i++) {
            searchLocation.setY(i);
            if (searchLocation.getBlock().getType().isAir()) {
                return searchLocation;
            }
        }
        // Unable to find anywhere for the Egg to go
        return null;
    }

    @Override
    public boolean shouldRegisterListener() {
        return doGiveXP || doSpawnEgg || doDefeatAnnouncement || doCommands
            || (Main.getConfigManager().FEATURE_DRAGON_RESPAWN_COOLDOWN.isEnabled()
                && Main.getConfigManager().FEATURE_DRAGON_RESPAWN_COOLDOWN.getInt("cooldown") > 0)
            || Main.getConfigManager().FEATURE_STATISTICS.isEnabled();
    }

    @Override
    public Map<String, Object> getStatisticsDefaults() {
        final Map<String, Object> defaults = new HashMap<String, Object>();
        defaults.put("dragonDeathCount", 0);
        defaults.put("topDragonKillerName", "");
        defaults.put("topDragonKillerKills", 0);
        defaults.put("dragonKillers", null);
        defaults.put("totalXPGained", 0);
        defaults.put("eggsSpawned", 0);
        return defaults;
    }
}