package me.EvsDev.EnderDragonTweaks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {

    public final ConfigSection MAIN_SECTION = new ConfigSection(null);
    public final ConfigSection FEATURE_DRAGON_ENHANCEMENTS = new ConfigSection("dragon-enhancements");
    public final ConfigSection FEATURE_XP_DROP = new ConfigSection("xp-drop");
    public final ConfigSection FEATURE_DECORATION_ORBS = new ConfigSection("decoration-orbs");
    public final ConfigSection FEATURE_EGG_RESPAWN = new ConfigSection("egg-respawn");
    public final ConfigSection FEATURE_DEFEAT_ANNOUNCEMENT = new ConfigSection("defeat-announcement");
    public final ConfigSection FEATURE_CUSTOM_COMMANDS = new ConfigSection("custom-commands");
    public final ConfigSection FEATURE_BOSSBAR_CUSTOMISATION = new ConfigSection("bossbar-customisation");
    public final ConfigSection FEATURE_DRAGON_RESPAWN_COOLDOWN = new ConfigSection("dragon-respawn-cooldown");
    public final ConfigSection FEATURE_STATISTICS = new ConfigSection("statistics");

    // Increment this when updating the default config in any way
    // NOTE: The value of the version key in the config should ALWAYS BE 0 to allow the below code to work
    private static final int CONFIG_VERSION = 5;
    private final FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        // Check if the config file is present before the default config is saved to disk
        final File configFile = new File(plugin.getDataFolder(), "config.yml");
        final boolean configFileExisted = configFile.exists();

        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();

        final int loadedConfigVersion = this.config.getInt("version");
        checkConfigVersion(configFile, configFileExisted, loadedConfigVersion, CONFIG_VERSION, "config");
    }

    public static void checkConfigVersion(File configFile, boolean configFileExisted, int loadedConfigVersion, int targetConfigVersion, String noun) {
        if (loadedConfigVersion != targetConfigVersion) {
            if (configFileExisted) {
                // The loaded config is the wrong version (the version number is different to current or doesn't exist)
                Util.logWarning(String.format(
                    "Your EnderDragonTweaks config is an old/unexpected version (config v%s)!"
                    + " Delete/rename it and restart the server to generate a new one (config v%s)."
                    + " (Not updating may result in default config values being used instead of your custom ones!)",
                    loadedConfigVersion, targetConfigVersion).replaceAll("config", noun)
                );
            } else {
                // A new config has just been generated, so the version needs to be set to current to
                // avoid future warnings. This is done because the Spigot config API is hopeless
                final Path path = configFile.toPath();
                final Charset charset = StandardCharsets.UTF_8;
                String content;
                try {
                    content = new String(Files.readAllBytes(path), charset);
                    content = content.replace("version: " + loadedConfigVersion, "version: " + targetConfigVersion);
                    Files.write(path, content.getBytes(charset));
                } catch (IOException e) {
                    // Something weird has happened
                }
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
