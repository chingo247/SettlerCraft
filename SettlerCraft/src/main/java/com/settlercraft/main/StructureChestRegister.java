/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.main;

import com.settlercraft.model.structure.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Chingo
 */
public class StructureChestRegister {
    
    private SettlerCraft craft;
    
    public StructureChestRegister(SettlerCraft sc) {
        this.craft = sc;
    }
    
    public void registerStructureChest(Chest chest, Structure structure) {
        chest.setMetadata("builderchest", new FixedMetadataValue(craft, chest));
    }
    
    public void registerStructureChest(Block block, Structure structure) {
        if(block.getType() != Material.CHEST) throw new IllegalArgumentException("Block is not of type CHEST");
        registerStructureChest((Chest) block.getState(), structure);
    }
    
    public void registerStructureChest(Location location, Structure structure) {
        registerStructureChest(location.getBlock(), structure);
    }
    
}
