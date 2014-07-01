/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.perks;

import com.sc.construction.structure.Structure;
import com.sc.persistence.StructureService;
import java.util.Collection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class PerkListener implements Listener {

    private StructureService ss;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent bbe) {
//        Structure structure = ss.getStructure(bbe.getBlock().getLocation());
//        if (structure != null) {
        // Multiply
        Collection<ItemStack> drops = bbe.getBlock().getDrops();
        for (ItemStack stack : drops) {
            stack.setAmount(stack.getAmount() * 5);
            bbe.getBlock().getWorld().dropItemNaturally(bbe.getBlock().getLocation(), stack);
        }
        
        
        System.out.println("Drops!");
//        }
    }

    @EventHandler
    public void onSmeltEvent(FurnaceSmeltEvent fse) {
//        Structure structure = ss.getStructure(fse.getBlock().getLocation());
//        if (structure != null) {
//            
//        }
        
        System.out.println(fse.getResult().getAmount());
        ItemStack stack = fse.getResult();
        stack.setAmount(stack.getAmount() * 10);
        fse.setResult(stack);
    }
    

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent fbe) throws InstantiationException, IllegalAccessException {
        System.out.println(fbe.getBurnTime());
        System.out.println(fbe.getEventName());
//        Structure structure = ss.getStructure(fbe.getBlock().getLocation());
//        if (structure != null) {
//            fbe.
//        }
    }

    @EventHandler
    public void onItemCrafted(CraftItemEvent cie) {
        Structure structure = ss.getStructure(cie.getWhoClicked().getLocation());
        if (structure != null) {
            // Multiply
            // ExpIncrease?
        }
    }

    @EventHandler
    public void onBrewEvent(BrewEvent be) {
        Structure structure = ss.getStructure(be.getBlock().getLocation());
        if (structure != null) {
            // Multiply
            // ExpIncrease?
        }
    }

//    @EventHandler
//    public void onChickenEggThrow(PlayerEggThrowEvent pete) {
//        if (pete.getHatchingType() != EntityType.CHICKEN) {
//            return;
//        }
//        
//        
//        
//        Structure structure = ss.getStructure(pete.getPlayer().getLocation());
//        if (structure != null) {
//            // Hatch chance 
//            // Increase hatches? / Multiply
//        }
//    }

//    @EventHandler
//    public void onSlay(EntityDeathEvent event) {
//        if (event.getEntity() != null) {
//            Structure structure = ss.getStructure(event.getEntity().getLocation());
//            if (structure != null) {
//            // Increase drops / Multiply
//                // ExpIncrease?
//            }
//        }
//    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent pfe) {
        Structure structure = ss.getStructure(pfe.getPlayer().getLocation());
        if (structure != null) {
            // Increase bitechance
            // ExpIncrease?
        }
    }
    
    @EventHandler
    public void onBlockExp(BlockExpEvent bee) {
        
    }
    

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent cse) {
        
    }
    

    

}
