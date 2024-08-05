package dev.joeyfoxo.worldManagement.worlds;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public record WorldSave(World world, GameType gameType, Player saver, LocalDateTime creationDate) {

    public Material getIcon() {
        return gameType.getMaterial();
    }
}
