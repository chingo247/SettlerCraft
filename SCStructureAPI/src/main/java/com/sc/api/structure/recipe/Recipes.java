/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.recipe;

import com.settlercraft.recipe.CShapedRecipe;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class Recipes {

    public static final String CONSTRUCTION_TOOL = "Construction Hammer";

    private static final HashSet<CShapedRecipe> recipes = new HashSet<>(Arrays.asList(
            new CShapedRecipe(new ItemStack(Material.STONE_HOE), CONSTRUCTION_TOOL)
            .shape("W", "S", "S")
            .setIngredient('W', Material.WORKBENCH)
            .setIngredient('S', Material.STICK),
            new CShapedRecipe(new ItemStack(Material.PAPER), "Town Center")
            .shape("D", "D", "D")
            .setIngredient('D', Material.DIRT),
            new CShapedRecipe(new ItemStack(Material.PAPER), "Colosseum")
            .shape("S", "D", "S")
            .setIngredient('D', Material.DIRT)
            .setIngredient('S', Material.STONE),
            new CShapedRecipe(new ItemStack(Material.PAPER), "PluginCrashAttempt")
            .shape("S", "D", "D")
            .setIngredient('D', Material.DIRT)
            .setIngredient('S', Material.STONE)
    ));

    public static final HashSet<CShapedRecipe> getRecipes() {
        return new HashSet<>(recipes);
    }

}
