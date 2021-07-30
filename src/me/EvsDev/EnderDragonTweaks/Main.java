package me.EvsDev.EnderDragonTweaks;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String LOG_PREFIX = "[EnderDragonTweaks] ";
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        if (!configManager.MAIN_SECTION.isEnabled()) return;

        final AbstractEnderDragonTweaksListener[] listeners = {
            new EnderDragonDeathListener(),
            new EndCrystalPlacedListener(),
            new EnderDragonChangePhaseListener(),
        };

        final PluginManager pluginManager = getServer().getPluginManager();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i].shouldRegisterListener()) {
                pluginManager.registerEvents(listeners[i], this);
                Util.logInfo("Enabling " + listeners[i].getClass().getSimpleName());
            }
        }
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

}
