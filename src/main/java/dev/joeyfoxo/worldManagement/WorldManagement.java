package dev.joeyfoxo.worldManagement;

import dev.joeyfoxo.worldManagement.events.PlayerEvents;
import dev.joeyfoxo.worldManagement.gui.GUIManager;
import dev.joeyfoxo.worldManagement.worlds.WorldHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldManagement extends JavaPlugin {

    public static WorldManagement instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        new GUIManager();
        new PlayerEvents(new WorldHandler());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static WorldManagement getInstance() {
        return instance;
    }
}
