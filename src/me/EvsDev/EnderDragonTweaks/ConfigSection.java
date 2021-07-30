package me.EvsDev.EnderDragonTweaks;

import java.util.Arrays;
import java.util.List;

public class ConfigSection {

    private final String configSectionHeader;

    public ConfigSection(String configSectionName) {
        this.configSectionHeader = configSectionName != null && configSectionName.length() > 0 ? configSectionName + "." : "";
    }

    public boolean isEnabled() {
        return getBoolean("enabled");
    }

    private Object getValue(String name, boolean acceptNull) {
        final String path = configSectionHeader + name;
        final Object value = Main.getConfigManager().getConfig().get(path);
        if (!acceptNull && value == null) {
            Util.logWarning("Non-existent or null config path " + path + " was accessed (this might be OK)");
            return 0;
        }
        //Util.logWarning(path + " = " + value);
        return value;
    }

    private Object getValue(String name) {
        return getValue(name, false);
    }

    public boolean getBoolean(String name) {
        try {
            return (boolean) getValue(name);
        } catch (ClassCastException e) {
            Util.logWarning("Config value " + name + ": " + e.getMessage());
            return false;
        }
    }

    public int getInt(String name) {
        return (int) getValue(name);
    }

    public double getDouble(String name) {
        return (double) getValue(name);
    }

    public String getString(String name) {
        return getValue(name).toString();
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String name) {
        final Object value = getValue(name, true);
        if (value == null || !(value instanceof List<?>)) {
            return Arrays.asList();
        }
        try {
            return (List<String>) value;
        } catch (ClassCastException e) {
            return Arrays.asList();
        }
    }

}
