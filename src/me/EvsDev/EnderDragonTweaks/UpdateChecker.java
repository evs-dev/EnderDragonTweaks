package me.EvsDev.EnderDragonTweaks;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    public static void check(int resourceId) {
        getVersion(resourceId, version -> {
            version = version.substring(1); // Remove the "v" prefix
            if (!Main.getPlugin(Main.class).getDescription().getVersion().equals(version)) {
                Util.logWarning("A new version of EnderDragonTweaks is available: " + version);
            }
        });
    }

    // From https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
    private static void getVersion(int resourceId, final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId + "/~").openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                Util.logWarning("Unable to check for updates: " + e.getMessage());
            }
        });
    }
}