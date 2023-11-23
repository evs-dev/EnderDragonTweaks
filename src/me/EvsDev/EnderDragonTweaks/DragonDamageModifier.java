package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DragonDamageModifier extends AbstractEnderDragonTweaksListener {

    public static int hitMultiplier = 1;
    public static int breathMultiplier = 1;

    public DragonDamageModifier() {
        final ConfigManager configManager = Main.getConfigManager();
        if (!configManager.FEATURE_DRAGON_ENHANCEMENTS.isEnabled()) return;
        hitMultiplier = configManager.FEATURE_DRAGON_ENHANCEMENTS.getInt("damage-multiplier");
        breathMultiplier = configManager.FEATURE_DRAGON_ENHANCEMENTS.getInt("breath-damage-multiplier");
    }

    @EventHandler
    public void onPlayerDamageByDragon(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getDamager() instanceof EnderDragon) {
            if (hitMultiplier == 1) return;
            event.setDamage(event.getDamage() * Math.max(hitMultiplier, 0));
        } else if (event.getDamager() instanceof AreaEffectCloud) {
            if (breathMultiplier == 1) return;
            final AreaEffectCloud cloud = (AreaEffectCloud) event.getDamager();
            if (!(cloud.getSource() instanceof EnderDragon)) return;
            event.setDamage(event.getDamage() * Math.max(breathMultiplier, 0));
        }
    }

    @Override
    public boolean shouldRegisterListener() {
        return hitMultiplier != 1 || breathMultiplier != 1;
    }

    @Override
    public Map<String, Object> getStatisticsDefaults() {
        final Map<String, Object> defaults = new HashMap<String, Object>();
        return defaults;
    }
}