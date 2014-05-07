/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.settlercraft.core.model.entity.structure.Structure;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public interface BuildCallback {

    /**
     * Code to be executed on succes U can update the inventory here or/and fire
     * an event
     *
     * @param structure The structure
     * @param deposit The itemStack removed from the inventory
     */
    public void onSucces(Structure structure, ItemStack deposit);

    /**
     * Structure was in a state where building is not possible, use
     * structure.getStatus() and define what to do for that state
     *
     * @param structure The structure
     */
    public void onNotInBuildState(Structure structure);

    /**
     * Inventory doesnt have any materials the Structure requires at the moment
     *
     * @param structure The structure
     */
    public void onResourcesNotRequired(Structure structure);
}
