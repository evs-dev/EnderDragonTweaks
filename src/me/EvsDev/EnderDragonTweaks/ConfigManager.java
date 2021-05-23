package me.EvsDev.EnderDragonTweaks;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ConfigManager {

    public static final String entry_enabled = "enabled";
    public static final String entry_delay = "delay";
    public static final String entry_playerDistanceFromOrigin = "max-player-distance-from-end-centre";
    public static final String entry_xpMode = "xp-mode";
    public static final String entry_xpPerPlayer = "xp-per-player";
    public static final String entry_orbCountPerPlayer = "orb-count-per-player";
    public static final String entry_defeatMessage = "defeat-announcement-message";
    public static final String entry_enableXP = "enable-xp-drop";
    public static final String entry_enableDecorationOrbs = "enable-decoration-orbs";
    public static final String entry_enableEggRespawn = "enable-egg-respawn";
    public static final String entry_enableDefeatAnnouncement = "enable-defeat-announcement";

    public static final String entry_eggPositionSection = "egg-position.";
    public static final String entry_overrideEggY = entry_eggPositionSection + "override-y";

    private final FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
        if (this.getInt("xp-points-per-player") != 0) {
            Util.logWarning("Your EnderDragonTweaks config is old! Delete/rename it and restart the server to generate a new one.");
        }
    }

    public boolean getBoolean(String name) {
        return this.config.getBoolean(name);
    }

    public int getInt(String name) {
        return this.config.getInt(name);
    }

    public String getString(String name) {
        return this.config.getString(name);
    }

    public Vector getEggLocationAsVector() {
        return new Vector(
            this.getInt(entry_eggPositionSection + "x"),
            this.getInt(entry_eggPositionSection + "y"),
            this.getInt(entry_eggPositionSection + "z")
        );
    }

}
