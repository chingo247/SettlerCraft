/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.structure.event.LayerCompleteEvent;
import com.sc.api.structure.event.PlayerBuildEvent;
import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.listeners.PlayerListener;
import com.sc.api.structure.listeners.StructureListener;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.sc.api.structure.recipe.Recipes;
import com.settlercraft.core.SettlerCraftModule;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
import com.settlercraft.core.persistence.StructureProgressService;
import com.settlercraft.recipe.CShapedRecipe;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureModule extends SettlerCraftModule {

    public SCStructureModule() {
        super("SCStructureAPI");
    }

    @Override
    protected void setupListeners(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new StructureListener(), plugin);
    }

    @Override
    protected void setupRecipes(JavaPlugin plugin) {
        for (CShapedRecipe r : Recipes.getRecipes()) {
            plugin.getServer().addRecipe(r.getRecipe());
        }
    }

    /**
     * Read and loads all structures in the datafolder of the plugin
     *
     * @param baseFolder The datafolder of the plugin
     */
    private void loadStructures(File baseFolder) {
        if (!baseFolder.exists()) {
            baseFolder.mkdir();
        }
        try {
            StructurePlanLoader spLoader = new StructurePlanLoader();
            spLoader.load(baseFolder);
        }
        catch (InvalidStructurePlanException ex) {
            Logger.getLogger(SCStructureModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void init(JavaPlugin plugin) {
        loadStructures(plugin.getDataFolder().getAbsoluteFile());
        setupListeners(plugin);
        setupRecipes(plugin);
    }

    public static void build(Player player, Structure structure) {
        StructureProgressService structureProgressService = new StructureProgressService();
        List<MaterialResource> resources = structure.getProgress().getResources();
        Iterator<MaterialResource> lit = resources.iterator();
        
        while (lit.hasNext()) {
            MaterialResource materialResource = lit.next();
            
            for (ItemStack stack : player.getInventory()) {
                if (materialResource != null && stack != null && materialResource.getMaterial() == stack.getType()) {
                    int removed = structureProgressService.resourceTransaction(materialResource, Math.min(stack.getAmount(), 5));
                    if (removed > 0) {
                        // Remove items from player inventory
                        ItemStack removedIS = new ItemStack(stack);
                        removedIS.setAmount(removed);
                        player.getInventory().removeItem(removedIS);
                        player.updateInventory();
                        player.sendMessage(ChatColor.YELLOW + "[SC]: " + removed + " " + removedIS.getType() + " has been removed from your inventory");
                        
                        System.out.println(structure.getProgress());
                        // Layer Complete?
                        if (structure.getProgress().getResources().isEmpty()) {
                            int completedLayer = structure.getProgress().getLayer();
                            structureProgressService.nextLayer(structure);
                            Bukkit.getPluginManager().callEvent(new LayerCompleteEvent(structure, completedLayer));
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerBuildEvent(structure, player, removedIS));
                    }
                    return;
                }
            }
        }
    }

    public static Builder getBuilder() {
        for (World world : Bukkit.getServer().getWorlds()) {
            world.getWorldFolder().lastModified();
        }
        return new Builder();
    }

}
