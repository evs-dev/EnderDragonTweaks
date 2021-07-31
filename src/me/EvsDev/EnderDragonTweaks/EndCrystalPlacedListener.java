package me.EvsDev.EnderDragonTweaks;

import java.time.Instant;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;

public class EndCrystalPlacedListener extends AbstractEnderDragonTweaksListener {

    private static boolean isInCooldown;
    private static BukkitTask cooldownTask;
    private static long cooldownStartEpochSeconds;
    private static boolean doRespawnCooldown;
    private static int respawnCooldownTicks;
    private static int respawnCooldownSeconds;
    private static String enterCooldownMessage;
    private static String leaveCooldownMessage;
    private static String cooldownWarningMessage;

    public EndCrystalPlacedListener() {
        final ConfigManager configManager = Main.getConfigManager();
        doRespawnCooldown = configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN.isEnabled();
        if (doRespawnCooldown) {
            respawnCooldownTicks = configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN.getInt("cooldown");
            respawnCooldownSeconds = respawnCooldownTicks / 20; // 20 ticks in 1 second
            enterCooldownMessage = configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN.getString("enter-announcement");
            leaveCooldownMessage = configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN.getString("leave-announcement");
            cooldownWarningMessage = configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN.getString("warning");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEndCrystalPlaced(PlayerInteractEvent e) {
        // Check whether the player placed an End Crystal on the End Portal Bedrock (as if going to respawn the Dragon),
        // and cancel the action if so and if the respawn cooldown is active
        if (e.getPlayer().getWorld().getEnvironment() != Environment.THE_END || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final boolean inRadiusOfPortal = e.getPlayer().getWorld().getEnderDragonBattle().getEndPortalLocation()
            .distanceSquared(e.getClickedBlock().getLocation()) < 16; // r=4; r*r=16

        if (inRadiusOfPortal && e.getMaterial() == Material.END_CRYSTAL && e.getClickedBlock().getType() == Material.BEDROCK) {
            if (isInCooldown) {
                e.setCancelled(true);
                final long secondsSinceStartedCooldown = Instant.now().getEpochSecond() - cooldownStartEpochSeconds;
                final long timeLeftInCooldown = respawnCooldownSeconds - secondsSinceStartedCooldown;
                Util.logInfo("The Dragon respawn was cancelled because it's in cooldown. Time left: " + timeLeftInCooldown + " seconds");
                if (cooldownWarningMessage != null && cooldownWarningMessage.length() > 0) {
                    e.getPlayer().sendMessage(
                        ChatColor.translateAlternateColorCodes('&', cooldownWarningMessage)
                            .replace("<time-remaining>", Long.toString(timeLeftInCooldown))
                    );
                }
            }
        }
    }

    public static void startCooldown() {
        if (cooldownTask != null && !cooldownTask.isCancelled()) {
            cooldownTask.cancel();
        }

        if (respawnCooldownTicks == 0) {
            isInCooldown = false;
        } else {
            cooldownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    isInCooldown = false;
                    broadcastMessage(leaveCooldownMessage.replace("<cooldown-length>", Integer.toString(respawnCooldownSeconds)));
                }
            }.runTaskLater(Main.getPlugin(Main.class), respawnCooldownTicks);
            isInCooldown = true;
            cooldownStartEpochSeconds = Instant.now().getEpochSecond();
            broadcastMessage(enterCooldownMessage.replace("<time-remaining>", Integer.toString(respawnCooldownSeconds)));
        }
    }

    @Override
    public boolean shouldRegisterListener() {
        return doRespawnCooldown && respawnCooldownTicks > 0;
    }

    private static void broadcastMessage(String rawMessage) {
        if (rawMessage != null && rawMessage.length() > 0)
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', rawMessage));
    }

}