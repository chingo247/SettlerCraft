/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SettlerCraftTools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import com.sc.api.menu.plugin.shop.ItemShopCategoryMenu;
import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.api.structure.SCStructureAPI;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.persistence.StructurePlanService;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    public static final String PLANSHOP = "Buy & Build"; // Unique Identifier for shop
    


    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("SCStructureAPI") == null) {
            System.out.println("Couldn't find SCStructureAPI, DISABLING SettlerCraft");
            this.setEnabled(false);
            return;
        }
        if(Bukkit.getPluginManager().getPlugin("SCMenu") == null) {
            System.out.println("Couldn't find SCMenu, DISABLING SettlerCraft");
            this.setEnabled(false);
            return;
        }
        
        SCStructureAPI.loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));
        initPlanShop();
    }
    
    private static void initPlanShop() {
        ItemShopCategoryMenu planShop = new ItemShopCategoryMenu(PLANSHOP, true, true);

        // Add Plan Categories
        planShop.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planShop.addCategory(1, new ItemStack(Material.WORKBENCH), "General", "Town Center");
        planShop.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planShop.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planShop.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planShop.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planShop.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Castles", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep");
        planShop.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planShop.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planShop.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planShop.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planShop.setLocked(10, 11, 12, 13, 14, 15, 16);
        planShop.setDefaultCategory("All");
        planShop.setChooseDefaultCategory(true);
         
        StructurePlanService planService = new StructurePlanService();
        for(StructurePlan plan : planService.getPlans()) {
            planShop.addItem(new ItemStack(Material.PAPER), plan.getDisplayName(), plan.getPrice(), plan.getCategory());
        }

        MenuManager.getInstance().register(planShop);
    }

    



}
