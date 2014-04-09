/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.listener;

import com.settlercraft.model.entity.structure.StructureChest;
import com.settlercraft.persistence.StructureChestService;
import com.settlercraft.plugin.SettlerCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

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
    public void onBuildChestBreak(BlockBreakEvent bbe) {
        System.out.println("Inventory Close event");
        if (bbe.getBlock().getType() == Material.CHEST) {
            Location chest = bbe.getBlock().getLocation();
            final StructureChest stc = scs.getStructureChest(chest.getWorld().getName(), chest.getBlockX(), chest.getBlockY(), chest.getBlockZ());
            if (stc != null) {
                bbe.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryChangeEvent(InventoryClickEvent ice) {
        System.out.println("Inventory Click Event: " + ice.getAction());


    }


}
