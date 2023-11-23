package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.attribute.Attribute;

public class DragonHealthModifier extends AbstractEnderDragonTweaksListener {

    public static int health = 200;

    public DragonHealthModifier() {
        final ConfigManager configManager = Main.getConfigManager();
        if (!configManager.FEATURE_DRAGON_ENHANCEMENTS.isEnabled()) return;
        health = configManager.FEATURE_DRAGON_ENHANCEMENTS.getInt("health");
        if (health <= 0) {
            Util.logWarning("Dragon health is set to " + health + " which is invalid. Setting to 1.");
            health = 1;
        }
    }

    @EventHandler
    public void onDragonSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;

        final EnderDragon dragon = (EnderDragon) event.getEntity();
        dragon.setHealth(health);
        dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    @Override
    public boolean shouldRegisterListener() {
        return health != 200;
    }

    @Override
    public Map<String, Object> getStatisticsDefaults() {
        final Map<String, Object> defaults = new HashMap<String, Object>();
        return defaults;
    }
}