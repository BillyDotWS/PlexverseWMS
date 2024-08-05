package dev.joeyfoxo.worldManagement.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;

import static dev.joeyfoxo.worldManagement.util.Util.createGUI;
import static dev.joeyfoxo.worldManagement.gui.GUIManager.closeItem;

public class UnloadedGUI {

    private static Inventory unloadedGUI;

    public UnloadedGUI() {
        unloadedGUI = createGUI(null, 54, Component.text("Unloaded Worlds"));
        populateUnloadedGUI();
    }

    public static Inventory getUnloadedGUI() {
        return unloadedGUI;
    }

    private void populateUnloadedGUI() {

        unloadedGUI.setItem(49, closeItem);

    }
}

