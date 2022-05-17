package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderReplacer {

    final Map<String, String> placeholders;
    // e.g. <stat-wr.ty>
    final Pattern statPlaceholderPattern = Pattern.compile("<stat\\-([a-zA-Z][[\\w+]|[\\.*]]+)>");

    public PlaceholderReplacer() {
        placeholders = new HashMap<>();
    }

    public PlaceholderReplacer add(String placeholder, String with) {
        placeholders.put(placeholder, with);
        return this;
    }

    public String replaceIn(String string) {
        // Replace added this
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue());
        }
        // Replace all <stat-statName>
        final Matcher statPlaceholderMatcher = statPlaceholderPattern.matcher(string);
        while (statPlaceholderMatcher.find()) {
            final String statPlaceholder = statPlaceholderMatcher.group(0);
            final String statPath = statPlaceholderMatcher.group(1);
            Object statValue = Main.getStatisticsManager().getStatObject(statPath);
            if (statValue == null) statValue = "";
            string = string.replaceAll(statPlaceholder, statValue.toString());
        }
        return string;
    }

}
