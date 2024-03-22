package me.EvsDev.EnderDragonTweaks;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private final JavaPlugin plugin;
    private String newVersion;

    public UpdateChecker(int resourceId) {
        plugin = Main.getPlugin(Main.class);
        check(resourceId);
    }

    // From https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
    private void getVersion(int resourceId, final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId + "/~").openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                Util.logWarning("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    private void check(int resourceId) {
        getVersion(resourceId, version -> {
            // Remove the "v" prefix
            version = version.substring(1);
            if (!plugin.getDescription().getVersion().equals(version)) {
                newVersion = version;
                Util.logWarning("A new version of EnderDragonTweaks is available: " + newVersion);
            }
        });
    }
}