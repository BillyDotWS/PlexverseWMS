package dev.joeyfoxo.worldManagement.gui.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static dev.joeyfoxo.worldManagement.util.Util.createItem;

public class WMSItem {

    public static ItemStack getWMSItem() {
        return createItem(Material.NETHER_STAR, Component.text()
                        .content("World Management System")
                        .color(TextColor.color(3, 203, 0))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true).build(),
                List.of(Component.text()
                        .content("Use this to manage your worlds")
                        .decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.color(100, 100, 100)).build()));
    }

}
