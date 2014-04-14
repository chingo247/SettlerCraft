/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.manager;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.inventory.Recipe;

/**
 *
 * @author Chingo
 */
public class RecipeManager {

    private static RecipeManager instance;
    private final Set<Recipe> recipes;

    private RecipeManager() {
        recipes = new HashSet<>();
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    /**
     * Adds a recipe to the RecipeManager
     *
     * @param recipe The recipe to add
     */
    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    /**
     * Returns a clons of all recipes
     * @return All recipes
     */
    public final Set<Recipe> getRecipes() {
        return new HashSet<>(recipes);
    }

    

}
