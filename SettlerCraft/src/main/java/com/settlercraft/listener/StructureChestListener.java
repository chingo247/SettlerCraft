/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.listener;

import com.settlercraft.main.SettlerCraft;
import com.settlercraft.model.structure.Structure;
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
            Structure s = sc.getDatabase().find(Structure.class).where()
                    .eq("structureChest.x", chest.getX())
                    .eq("structureChest.y", chest.getY())
                    .eq("structureChest.z", chest.getZ())
                    .ieq("structureChest.world", chest.getWorld().getName())
                    .findUnique();
            System.out.println(s);
            if(s != null) {
                
            }
        }
    }
    
    @EventHandler
    public void onBuildChestInventoryChanged(InventoryMoveItemEvent ime) {
        System.out.println("Inventory move item Event");
    }
    
    
    
}
