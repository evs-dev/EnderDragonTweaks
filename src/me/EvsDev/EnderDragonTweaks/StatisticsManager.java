package me.EvsDev.EnderDragonTweaks;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StatisticsManager {

    private final File statsFile;
    private final FileConfiguration statsConfig;
    private boolean couldNotLoad = false;
    private boolean isEnabled = false;

    public StatisticsManager() {
        isEnabled = Main.getConfigManager().FEATURE_STATISTICS.isEnabled();
        if (isEnabled) Util.logInfo("Enabling statistics");

        statsFile = new File(Main.getPlugin(Main.class).getDataFolder(), "statistics.yml");
        boolean statsFileExisted = statsFile.exists();
        if (!statsFileExisted) {
            Main.getPlugin(Main.class).saveResource("statistics.yml", false);
            Util.logInfo("Created statistics.yml");
        }

        statsConfig = new YamlConfiguration();
        try {
            statsConfig.load(statsFile);
        } catch (IOException | InvalidConfigurationException e) {
            //e.printStackTrace();
            Util.logWarning("Could not load statistics.yml");
            couldNotLoad = true;
        }

        if (!couldNotLoad) {
            ConfigManager.checkConfigVersion(statsFile, statsFileExisted, getStatInt("version"), 1, "statistics file");
        }
    }

    public void setStat(String statName, Object value) {
        if (!isEnabled || couldNotLoad) return;
        statsConfig.set(statName, value);
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            //e.printStackTrace();
            Util.logWarning("Could not save statistics.yml");
        }
    }

    public int getStatInt(String statName) {
        return statsConfig.getInt(statName);
    }

    public String getStatString(String statName) {
        return statsConfig.getString(statName);
    }

    public Object getStatObject(String statName) {
        return statsConfig.get(statName);
    }

    public boolean isSet(String statName) {
        return statsConfig.isSet(statName);
    }

}
