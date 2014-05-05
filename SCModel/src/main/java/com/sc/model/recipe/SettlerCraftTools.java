/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SettlerCraftTools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.settlercraft.plugin.SettlerCraft;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 *
 * @author Chingo
 */
public class SettlerCraftTools {
    
    public static final String CONSTRUCTION_TOOL = "Construction Tool";
    private static final HashMap<String,CShapedRecipe> recipes = Maps.newHashMap();

    
    public static void load(SettlerCraft slc) {
        CShapedRecipe recipe = new CShapedRecipe(new ItemStack(Material.STONE_SPADE), CONSTRUCTION_TOOL)
                .shape("W","S","S")
                .setIngredient('W', Material.WORKBENCH)
                .setIngredient('S', Material.STICK);
        put(recipe,slc);
    }
    
    private static void put(CShapedRecipe recipe, SettlerCraft slc) {
        Preconditions.checkArgument(!recipes.containsKey(recipe.getDisplayName()));
        recipes.put(recipe.getDisplayName(), recipe);
        slc.getServer().addRecipe(recipe.getRecipe());
    }
    
    public static ShapedRecipe getRecipe(String name) {
        return recipes.get(name).getRecipe();
    }
    
    public static HashMap<String, CShapedRecipe> getRecipes() {
        return new HashMap<>(recipes);
    }
    
    
}
