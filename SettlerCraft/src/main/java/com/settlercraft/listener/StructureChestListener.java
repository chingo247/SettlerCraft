/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.listener;

import com.settlercraft.main.SettlerCraft;
import com.settlercraft.model.structure.Builder;
import com.settlercraft.model.structure.StructureChest;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

/**
 *
 * @author Chingo
 */
public class StructureChestListener implements Listener {
    
    private final SettlerCraft sc;
    
    public StructureChestListener(SettlerCraft sc) {
        this.sc = sc;
    }
    
    @EventHandler
    public void onBuildChestEvent(InventoryCloseEvent ice) {
        System.out.println("Inventory Close event");
        if(ice.getInventory().getHolder() instanceof Chest){
            Chest chest = (Chest) ice.getInventory().getHolder();
            StructureChest strucChest = sc.getDatabase().find(StructureChest.class).where()
                    .eq("x", chest.getLocation().getBlockX())
                    .eq("y", chest.getLocation().getBlockY())
                    .eq("z", chest.getLocation().getBlockZ())
                    .ieq("world", chest.getLocation().getWorld().getName()).findUnique();
            if(sc != null) {
                Builder builder = sc.getDatabase().find(Builder.class).where().eq("structureChest", strucChest).findUnique();
                if(builder != null) {
                    // builder.validate
                } else {
                    throw new AssertionError("BuilderChest without builder");
                }
            }
        }
    }
    
    @EventHandler
    public void onBuildChestInventoryChanged(InventoryMoveItemEvent ime) {
        System.out.println("Inventory move item Event");
    }
    
}
