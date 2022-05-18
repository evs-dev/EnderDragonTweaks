package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class ConfigStringParser {

    private final Map<String, String> placeholders;
    // e.g. <stat-wr.ty>
    private static final Pattern STAT_PLACEHOLDER_PATTERN = Pattern.compile("<stat\\-([a-zA-Z][[\\w+]|[\\.*]]+)>");

    public ConfigStringParser() {
        placeholders = new HashMap<>();
    }

    public ConfigStringParser addPlaceholder(String placeholder, String with) {
        placeholders.put(placeholder, with);
        return this;
    }

    public String parse(String string) {
        // Replace added this
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue());
        }
        // Replace all <stat-statName>
        final Matcher statPlaceholderMatcher = STAT_PLACEHOLDER_PATTERN.matcher(string);
        while (statPlaceholderMatcher.find()) {
            final String statPlaceholder = statPlaceholderMatcher.group(0);
            final String statPath = statPlaceholderMatcher.group(1);
            Object statValue = Main.getStatisticsManager().getStatObject(statPath);
            if (statValue == null) statValue = "";
            string = string.replaceAll(statPlaceholder, statValue.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
