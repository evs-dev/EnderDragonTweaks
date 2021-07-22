package me.EvsDev.EnderDragonTweaks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ConfigManager {

    public static final String entry_enabled = "enabled";
    public static final String entry_delay = "delay";
    public static final String entry_playerDistanceFromOrigin = "max-player-distance-from-end-centre";
    public static final String entry_dragonRespawnCooldown = "dragon-respawn-cooldown";
    public static final String entry_dragonRespawnCooldownEnterAnnouncement = "enter-respawn-cooldown-announcement";
    public static final String entry_dragonRespawnCooldownLeaveAnnouncement = "leave-respawn-cooldown-announcement";
    public static final String entry_dragonRespawnCooldownWarning = "respawn-cooldown-warning";
    public static final String entry_xpMode = "xp-mode";
    public static final String entry_xpPerPlayer = "xp-per-player";
    public static final String entry_orbCountPerPlayer = "orb-count-per-player";
    public static final String entry_defeatMessageOneParticipant = "defeat-announcement-message-one-participant";
    public static final String entry_defeatMessageMultipleParticipants = "defeat-announcement-message-multiple-participants";
    public static final String entry_commandsList = "commands";
    public static final String entry_commandsNoParticipantsFiller = "commands-no-participants-filler";
    public static final String entry_enableXP = "enable-xp-drop";
    public static final String entry_enableDecorationOrbs = "enable-decoration-orbs";
    public static final String entry_enableEggRespawn = "enable-egg-respawn";
    public static final String entry_enableDefeatAnnouncement = "enable-defeat-announcement";
    public static final String entry_enableCommands = "enable-commands";

    public static final String entry_eggPositionSection = "egg-position.";
    public static final String entry_overrideEggY = entry_eggPositionSection + "override-y";

    // Increment this when updating the default config in any way
    // NOTE: The value of the version key in the config should ALWAYS BE 0 to allow the below code to work
    private static final int CONFIG_VERSION = 3;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        // Check if the config file is present before the default config is saved to disk
        final File configFile = new File(plugin.getDataFolder(), "config.yml");
        final boolean configFileExisted = configFile.exists();

        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();

        final int loadedConfigVersion = this.config.getInt("version");
        if (loadedConfigVersion != CONFIG_VERSION) {
            if (configFileExisted) {
                // The loaded config is the wrong version (the version number is different to current or doesn't exist)
                Util.logWarning(String.format(
                    "Your EnderDragonTweaks config is an old/unexpected version (config v%s)!"
                    + " Delete/rename it and restart the server to generate a new one (config v%s).",
                    loadedConfigVersion, CONFIG_VERSION)
                );
            } else {
                // A new config has just been generated, so the version needs to be set to current to
                // avoid future warnings. This is done because the Spigot config API is hopeless
                final Path path = configFile.toPath();
                final Charset charset = StandardCharsets.UTF_8;
                String content;
                try {
                    content = new String(Files.readAllBytes(path), charset);
                    content = content.replace("version: " + loadedConfigVersion, "version: " + CONFIG_VERSION);
                    Files.write(path, content.getBytes(charset));
                } catch (IOException e) {
                    // Something weird has happened
                }
            }
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

    public List<String> getStringList(String name) {
        return this.config.getStringList(name);
    }

    public Vector getEggLocationAsVector() {
        return new Vector(
            this.getInt(entry_eggPositionSection + "x"),
            this.getInt(entry_eggPositionSection + "y"),
            this.getInt(entry_eggPositionSection + "z")
        );
    }

}
