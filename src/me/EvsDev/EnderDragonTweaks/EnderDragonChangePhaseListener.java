package me.EvsDev.EnderDragonTweaks;

import java.util.List;
import java.util.Random;

import org.bukkit.World.Environment;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

public class EnderDragonChangePhaseListener extends AbstractEnderDragonTweaksListener {

    private static boolean doBossbarCustomisation;
    private static List<String> bossbarNames;
    private static String bossbarColour;
    private static String bossbarStyle;

    private Entity currentDragonEntity;
    private final Random RANDOM = new Random();

    public EnderDragonChangePhaseListener() {
        final ConfigManager configManager = Main.getConfigManager();
        doBossbarCustomisation = configManager.FEATURE_BOSSBAR_CUSTOMISATION.isEnabled();
        if (doBossbarCustomisation) {
            bossbarNames = configManager.FEATURE_BOSSBAR_CUSTOMISATION.getStringList("names");
            bossbarColour = configManager.FEATURE_BOSSBAR_CUSTOMISATION.getString("colour").toUpperCase();
            bossbarStyle = configManager.FEATURE_BOSSBAR_CUSTOMISATION.getString("style").toUpperCase();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnderDragonChangePhase(EnderDragonChangePhaseEvent e) {
        if (e.getEntity().getWorld().getEnvironment() != Environment.THE_END) return;
        if (currentDragonEntity != e.getEntity()) {
            currentDragonEntity = e.getEntity();
        } else {
            return;
        }

        final BossBar bossbar = e.getEntity().getWorld().getEnderDragonBattle().getBossBar();
        if (bossbarNames.size() > 0) {
            final String name = bossbarNames.get(RANDOM.nextInt(bossbarNames.size()));
            bossbar.setTitle(name);
            Util.logInfo("Set Dragon bossbar name to " + name);
        }
        if (bossbarColour.length() > 0) {
            BarColor barColour = BarColor.PINK;
            try {
                barColour = BarColor.valueOf(bossbarColour);
            } catch (IllegalArgumentException ex) {
                Util.logWarning("Bossbar colour \"" + bossbarColour + "\" is invalid");
            } finally {
                bossbar.setColor(barColour);
                Util.logInfo("Set Dragon bossbar colour to " + bossbar.getColor().toString().toLowerCase());
            }
        }
        if (bossbarStyle.length() > 0) {
            BarStyle barStyle = BarStyle.SOLID;
            String barStyleName = "progress";
            // Convert config names to possible enum names
            // In the config and the built-in /bossbar command, "segmented" styles are called "notched",
            // and the "solid" style is called "progress"
            final String configToEnumStyle = bossbarStyle.replace("PROGRESS", "SOLID").replace("NOTCHED", "SEGMENTED");
            try {
                barStyle = BarStyle.valueOf(configToEnumStyle);
                barStyleName = bossbarStyle.toLowerCase();
            } catch (IllegalArgumentException ex) {
                Util.logWarning("Bossbar style \"" + bossbarStyle + "\" is invalid");
            } finally {
                bossbar.setStyle(barStyle);
                Util.logInfo("Set Dragon bossbar style to " + barStyleName);
            }
        }
    }

    @Override
    public boolean shouldRegisterListener() {
        return doBossbarCustomisation;
    }

}
