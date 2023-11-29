package me.EvsDev.EnderDragonTweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.attribute.Attribute;

public class DragonHealthModifier extends AbstractEnderDragonTweaksListener {

    public static int health = 200;
    public static String mode = "fixed";
    public static int modeValue = 0;
    public static Random random = new Random();

    public DragonHealthModifier() {
        final ConfigManager configManager = Main.getConfigManager();
        if (!configManager.FEATURE_DRAGON_ENHANCEMENTS.isEnabled()) return;
        health = configManager.FEATURE_DRAGON_ENHANCEMENTS.getInt("health.amount");
        mode = configManager.FEATURE_DRAGON_ENHANCEMENTS.getString("health.mode").toLowerCase();
        modeValue = configManager.FEATURE_DRAGON_ENHANCEMENTS.getInt("health.mode-value");
        if (health <= 0) {
            Util.logWarning("Dragon health is set to " + health + " which is invalid. Setting to 1.");
            health = 1;
        }
        if (modeValue < 0) {
            Util.logWarning("Dragon health mode value is set to " + modeValue + " which is invalid. Setting to 0.");
            modeValue = 0;
        }
    }

    @EventHandler
    public void onDragonSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;

        int healthToSet;
        switch (mode) {
            default:
            case "fixed":
                healthToSet = health;
                break;
            case "random":
                healthToSet = random.nextInt(health - modeValue + 1) + modeValue;
                break;
            case "progressive":
                healthToSet = health + (Main.getStatisticsManager().getStatInt("dragonDeathCount") + 1) * modeValue;
                break;
        }
        final EnderDragon dragon = (EnderDragon) event.getEntity();
        dragon.setHealth(healthToSet);
        dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(healthToSet);
    }

    @Override
    public boolean shouldRegisterListener() {
        return !(health == 200 && mode == "fixed");
    }

    @Override
    public Map<String, Object> getStatisticsDefaults() {
        final Map<String, Object> defaults = new HashMap<String, Object>();
        return defaults;
    }

}