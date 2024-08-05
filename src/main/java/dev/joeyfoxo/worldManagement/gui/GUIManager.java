package dev.joeyfoxo.worldManagement.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static dev.joeyfoxo.worldManagement.util.Util.createItem;

public class GUIManager {

    public GUIManager() {
        new MainGUI();
        new UnloadedGUI();
    }

    public static final ItemStack closeItem =
            createItem(Material.BARRIER, Component.text("Close GUI").color(NamedTextColor.DARK_RED)
                    .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
            );

}
