package me.EvsDev.EnderDragonTweaks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.boss.DragonBattle;
import org.bukkit.boss.DragonBattle.RespawnPhase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RespawnDragonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // Not a player - TODO make this be okay
            return true;
        }

        final Player player = (Player) sender;
        final World theEnd = player.getWorld();

        if (theEnd.getEnvironment() != Environment.THE_END) {
            // Not in the End
            sender.sendMessage("You cannot respawn the Dragon while not in the End.");
            return true;
        }

        final DragonBattle dragonBattle = theEnd.getEnderDragonBattle();
        if (dragonBattle.getEnderDragon() != null) {
            // Already a dragon
            sender.sendMessage("You cannot respawn the Dragon when there is already one.");
            return true;
        }

        if (dragonBattle.getRespawnPhase() != RespawnPhase.NONE) {
            // Respawn already in progress
            sender.sendMessage("You cannot respawn the Dragon while it is already respawning." + dragonBattle.getRespawnPhase().toString());
            return true;
        }
        sender.sendMessage("You cannot respawn the Dragon while it is already respawning." + dragonBattle.getRespawnPhase().toString());

        final Location endPortalLoc = dragonBattle.getEndPortalLocation();
        endPortalLoc.clone().add(3, 2, 0).getBlock().setType(Material.END_CRYSTAL);
        endPortalLoc.clone().add(-3, 2, 0).getBlock().setType(Material.END_CRYSTAL);
        endPortalLoc.clone().add(0, 2, 3).getBlock().setType(Material.END_CRYSTAL);
        endPortalLoc.clone().add(0, 2, -3).getBlock().setType(Material.END_CRYSTAL);

        dragonBattle.initiateRespawn();

        sender.sendMessage("Beginning Ender Dragon respawn process");
        return true;
    }

}
