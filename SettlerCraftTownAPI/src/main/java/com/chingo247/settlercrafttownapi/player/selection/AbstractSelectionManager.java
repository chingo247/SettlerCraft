/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.player.selection;

import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.chingo247.settlercrafttownapi.world.Vector2D;
import com.sk89q.worldedit.Vector;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public abstract class AbstractSelectionManager {
    
    public abstract void select(Player player, World world, Vector vector, ChatColor color);
    
    public void select(Player player, Location location, ChatColor color) {
        select(player, location.getWorld(), new Vector(location.getX(), location.getY(), location.getZ()), color);
    }
    
    public void select(Player player, Block block, ChatColor color) {
        select(player, block.getLocation(), color);
    }
    
    public abstract void selectField(Player player, World world, Vector2D pos1, Vector2D pos2, int height, ChatColor color);
    
    public abstract void selectRegion(Player player, World world, Vector pos1, Vector pos2, ChatColor color);
    
    public void selectRegion(Player player, World world, Dimension dimension, ChatColor color) {
        AbstractSelectionManager.this.selectRegion(player, world, dimension.getMinPosition(), dimension.getMaxPosition(), color);
    }
    
    public abstract void clear(UUID uuid);
    
    public void clear(Player player) {
        clear(player.getUniqueId());
    }
    
}
