package me.EvsDev.EnderDragonTweaks;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String LOG_PREFIX = "[EnderDragonTweaks] ";
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        if (!configManager.getBoolean(ConfigManager.entry_enabled)) return;

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new EnderDragonDeathListener(), this);
        final EndCrystalPlacedListener respawnListener = new EndCrystalPlacedListener();
        if (respawnListener.shouldRegisterListener())
            pluginManager.registerEvents(respawnListener, this);
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

}
