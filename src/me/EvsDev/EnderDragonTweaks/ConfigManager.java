package me.EvsDev.EnderDragonTweaks;

import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class ConfigManager {

	public static final String entry_enabled = "enabled";
	public static final String entry_delay = "delay";
	public static final String entry_playerDistanceFromOrigin = "max-player-distance-from-end-centre";
	public static final String entry_xpPointsPerPlayer = "xp-points-per-player";
	public static final String entry_orbCountPerPlayer = "orb-count-per-player";
	public static final String entry_defeatMessage = "defeat-announcement-message";
	public static final String entry_enableXP = "enable-xp-drop";
	public static final String entry_enableDecorationOrbs = "enable-decoration-orbs";
	public static final String entry_enableEggRespawn = "enable-egg-respawn";
	public static final String entry_enableDefeatAnnouncement = "enable-defeat-announcement";

	public static final String entry_eggPositionSection = "egg-position.";
	public static final String entry_overrideEggY = entry_eggPositionSection + "override-y";

	private FileConfiguration config;

	public ConfigManager(Plugin plugin) {
		this.config = plugin.getConfig();
		plugin.saveDefaultConfig();
	}

	public boolean getBoolean(String name) {
		return this.config.getBoolean(name);
	}

	public int getInt(String name) {
		return this.config.getInt(name);
	}

	public String getString(String name) {
		return this.config.getString(name);
	}

	public String formatDefeatAnnouncementMessage(Player killer, World theEnd) {
		String message = ChatColor.translateAlternateColorCodes('&', this.getString(entry_defeatMessage));
		int playerRadius = getInt(entry_playerDistanceFromOrigin);
		String killerName = "<UNKNOWN>";
		if (killer != null)
			killerName = killer.getDisplayName();

		String playersInEnd = "";
		if (message.contains("<players-in-end>")) {
			final List<Player> players = Util.getPlayersInEndCentreRadius(theEnd, playerRadius);
			int minSize = killer == null ? 0 : 1;
			if (players != null && players.size() > minSize) {
				int count = 0;
				for (Player player : players) {
					String name = player.getDisplayName();
					count++;
					if (name.equals(killerName)) continue;
					playersInEnd += name;
					if (count < players.size())
						playersInEnd += ", ";
				}
			} else {
				playersInEnd = "no-one";
			}
		}

		return message
			.replace("<killer>", killerName)
			.replace("<players-in-end>", playersInEnd);
	}

	public Vector getEggLocationAsVector() {
		return new Vector(
			this.getInt(entry_eggPositionSection + "x"),
			this.getInt(entry_eggPositionSection + "y"),
			this.getInt(entry_eggPositionSection + "z")
		);
	}

}
