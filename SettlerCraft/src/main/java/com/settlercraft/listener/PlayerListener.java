/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author Chingo
 */
public class PlayerListener implements Listener{
    
    
    @EventHandler
    public void onPlayerPlacedBlock(BlockPlaceEvent bpe) {
        System.out.println(
                "Player placed: " + bpe.getBlock().getType()
              + " data: " + bpe.getBlock().getData()
              + " durability: " + bpe.getBlock().getType().getMaxDurability()
              + " material data: " + bpe.getBlock().getType().getData()
        );
        
    }
}
