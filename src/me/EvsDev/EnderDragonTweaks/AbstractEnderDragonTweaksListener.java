package me.EvsDev.EnderDragonTweaks;

import java.util.Map;

import org.bukkit.event.Listener;

public abstract class AbstractEnderDragonTweaksListener implements Listener {

    public abstract boolean shouldRegisterListener();
    public abstract Map<String, Object> getStatisticsDefaults();

}
