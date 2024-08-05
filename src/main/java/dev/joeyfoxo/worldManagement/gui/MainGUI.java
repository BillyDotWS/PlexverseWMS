package dev.joeyfoxo.worldManagement.gui;

import dev.joeyfoxo.worldManagement.WorldManagement;
import dev.joeyfoxo.worldManagement.util.Util;
import dev.joeyfoxo.worldManagement.worlds.GameType;
import dev.joeyfoxo.worldManagement.worlds.WorldHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static dev.joeyfoxo.worldManagement.gui.GUIManager.closeItem;
import static dev.joeyfoxo.worldManagement.util.Util.createGUI;
import static dev.joeyfoxo.worldManagement.util.Util.createItem;

public class MainGUI {

    private static Inventory mainGUI;
    private final ItemStack infoItem =
            createItem(Material.GRASS_BLOCK, Component.text("Build Server Info")
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .color(TextColor.color(255, 187, 0)));
    private static final ItemStack importItem = createItem(
            Material.ENDER_CHEST,
            Component.text("Import World")
                    .color(NamedTextColor.DARK_PURPLE)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false),

            List.of(
                    Component.text(" "), // Space between title and lore
                    Component.text("Import a new world into the server")
                            .color(TextColor.color(128, 128, 128))
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack createWorldItem = createItem(
            Material.CRAFTING_TABLE,
            Component.text("Create World")
                    .color(NamedTextColor.GREEN)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false),

            List.of(
                    Component.text(" "), // Space between title and lore
                    Component.text("Create a new world")
                            .color(TextColor.color(128, 128, 128))
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private static final ItemStack unloadItem = createItem(
            Material.GRAY_DYE,
            Component.text("Unloaded Worlds")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false),

            List.of(
                    Component.text(" "), // Space between title and lore
                    Component.text("View and manage unloaded worlds")
                            .color(TextColor.color(128, 128, 128))
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private static ItemStack filterItem = createItem(
            GameType.ALL.getMaterial(),
            Component.text("Sort By:")
                    .color(TextColor.color(128, 128, 128))
                    .decoration(TextDecoration.ITALIC, false),
            List.of(
                    Component.text("ALL")
                            .color(GameType.ALL.getColor())
                            .decorate(TextDecoration.BOLD)
                            .decoration(TextDecoration.ITALIC, false)));

    private static GameType currentGameType = GameType.ALL;
    public static List<ItemStack> originalWorldItems = new ArrayList<>();
    private static List<ItemStack> modifiedWorldItems = new ArrayList<>();

    public MainGUI() {
        mainGUI = createGUI(null, 54, Component.text("Plexverse World Manager"));
        populateGUI();
        Bukkit.getScheduler().runTaskTimer(WorldManagement.getInstance(), task -> {

            ItemMeta meta = infoItem.getItemMeta();
            meta.lore(List.of(
                    Component.text("")
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Loaded Worlds: ")
                            .color(TextColor.color(128, 128, 128))
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(String.valueOf(WorldHandler.loadedWorlds.size()))
                                    .color(TextColor.color(0, 255, 0))
                                    .decoration(TextDecoration.ITALIC, false)),
                    Component.text("Unloaded Worlds: ")
                            .color(TextColor.color(128, 128, 128))
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(String.valueOf(WorldHandler.unloadedWorlds.size()))
                                    .color(TextColor.color(0, 255, 0))
                                    .decoration(TextDecoration.ITALIC, false)),
                    Component.text("")
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text(Util.round(Bukkit.getTPS()[0], 2) + " TPS")
                            .color(getTPSColor(Util.round(Bukkit.getTPS()[0], 2)))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text(Util.getUsedMemory() + "/" + Util.getMaxMemory() + " MB")
                            .color(TextColor.color(0, 171, 128))
                            .decoration(TextDecoration.ITALIC, false)));
            infoItem.setItemMeta(meta);
            mainGUI.setItem(4, infoItem);

            originalWorldItems.removeAll(Arrays.stream(UnloadedGUI.getUnloadedGUI().getContents())
                    .filter(item -> item != null && MainGUI.originalWorldItems.contains(item))
                    .toList());

        }, 0, 40);
    }

    public static Inventory getMainGUI() {
        return mainGUI;
    }

    public void populateGUI() {
        AtomicInteger emptySlots = new AtomicInteger();

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45) {
                mainGUI.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")));
            }
        }

        mainGUI.setItem(49, closeItem);
        mainGUI.setItem(47, importItem);
        mainGUI.setItem(51, createWorldItem);
        mainGUI.setItem(8, unloadItem);
        mainGUI.setItem(0, filterItem);

        for (ItemStack item : mainGUI.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                emptySlots.getAndIncrement();
            }
        }

        if (emptySlots.get() == 0) {
            mainGUI.setItem(53, createItem(Material.ARROW, Component.text("Next Page")));
        }
    }

    public static ItemStack getImportItem() {
        return importItem;
    }

    public static ItemStack getUnloadItem() {
        return unloadItem;
    }

    public static ItemStack getFilterItem() {
        return filterItem;
    }

    public static void cycleFilterItem() {
        GameType[] gameTypes = GameType.values();
        int index = (currentGameType.ordinal() + 1) % gameTypes.length;
        currentGameType = gameTypes[index];

        filterItem = filterItem.withType(currentGameType.getMaterial());

        ItemMeta meta = filterItem.getItemMeta();
        meta.lore(List.of(Component.text(currentGameType.name())
                .color(currentGameType.getColor())
                .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        filterItem.setItemMeta(meta);
        mainGUI.setItem(0, filterItem); // Update the filter item in the GUI

        filterItems(currentGameType);
    }

    private static void filterItems(GameType gameType) {

        for (int i = 9; i < 45; i++) {
            mainGUI.setItem(i, null);
        }
        modifiedWorldItems.clear();
        if (gameType == GameType.ALL) {
            modifiedWorldItems.addAll(originalWorldItems);
        } else {
            modifiedWorldItems.addAll(originalWorldItems.stream()
                    .filter(item -> item.getType() == gameType.getMaterial())
                    .collect(Collectors.toSet()));
        }
        for (int i = 0; i < modifiedWorldItems.size() && i < 36; i++) {
            mainGUI.setItem(9 + i, modifiedWorldItems.get(i));
        }
    }

    private TextColor getTPSColor(double tps) {
        if (tps > 15) {
            return TextColor.color(0, 255, 0); // Green
        } else if (tps > 10) {
            return TextColor.color(255, 255, 0); // Yellow
        } else {
            return TextColor.color(255, 0, 0); // Red
        }
    }
}
