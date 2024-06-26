package me.EvsDev.EnderDragonTweaks;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Util {

    public static List<Player> getPlayersInEndCentreRadius(World theEnd, int radius) {
        final Location endCentre = new Location(theEnd, 0, 65, 0);
        radius = Math.min(radius, 46000); // Cap radius so the square won't go beyond Integer.MAX_VALUE
        final int radiusSqrd = radius * radius;
        return theEnd.getPlayers().stream()
            .filter(p -> p.getLocation().distanceSquared(endCentre) <= radiusSqrd)
            .collect(Collectors.toList());
    }

    public static String formatDefeatAnnouncementMessage(String killerName, boolean killerIsPlayer, List<String> fightParticipantNames, ConfigManager config) {
        if (fightParticipantNames == null || fightParticipantNames.size() == 0) return null;
        int numFightParticipants = fightParticipantNames.size();

        boolean multipleParticipants;
        if (numFightParticipants == 1 && !killerIsPlayer) {
            // One player killed the Dragon with e.g. a bed, so ideally they should be credited and not the bed
            // With multiple players it is impossible/too challenging to determine who blew up the bed (oh well)
            multipleParticipants = false;
            killerName = fightParticipantNames.get(0);
            killerIsPlayer = true;
        } else {
            multipleParticipants = numFightParticipants > 1 || !killerIsPlayer;
        }

        final String message = config.FEATURE_DEFEAT_ANNOUNCEMENT.getString(
            multipleParticipants ? "multiple-participants" : "one-participant"
        );

        String helpingParticipants = "";
        if (message.contains("<participants>")) {
            try {
                fightParticipantNames.remove(killerName);
            } catch (UnsupportedOperationException e) {
                // An unmodifiable list was passed to fightParticipantNames (e.g. Arrays.asList(...))
            }
            numFightParticipants--;

            if (numFightParticipants > 0) {
                helpingParticipants = formatParticipantsList(fightParticipantNames);
            } else {
                helpingParticipants += "no-one";
            }
        }

        return new ConfigStringParser()
            .addPlaceholder("<killer>", killerName)
            .addPlaceholder("<participants>", helpingParticipants)
            .parse(message);
    }

    public static String formatParticipantsList(List<String> list) {
        int count = 0;
        String output = "";
        final int length = list.size();
        for (String element : list) {
            count++;
            output += element;
            if (count < length) {
                if (length == 2) {
                    // Between 2 players
                    output += " & ";
                } else if (count == length - 1) {
                    // Just before last player of 3+ players
                    output += ", & ";
                } else {
                    // Between 3+ players (except penultimate and final)
                    output += ", ";
                }
            }
        }
        return output;
    }

    public static String getKillerName(Player killer, EntityDamageEvent damage, boolean displayName) {
        if (killer == null) {
            if (damage == null) return "unknown damage source";
            return damage.getCause().toString().replace('_', ' ');
        }
        return displayName ? killer.getDisplayName() : killer.getName();
    }

    public static String formatCoordinates(Location location) {
        return String.format("x=%s y=%s z=%s", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String formatSecondsToHHMMSS(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long remainingSeconds = duration.getSeconds() % 60;

        if (hours <= 0 && minutes <= 0) {
            return String.format(remainingSeconds < 10 ? "%s seconds" : "%02d seconds", remainingSeconds);
        } else if (hours <= 0) {
            return String.format("%02d:%02d", minutes, remainingSeconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        }
    }

    public static void logInfo(String message) {
        Bukkit.getLogger().info(Main.LOG_PREFIX + message);
    }

    public static void logWarning(String message) {
        Bukkit.getLogger().warning(Main.LOG_PREFIX + message);
    }

}
