/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.recipe;

import com.settlercraft.plugin.SettlerCraft;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 *
 * @author Chingo
 * @deprecated No one can craft them
 */
public class SettlerCraftBuildingPlans {
    
    private SettlerCraftBuildingPlans(){}
    
    public static void load(SettlerCraft slc) {
        ShapedRecipe claimTownGroundRecipe = new CShapedRecipe(new ItemStack(Material.PAPER), "Town Center")
                .shape("D", "D")
                .setIngredient('D', Material.DIRT)
                .getRecipe();
        
        slc.getServer().addRecipe(claimTownGroundRecipe);
    }
    
    
    
    
    
    
}
