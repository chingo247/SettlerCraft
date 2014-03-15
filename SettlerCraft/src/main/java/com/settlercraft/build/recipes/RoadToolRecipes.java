/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.build.recipes;

import com.settlercraft.main.SettlerCraft;
import com.settlercraft.recipe.CShapedRecipe;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class RoadToolRecipes {

    private final Set<CShapedRecipe> recipes;

    private final Set<Material> spades = new HashSet<>(Arrays.asList(
            Material.WOOD_SPADE,
            Material.STONE_SPADE
//            Material.IRON_SPADE,
//            Material.GOLD_SPADE,
//            Material.DIAMOND_SPADE
    ));

    public RoadToolRecipes(SettlerCraft craft) {
        recipes = new HashSet<>();
        recipes.addAll(createSpadeRecipes(Material.SANDSTONE));
        recipes.addAll(createSpadeRecipes(Material.STONE));


        for (CShapedRecipe r : recipes) {
            craft.getServer().addRecipe(r.getRecipe());
            System.out.println(r.getDisplayName() + " added");
        }
    }
    
    public Set<CShapedRecipe> getRecipes() {
        return new HashSet<>(recipes);
    }

    private Set<CShapedRecipe> createSpadeRecipes(Material roadType) {

        Set<CShapedRecipe> rcp = new HashSet<>(spades.size());

        for (Material m : spades) {
            String title = "";
            switch (m) {
                case WOOD_SPADE:
                    title = "Wood";
                    break;
                case STONE_SPADE:
                    title = "Stone";
                    break;
//                case IRON_SPADE:
//                    title = "Iron";
//                    break;
//                case GOLD_SPADE:
//                    title = "Gold";
//                    break;
//                case DIAMOND_SPADE:
//                    title = "Diamond";
//                    break;
            }

            rcp.add(new CShapedRecipe(new ItemStack(m), title + " " + roadType.toString() + " Road Spade")
                    .shape("S", "T")
                    .setIngredient('S', roadType)
                    .setIngredient('T', m));
            
        }

        return rcp;
    }

}
