package me.EvsDev.EnderDragonTweaks;

import java.util.List;
import java.util.stream.Collectors;

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

}
