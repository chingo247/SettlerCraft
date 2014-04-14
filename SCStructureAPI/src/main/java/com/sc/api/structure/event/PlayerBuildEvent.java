/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event;

import com.settlercraft.core.model.entity.structure.Structure;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class PlayerBuildEvent extends BuildEvent {
    private final Player player;
    

    public PlayerBuildEvent(Structure structure, Player player, ItemStack stack) {
        super(structure,player, stack);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
    
    
    
}
