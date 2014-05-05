package com.settlercraft.model.recipe;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * A recipe class make new recipes with display
 * @author Chingo
 */
public class CShapedRecipe implements Comparable<CShapedRecipe> {
    private final ShapedRecipe recipe;
 
    public CShapedRecipe(ItemStack result, String displayname) {
        ItemMeta im = result.getItemMeta();
        im.setDisplayName(displayname);
        result.setItemMeta(im);
        this.recipe = new ShapedRecipe(result);
    }
    
    public CShapedRecipe(ItemStack result, String displayname, List<String> lore) {
        ItemMeta im = result.getItemMeta();
        im.setLore(lore);
        im.setDisplayName(displayname);
        result.setItemMeta(im);
        this.recipe = new ShapedRecipe(result);
    }
    
    public String getDisplayName() {
        return recipe.getResult().getItemMeta().getDisplayName();
    }

    public final CShapedRecipe shape(String... shape) {
        this.recipe.shape(shape);
        return this;
    }

    public final CShapedRecipe setIngredient(char key, MaterialData ingredient) {
        this.recipe.setIngredient(key, ingredient);
        return this;
    }

    public final CShapedRecipe setIngredient(char key, Material ingredient) {
        this.recipe.setIngredient(key, ingredient);
        return this;
    }

    public final Map<Character, ItemStack> CShapedRecipe() {
        return recipe.getIngredientMap();
    }

    public final String[] getShape() {
        return recipe.getShape();
    }

    public final ItemStack getResult() {
        return recipe.getResult();
    }
    
    public final ShapedRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public int compareTo(CShapedRecipe o) {
        return this.getDisplayName().compareTo(o.getDisplayName());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof CShapedRecipe)) return false;
        CShapedRecipe rp = (CShapedRecipe) obj;
        if(rp == this) return true;
        return rp.getDisplayName().equals(this.getDisplayName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.recipe.getResult().getItemMeta().getDisplayName());
        return hash;
    }
    
    
    
    
    
    
}