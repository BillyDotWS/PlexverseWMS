package dev.joeyfoxo.worldManagement.worlds;

import dev.joeyfoxo.worldManagement.WorldManagement;
import dev.joeyfoxo.worldManagement.gui.MainGUI;
import dev.joeyfoxo.worldManagement.gui.UnloadedGUI;
import dev.joeyfoxo.worldManagement.util.Config;
import dev.joeyfoxo.worldManagement.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static dev.joeyfoxo.worldManagement.util.Util.createItem;

public class WorldHandler {

    public static HashSet<WorldSave> loadedWorlds = new HashSet<>();
    public static HashSet<WorldSave> unloadedWorlds = new HashSet<>();
    public HashSet<UUID> awaitingWorldInput = new HashSet<>();

    public WorldHandler() {
        Bukkit.getScheduler().runTaskTimer(WorldManagement.getInstance(), task -> {

            for (WorldSave saves : loadedWorlds) {
                if (saves.creationDate().isBefore(LocalDateTime.now().minusDays(7))) {
                    unloadWorld(saves.world(), Bukkit.getPlayer("Server"));
                }
            }
        }, 0, 20 * 60 * 60);
    }

    public void loadWorld(WorldSave worldSave, Player player) {
        String permission = Config.readFromConfig("loadWMSWorld");
        if (!player.hasPermission(permission)) {
            player.sendMessage(Util.insufficientPermission(permission));
            return;
        }

        if (worldSave.world() == null) {
            player.sendMessage(Component.text("World does not exist!"));
            return;
        }

        if (loadedWorlds.contains(worldSave)) {
            player.sendMessage(Component.text("World is already loaded!"));
            return;
        }

        String worldName = worldSave.world().getName();

        File sourceFolder = new File(Bukkit.getWorldContainer(), "Unloaded" + File.separator + worldName);
        if (!sourceFolder.exists()) {
            player.sendMessage(Component.text("World " + worldName + " does not exist in the 'Unloaded' directory."));
            return;
        }

        File destinationFolder = new File(Bukkit.getWorldContainer(), worldName);
        try {
            Files.move(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Bukkit.getLogger().info("World " + worldName + " moved back to the main world directory.");
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to move world " + worldName + " back to main world directory: " + e.getMessage());
            return;
        }

        // Load the world using WorldCreator
        Bukkit.getScheduler().runTask(WorldManagement.getInstance(), () -> {
            WorldCreator worldCreator = new WorldCreator(worldName);
            World loadedWorld = Bukkit.createWorld(worldCreator);
            if (loadedWorld != null) {
                handleWorldLoaded(loadedWorld, player);
            } else {
                player.sendMessage(Component.text("Failed to load world " + worldName + "."));
            }
        });
    }

    private void handleWorldLoaded(World world, Player player) {
        LocalDateTime currentTime = Util.millisToLocalDateTime(System.currentTimeMillis());
        WorldSave worldSave = new WorldSave(world, Util.getRandomGameTypeExcludingAll(), player, currentTime);
        //Changing this breaks not sure why
        loadedWorlds.add(worldSave);
        unloadedWorlds.remove(worldSave);
        player.sendMessage(Component.text("World " + world.getName() + " loaded successfully!"));
        addWorldToGUI(worldSave, MainGUI.getMainGUI());
        removeWorldFromGUI(worldSave, UnloadedGUI.getUnloadedGUI());
    }

    public void unloadWorld(World world, Player player) {

        if (player != null) {
            String permission = Config.readFromConfig("unloadWMSWorld");
            if (!player.hasPermission(permission)) {
                player.sendMessage(Util.insufficientPermission(permission));
                return;
            }
        }

        if (!Bukkit.isTickingWorlds()) {
            Bukkit.unloadWorld(world, true);
        }
        for (Player players : world.getPlayers()) {
            players.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());

        }

        Bukkit.getScheduler().runTaskLater(WorldManagement.getInstance(), () -> {

            File sourceWorldFolder = new File(Bukkit.getWorldContainer(), world.getName());
            File destinationFolder = new File(Bukkit.getWorldContainer(), "Unloaded");

            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            File destinationWorldFolder = new File(destinationFolder, world.getName());
            try {
                // Moving the world folder to the "Unloaded" directory
                Files.move(sourceWorldFolder.toPath(), destinationWorldFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if (player != null) {
                    player.sendMessage(Component.text("World " + world.getName() + " moved to the 'Unloaded' directory."));
                }
                for (WorldSave worldSave : loadedWorlds) {
                    if (worldSave.world().getName().equals(world.getName())) {
                        loadedWorlds.remove(worldSave);
                        unloadedWorlds.add(worldSave);
                        removeWorldFromGUI(worldSave, MainGUI.getMainGUI());
                        addWorldToGUI(worldSave, UnloadedGUI.getUnloadedGUI());
                        break;
                    }
                }
            } catch (IOException e) {
                if (player != null) {
                    player.sendMessage(Component.text("Failed to move world " + world.getName() + ": " + e.getMessage()));
                }
            }

        }, 20L);

    }

    public void createWorld(World world, Player player) {
        String permission = Config.readFromConfig("createWMSWorld");
        if (!player.hasPermission(permission)) {
            player.sendMessage(Util.insufficientPermission(permission));
            return;
        }

    }

    public void deleteWorld(WorldSave worldSave, Player player) {
        String permission = Config.readFromConfig("deleteWMSWorld");
        if (!player.hasPermission(permission)) {
            player.sendMessage(Util.insufficientPermission(permission));
            return;
        }

        for (WorldSave saves : loadedWorlds) {
            if (saves.equals(worldSave)) {
                player.sendMessage(Component.text("World must be unloaded before deletion."));
                return;
            }
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), "Unloaded" + File.separator + worldSave.world().getName());
        if (!worldFolder.exists()) {
            player.sendMessage(Component.text("World does not exist!"));
            return;
        }
        if (Util.deleteDirectory(worldFolder)) {
            player.sendMessage(Component.text("World deleted successfully!"));
            removeWorldFromGUI(worldSave, UnloadedGUI.getUnloadedGUI());
            unloadedWorlds.remove(worldSave);
        } else {
            player.sendMessage(Component.text("Failed to delete worldSave!"));
        }
    }

    public void importWorld(Component worldName, Player player) {
        String permission = Config.readFromConfig("importWMSWorld");
        awaitingWorldInput.remove(player.getUniqueId());
        if (!player.hasPermission(permission)) {
            player.sendMessage(Util.insufficientPermission(permission));
            return;
        }

        getAwaitingWorldInput().remove(player.getUniqueId());

        Bukkit.getScheduler().runTask(WorldManagement.getInstance(), () -> {
            File worldFile = new File(Bukkit.getWorldContainer(), Util.getTextFromComponent(worldName));
            if (!worldFile.exists() || !worldFile.isDirectory()) {
                player.sendMessage(Component.text("World not found!"));
                return;
            }

            World world = Bukkit.getWorld(worldFile.getName());
            if (world == null) {
                world = Bukkit.createWorld(new WorldCreator(worldFile.getName()));
                if (world == null) {
                    player.sendMessage(Component.text("World could not be loaded. " +
                            "Please check the console for errors."));
                    return;
                }
            }

            for (WorldSave saves : unloadedWorlds) {
                if (saves.world().getName().equals(world.getName())) {
                    player.sendMessage(Component.text("World already imported!"));
                    return;
                }
            }

            for (WorldSave saves : loadedWorlds) {
                if (saves.world().getName().equals(world.getName())) {
                    player.sendMessage(Component.text("World already imported!"));
                    return;
                }
            }

            WorldSave worldSave = new WorldSave(world,
                    Util.getRandomGameTypeExcludingAll(), player,
                    Util.millisToLocalDateTime(System.currentTimeMillis()));

            player.sendMessage(Component.text("World imported successfully!"));

            unloadedWorlds.add(worldSave);
            unloadWorld(world, player);
            addWorldToGUI(worldSave, UnloadedGUI.getUnloadedGUI());
        });
    }

    public HashSet<UUID> getAwaitingWorldInput() {
        return awaitingWorldInput;
    }

    private void addWorldToGUI(WorldSave worldSave, Inventory GUI) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = worldSave.creationDate().format(formatter);
        ItemStack item = createItem(worldSave.getIcon(),
                Component.text(worldSave.world().getName())
                        .color(TextColor.color(128, 128, 128))
                        .decoration(TextDecoration.ITALIC, false),
                List.of(
                        Component.text(""), // Space between title and lore
                        Component.text("Type: ").color(TextColor.color(128, 128, 128))
                                .append(Component.text(worldSave.gameType().name())
                                        .color(worldSave.gameType().getColor())
                                        .decoration(TextDecoration.BOLD, true)
                                        .decoration(TextDecoration.ITALIC, false)),
                        Component.text("Created By: ").color(TextColor.color(128, 128, 128))
                                .append(Component.text(worldSave.saver().getName())
                                        .color(TextColor.color(0, 255, 0))
                                        .decoration(TextDecoration.ITALIC, false)),
                        Component.text("Created On: ").color(TextColor.color(128, 128, 128))
                                .append(Component.text(formattedDate)
                                        .color(TextColor.color(0, 255, 0))
                                        .decoration(TextDecoration.ITALIC, false))
                ));
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Util.worldUUIDKey(), PersistentDataType.STRING, worldSave.world().getUID().toString());
        item.setItemMeta(meta);

        MainGUI.originalWorldItems.add(item);
        GUI.addItem(item);

    }

    private void removeWorldFromGUI(WorldSave worldSave, Inventory GUI) {

        for (ItemStack item : GUI.getContents()) {
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }

            if (meta.getPersistentDataContainer().has(Util.worldUUIDKey(), PersistentDataType.STRING)) {
                String worldUUID = meta
                        .getPersistentDataContainer()
                        .get(Util.worldUUIDKey(),
                                PersistentDataType.STRING);

                if (worldUUID.equals(worldSave.world().getUID().toString())) {
                    GUI.remove(item);
                    MainGUI.originalWorldItems.remove(item);
                    return;
                }
            }
        }

    }


}
