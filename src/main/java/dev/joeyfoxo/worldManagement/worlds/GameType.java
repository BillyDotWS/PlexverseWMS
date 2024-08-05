package dev.joeyfoxo.worldManagement.worlds;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public enum GameType {

    MICRO_BATTLES(Material.WOODEN_SWORD, NamedTextColor.RED),
    OITQ(Material.ARROW, NamedTextColor.YELLOW),
    SKYWARS(Material.DIAMOND_CHESTPLATE, NamedTextColor.AQUA),
    SURVIVAL_GAMES(Material.COOKED_BEEF, NamedTextColor.GREEN),
    SKYBLOCK(Material.GRASS_BLOCK, NamedTextColor.DARK_AQUA),
    CAKEWARS(Material.CAKE, NamedTextColor.LIGHT_PURPLE),
    ALL(Material.CLOCK, NamedTextColor.GOLD);

    private final Material material;
    private final NamedTextColor color;

    GameType(Material material, NamedTextColor color) {
        this.material = material;
        this.color = color;
    }

    public Material getMaterial() {
        return material;
    }

    public NamedTextColor getColor() {
        return color;
    }
}

