/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.platforms.bukkit;

import com.chingo247.settlercraft.core.platforms.services.IPlayerProvider;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BKPlayerProvider implements IPlayerProvider{
    
    private final WorldEditPlugin worldEdit;

    public BKPlayerProvider() {
        this.worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null) {
            throw new RuntimeException("WorldEdit was not found!");
        }
    }
    
    @Override
    public Player getPlayer(UUID playerId) {
        org.bukkit.entity.Player player = Bukkit.getPlayer(playerId);
        return player != null ? worldEdit.wrapPlayer(Bukkit.getPlayer(playerId)) : null;
    }
    
}
