/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.listener;

import com.settlercraft.SettlerCraft;
import com.settlercraft.model.entity.structure.StructureChest;
import com.settlercraft.persistence.StructureChestService;
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
    private final StructureChestService scs;
    
    public StructureChestListener(SettlerCraft sc) {
        this.sc = sc;
        this.scs = new StructureChestService();
    }
    
    @EventHandler
    public void onBuildChestEvent(InventoryCloseEvent ice) {
        System.out.println("Inventory Close event");
        if(ice.getInventory().getHolder() instanceof Chest && ice.getInventory().getContents().length > 0){
            Chest chest = (Chest) ice.getInventory().getHolder();
            StructureChest stc = scs.getStructureChest(chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ());
            if(stc != null) {
                System.out.println(stc);
            }
        }
    }
    
    @EventHandler
    public void onBuildChestInventoryChanged(InventoryMoveItemEvent ime) {
        System.out.println("Inventory move item Event");
    }
    
    
    
}
