package dev.joeyfoxo.worldManagement.worlds;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

// Define the record
public record WorldSave(World world, GameType gameType, Player saver, LocalDateTime creationDate) {

    // Define the accessor methods
    public Material getIcon() {
        return gameType.getMaterial();
    }
}
