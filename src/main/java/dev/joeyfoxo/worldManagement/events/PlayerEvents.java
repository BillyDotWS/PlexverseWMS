package dev.joeyfoxo.worldManagement.events;

import dev.joeyfoxo.worldManagement.WorldManagement;
import dev.joeyfoxo.worldManagement.gui.GUIManager;
import dev.joeyfoxo.worldManagement.gui.MainGUI;
import dev.joeyfoxo.worldManagement.gui.UnloadedGUI;
import dev.joeyfoxo.worldManagement.gui.item.WMSItem;
import dev.joeyfoxo.worldManagement.util.Config;
import dev.joeyfoxo.worldManagement.util.Util;
import dev.joeyfoxo.worldManagement.worlds.WorldHandler;
import dev.joeyfoxo.worldManagement.worlds.WorldSave;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static dev.joeyfoxo.worldManagement.util.Util.getWorldUIDFromMeta;

public class PlayerEvents implements Listener {

    WorldHandler worldHandler;

    public PlayerEvents(WorldHandler worldHandler) {
        this.worldHandler = worldHandler;
        Bukkit.getPluginManager().registerEvents(this, WorldManagement.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String permission = Config.readFromConfig("useWMSItem");
        if (!player.hasPermission(permission)) {
            player.sendMessage(Util.insufficientPermission(permission));
            return;
        }
        if (player.getInventory().contains(WMSItem.getWMSItem())) {
            return;
        }

        player.getInventory().setItem(8, WMSItem.getWMSItem());
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction().isRightClick()
                || event.getAction().isLeftClick())
                && (player.getInventory().getItemInMainHand().isSimilar(WMSItem.getWMSItem())
                || player.getInventory().getItemInOffHand().isSimilar(WMSItem.getWMSItem()))) {

            String permission = Config.readFromConfig("openWMSGUI");
            if (!player.hasPermission(permission)) {
                player.sendMessage(Util.insufficientPermission(permission));
                return;
            }
            player.openInventory(MainGUI.getMainGUI());
        }
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getWhoClicked() instanceof Player player) {
            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            if (event.getClickedInventory() == MainGUI.getMainGUI()) {
                event.setCancelled(true);

                if (event.getCurrentItem().isSimilar(GUIManager.closeItem)) {
                    player.closeInventory();
                    return;
                }

                if (event.getCurrentItem().isSimilar(MainGUI.getFilterItem())) {
                    MainGUI.cycleFilterItem();
                    return;
                }

                if (event.getCurrentItem().isSimilar(MainGUI.getUnloadItem())) {
                    player.openInventory(UnloadedGUI.getUnloadedGUI());
                    return;
                }

                if (event.getCurrentItem().isSimilar(MainGUI.getImportItem())) {
                    player.closeInventory();
                    player.sendMessage(Component.text("Please input a world name: "));
                    worldHandler.getAwaitingWorldInput().add(player.getUniqueId());
                    return;
                }

                String worldUUID = getWorldUIDFromMeta(meta);
                if (worldUUID == null) {
                    return;
                }

                if (event.getClick().isShiftClick()) {

                    for (WorldSave saves : WorldHandler.loadedWorlds) {
                        if (saves.world().getUID().toString().equals(worldUUID)) {
                            worldHandler.unloadWorld(saves.world(), player);
                            return;
                        }
                    }
                }

                for (WorldSave saves : WorldHandler.loadedWorlds) {
                    if (saves.world().getUID().toString().equals(worldUUID)) {
                        player.teleport(saves.world().getSpawnLocation());
                        return;
                    }
                }
            }

            if (event.getClickedInventory() == UnloadedGUI.getUnloadedGUI()) {
                event.setCancelled(true);

                if (event.getCurrentItem().isSimilar(GUIManager.closeItem)) {
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(WorldManagement.getInstance(), () -> player.openInventory(MainGUI.getMainGUI()), 2);
                    return;
                }

                if (event.getClick().isShiftClick()) {

                    String worldUUID = getWorldUIDFromMeta(meta);
                    if (worldUUID == null) {
                        return;
                    }

                    for (WorldSave saves : WorldHandler.unloadedWorlds) {
                        if (saves.world().getUID().toString().equals(worldUUID)) {
                            worldHandler.deleteWorld(saves, player);
                            return;
                        }
                    }
                }


                String worldUUID = getWorldUIDFromMeta(meta);
                if (worldUUID == null) {
                    return;
                }

                for (WorldSave saves : WorldHandler.unloadedWorlds) {
                    if (saves.world().getUID().toString().equals(worldUUID)) {
                        worldHandler.loadWorld(saves, player);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {

        Player player = event.getPlayer();
        if (worldHandler.getAwaitingWorldInput().contains(player.getUniqueId())) {
            event.setCancelled(true);
            worldHandler.importWorld(event.message(), player);

        }

    }
}
