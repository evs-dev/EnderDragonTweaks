package me.EvsDev.EnderDragonTweaks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.boss.DragonBattle;
import org.bukkit.boss.DragonBattle.RespawnPhase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

public class RespawnDragonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final World theEnd = Bukkit.getServer().getWorlds().stream().filter(world -> world.getEnvironment() == Environment.THE_END).findFirst().orElse(null);
        if (theEnd == null) {
            sender.sendMessage("Could not find End world.");
            return true;
        }

        if (args.length > 0) {
            switch (args[0]) {
                default:
                case "overrideCooldown":
                    if (EndCrystalPlacedListener.respawnIsInCooldown()) {
                        EndCrystalPlacedListener.cancelCooldown();
                        sender.sendMessage("The respawn cooldown was active so it was cancelled.");
                    }
                    break;
                case "heedCooldown":
                    if (EndCrystalPlacedListener.respawnIsInCooldown()) {
                        sender.sendMessage("The respawn cooldown is active and you have chosen not to override it.");
                        return true;
                    }
                    break;
            }
        }

        final DragonBattle dragonBattle = theEnd.getEnderDragonBattle();
        if (dragonBattle.getEnderDragon() != null) {
            sender.sendMessage("You cannot respawn the Dragon when there is already one.");
            return true;
        }

        if (dragonBattle.getRespawnPhase() != RespawnPhase.NONE) {
            sender.sendMessage("You cannot respawn the Dragon while it is already respawning.");
            return true;
        }

        final Location endPortalLoc = dragonBattle.getEndPortalLocation();
        theEnd.spawnEntity(endPortalLoc.clone().add(2.5d, 1, 0), EntityType.ENDER_CRYSTAL);
        theEnd.spawnEntity(endPortalLoc.clone().add(-2.5d, 1, 0), EntityType.ENDER_CRYSTAL);
        theEnd.spawnEntity(endPortalLoc.clone().add(0, 1, 2.5d), EntityType.ENDER_CRYSTAL);
        theEnd.spawnEntity(endPortalLoc.clone().add(0, 1, -2.5d), EntityType.ENDER_CRYSTAL);

        dragonBattle.initiateRespawn();

        sender.sendMessage("Dragon respawn initiated.");
        return true;
    }

}
