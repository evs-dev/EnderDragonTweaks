package me.EvsDev.EnderDragonTweaks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Util {

    public static List<Player> getPlayersInEndCentreRadius(World theEnd, int radius) {
        final Location endCentre = new Location(theEnd, 0, 65, 0);
        return theEnd.getPlayers().stream()
            .filter(p -> p.getLocation().distance(endCentre) <= radius)
            .collect(Collectors.toList());
    }

    public static void logInfo(String message) {
        Bukkit.getLogger().info(Main.getLogPrefix() + message);
    }

    public static void logWarning(String message) {
        Bukkit.getLogger().warning(Main.getLogPrefix() + message);
    }

}
