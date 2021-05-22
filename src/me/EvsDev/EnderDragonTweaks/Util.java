package me.EvsDev.EnderDragonTweaks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Util {

    public static List<Player> getPlayersInEndCentreRadius(World theEnd, int radius) {
        final Location endCentre = new Location(theEnd, 0, 65, 0);
        return theEnd.getPlayers().stream()
            .filter(p -> p.getLocation().distance(endCentre) <= radius)
            .collect(Collectors.toList());
    }

    public static String formatDefeatAnnouncementMessage(Player killer, World theEnd, ConfigManager config) {
        final String message = ChatColor.translateAlternateColorCodes('&', config.getString(ConfigManager.entry_defeatMessage));
        int playerRadius = config.getInt(ConfigManager.entry_playerDistanceFromOrigin);
        final String killerName = killer != null ? killer.getDisplayName() : "<UNKNOWN>";

        String playersInEnd = "";
        if (message.contains("<players-in-end>")) {
            final List<Player> players = Util.getPlayersInEndCentreRadius(theEnd, playerRadius);
            int numPlayers = players.size();
            int minNumPlayers = killer == null ? 0 : 1;

            if (players != null && numPlayers > minNumPlayers) {
                int count = 0;
                for (Player player : players) {
                    final String name = player.getDisplayName();
                    count++;
                    if (!name.equals(killerName)) {
                        playersInEnd += name;
                        if (count < numPlayers)
                            playersInEnd += ", ";
                    }
                }
            } else {
                playersInEnd = "no-one";
            }
        }

        return message
            .replace("<killer>", killerName)
            .replace("<players-in-end>", playersInEnd);
    }

    public static void logInfo(String message) {
        Bukkit.getLogger().info(Main.getLogPrefix() + message);
    }

    public static void logWarning(String message) {
        Bukkit.getLogger().warning(Main.getLogPrefix() + message);
    }

}
