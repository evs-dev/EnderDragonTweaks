package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String LOG_PREFIX = "[EnderDragonTweaks] ";
    private static ConfigManager configManager;
    private static StatisticsManager statisticsManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        if (!configManager.MAIN_SECTION.isEnabled()) return;

        statisticsManager = new StatisticsManager();

        //setupCommand("respawndragon", new RespawnDragonCommand(), null);

        final AbstractEnderDragonTweaksListener[] listeners = {
            new EnderDragonDeathListener(),
            new EndCrystalPlacedListener(),
            new EnderDragonChangePhaseListener(),
            new DragonHealthModifier(),
            new DragonDamageModifier(),
        };

        final PluginManager pluginManager = getServer().getPluginManager();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i].shouldRegisterListener()) {
                pluginManager.registerEvents(listeners[i], this);
                Util.logInfo("Enabling " + listeners[i].getClass().getSimpleName());

                final Map<String, Object> statisticsDefaults = listeners[i].getStatisticsDefaults();
                for (Map.Entry<String, Object> entry : statisticsDefaults.entrySet()) {
                    if (statisticsManager.isSet(entry.getKey())) continue;
                    statisticsManager.setStat(entry.getKey(), entry.getValue());
                }
            }
        }

        // bStats metrics
        final Metrics metrics = new Metrics(this, 12284);

        if (configManager.FEATURE_XP_DROP.isEnabled()) {
            metrics.addCustomChart(new Metrics.SimplePie("xp_drop_mode", () -> {
                return configManager.FEATURE_XP_DROP.getString("mode").toLowerCase();
            }));
        }

        if (configManager.FEATURE_EGG_RESPAWN.isEnabled()) {
            metrics.addCustomChart(new Metrics.SimplePie("egg_respawn_chance", () -> {
                return configManager.FEATURE_EGG_RESPAWN.getDouble("chance") >= 1d ? "100%" : "<100%";
            }));
        }

        if (configManager.FEATURE_BOSSBAR_CUSTOMISATION.isEnabled()) {
            metrics.addCustomChart(new Metrics.SimplePie("bossbar_colour", () -> {
                return configManager.FEATURE_BOSSBAR_CUSTOMISATION.getString("colour").toLowerCase();
            }));

            metrics.addCustomChart(new Metrics.SimplePie("bossbar_style", () -> {
                return configManager.FEATURE_BOSSBAR_CUSTOMISATION.getString("style").toLowerCase();
            }));
        }

        metrics.addCustomChart(new Metrics.SimplePie("custom_commands_enabled", () -> {
            return configManager.FEATURE_CUSTOM_COMMANDS.isEnabled() ? "enabled" : "disabled";
        }));

        metrics.addCustomChart(new Metrics.SimplePie("dragon_respawn_cooldown_enabled", () -> {
            return configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN.isEnabled() ? "enabled" : "disabled";
        }));

        metrics.addCustomChart(new Metrics.SimplePie("statistics_enabled", () -> {
            return configManager.FEATURE_STATISTICS.isEnabled() ? "enabled" : "disabled";
        }));

        final Map<String, ConfigSection> featuresToChart = new HashMap<String, ConfigSection>();
        featuresToChart.put("XP Drop", configManager.FEATURE_XP_DROP);
        featuresToChart.put("Decoration Orbs", configManager.FEATURE_DECORATION_ORBS);
        featuresToChart.put("Egg Respawn", configManager.FEATURE_EGG_RESPAWN);
        featuresToChart.put("Defeat Announcement", configManager.FEATURE_DEFEAT_ANNOUNCEMENT);
        featuresToChart.put("Custom Commands", configManager.FEATURE_CUSTOM_COMMANDS);
        featuresToChart.put("Bossbar Customisation", configManager.FEATURE_BOSSBAR_CUSTOMISATION);
        featuresToChart.put("Dragon Respawn Cooldown", configManager.FEATURE_DRAGON_RESPAWN_COOLDOWN);
        featuresToChart.put("Statistics", configManager.FEATURE_STATISTICS);

        metrics.addCustomChart(new Metrics.SimpleBarChart("features_popularity", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                final Map<String, Integer> map = new HashMap<>();
                for (Map.Entry<String, ConfigSection> entry : featuresToChart.entrySet()) {
                    map.put(entry.getKey(), entry.getValue().isEnabled() ? 1 : 0);
                }
                return map;
            }
        }));

    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    @SuppressWarnings("unused")
    private void setupCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        final PluginCommand command = getCommand(name);
        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);
    }

}
