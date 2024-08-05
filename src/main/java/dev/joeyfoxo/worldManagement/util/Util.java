package dev.joeyfoxo.worldManagement.util;

import dev.joeyfoxo.worldManagement.WorldManagement;
import dev.joeyfoxo.worldManagement.worlds.GameType;
import dev.joeyfoxo.worldManagement.worlds.WorldSave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

public class Util {

    public static NamespacedKey worldUUIDKey() {
        return new NamespacedKey(WorldManagement.getInstance(), "UUID_KEY");
    }

    public static Inventory createGUI(InventoryHolder owner, int size, Component title) {
        return Bukkit.createInventory(owner, size, title);
    }


    public static Inventory createGUI(InventoryHolder owner, InventoryType type, Component title) {
        return Bukkit.createInventory(owner, type, title);
    }


    public static ItemStack createItem(Material material, Component component) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(component);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, Component component, List<Component> lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.lore(lore);
        meta.displayName(component);
        item.setItemMeta(meta);
        return item;

    }

    public static Component insufficientPermission(String permission) {
        return Component.text("You do not have permission to do this!")
                .color(TextColor.color(200, 0, 0))
                .append(Component.newline())
                .append(Component.text("You need the '" + permission + "' permission")
                        .color(TextColor.color(255, 100, 100)));
    }

    public static String getTextFromComponent(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static LocalDateTime millisToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }
    public static long getUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    }
    public static long getMaxMemory() {
            return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static String getWorldUIDFromMeta(ItemMeta meta) {
        if (meta.getPersistentDataContainer().has(Util.worldUUIDKey(), PersistentDataType.STRING)) {

            return meta
                    .getPersistentDataContainer()
                    .get(Util.worldUUIDKey(),
                            PersistentDataType.STRING);
        }
        return null;
    }

    public static GameType getRandomGameTypeExcludingAll() {
        GameType[] gameTypes = GameType.values();
        Random random = new Random();
        GameType randomGameType;
        do {
            randomGameType = gameTypes[random.nextInt(gameTypes.length)];
        } while (randomGameType == GameType.ALL);
        return randomGameType;
    }
}
