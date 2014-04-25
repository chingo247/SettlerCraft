/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.sc.api.structure.event.structure.StructureLayerCompleteEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureState;
import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
import com.settlercraft.core.persistence.StructureProgressService;
import com.settlercraft.core.util.Maths;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class BuildAction {

    private final Structure structure;

    public BuildAction(Structure structure) {
        this.structure = structure;
    }

    public void build(Inventory inventory, int baseValue, BuildCallback callback) {
        if (structure.getStatus() == StructureState.READY_TO_BE_BUILD) {
            StructureProgressService structureProgressService = new StructureProgressService();
            List<MaterialResource> resources = structure.getProgress().getResources();
            Iterator<MaterialResource> lit = resources.iterator();

            while (lit.hasNext()) {
                MaterialResource materialResource = lit.next();

                for (ItemStack stack : inventory) {
                    if (materialResource != null && stack != null && materialResource.getMaterial() == stack.getType()) {
                        int removed = structureProgressService.resourceTransaction(materialResource, Maths.lowest(stack.getAmount(), baseValue, stack.getMaxStackSize()));
                        
                        if (removed > 0) {
                            // Remove items from player inventory
                            ItemStack removedIS = new ItemStack(stack);
                            removedIS.setAmount(removed);
                            inventory.removeItem(removedIS);

                            // Layer Complete?
                            if (structure.getProgress().getResources().isEmpty()) {
                                int completedLayer = structure.getProgress().getLayer();
                                structureProgressService.nextLayer(structure.getProgress(), false);
                                SCStructureAPI.build(structure).layer(completedLayer, true);
                                Bukkit.getPluginManager().callEvent(new StructureLayerCompleteEvent(structure, completedLayer));
                            }
                            callback.onSucces(structure,removedIS);
                        }
                    }
                }
            }
            callback.onResourcesNotRequired(structure);
        } else {
            callback.onNotInBuildState(structure);
        }
    }
    
    public interface BuildCallback {
        /**
         * Code to be executed on succes
         * U can update the inventory here or/and fire an event
         * @param structure The structure
         * @param deposit The itemStack removed from the inventory
         */
        void onSucces(Structure structure, ItemStack deposit);
        /**
         * Structure was in a state where building is not possible,
         * use structure.getStatus() and define what to do for that state
         * @param structure The structure
         */
        void onNotInBuildState(Structure structure);
        /**
         * Inventory doesnt have any materials the Structure requires at the moment
         * @param structure The structure
         */
        void onResourcesNotRequired(Structure structure);
    }

}
