package me.EvsDev.EnderDragonTweaks;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static String logPrefix = "[plugin-name] ";
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        logPrefix = "[" + this.getName() + "] ";

        configManager = new ConfigManager(this);
        if (!configManager.getBoolean(ConfigManager.entry_enabled)) return;

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new EnderDragonDeathListener(), this);
    }

    public static String getLogPrefix() {
        return logPrefix;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

}
