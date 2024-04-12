package me.EvsDev.EnderDragonTweaks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class RespawnDragonTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0 || args.length > 1) return Collections.emptyList();
        return Arrays.asList("overrideCooldown", "heedCooldown");
    }
}