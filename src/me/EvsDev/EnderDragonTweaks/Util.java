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

    public static String formatDefeatAnnouncementMessage(String killerName, boolean killerIsPlayer, List<String> fightParticipantNames, ConfigManager config) {
        if (fightParticipantNames == null || fightParticipantNames.size() == 0) return null;
        final int numFightParticipants = fightParticipantNames.size();

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

        final String message = ChatColor.translateAlternateColorCodes(
            '&',
            config.getString(
                multipleParticipants ? ConfigManager.entry_defeatMessageMultipleParticipants : ConfigManager.entry_defeatMessageOneParticipant
            )
        );

        String helpingParticipants = "";
        if (message.contains("<participants>")) {
            final int minNumPlayers = killerIsPlayer ? 1 : 0;

            if (numFightParticipants > minNumPlayers) {
                int count = 0;
                for (String name : fightParticipantNames) {
                    count++;
                    if (!name.equals(killerName)) {
                        helpingParticipants += name;
                        if (count < numFightParticipants) {
                            if (numFightParticipants == (killerIsPlayer ? 3 : 2)) {
                                // Between 2 "helping" players
                                helpingParticipants += " & ";
                            } else if (count == numFightParticipants - 1) {
                                // Just before last player of 3+ "helping" players
                                helpingParticipants += ", & ";
                            } else {
                                // Between 3+ "helping" players (expect penultimate and final)
                                helpingParticipants += ", ";
                            }
                        }
                    }
                }
            } else {
                helpingParticipants += "no-one";
            }
        }

        return message
            .replace("<killer>", killerName)
            .replace("<participants>", helpingParticipants);
    }

    public static void logInfo(String message) {
        Bukkit.getLogger().info(Main.getLogPrefix() + message);
    }

    public static void logWarning(String message) {
        Bukkit.getLogger().warning(Main.getLogPrefix() + message);
    }

}
