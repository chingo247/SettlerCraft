package com.sc.api.structure.vendor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//import com.cc.plugin.scshop.Shop;
import com.settlercraft.core.util.ShopUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Chingo
 */
public class PlanShop {
    
    

    static final ItemStack[] StructureCategories = {
        ShopUtil.createCategory("All", new ItemStack(Material.NETHER_STAR)),
        ShopUtil.createCategory("General", new ItemStack(Material.WORKBENCH)),
        ShopUtil.createCategory("Houses", new ItemStack(Material.BED)),
        ShopUtil.createCategory("Shops", new ItemStack(Material.GOLD_INGOT)),
        ShopUtil.createCategory("Temples", new ItemStack(Material.QUARTZ_BLOCK)),
        ShopUtil.createCategory("Castles", new ItemStack(Material.SMOOTH_BRICK)),
        ShopUtil.createCategory("Tower", new ItemStack(Material.LADDER)),
        ShopUtil.createCategory("Dungeons&Arenas", new ItemStack(Material.IRON_SWORD)),
        ShopUtil.createCategory("Misc", new ItemStack(Material.RECORD_10))
    };
    
    public static  String title = "Plan Shop - Category: " + StructureCategories[0].getItemMeta().getDisplayName();

//    public PlanShop(Collection<ItemStack> items) {
//        super(title, StructureCategories, items);
//    }
//
//    public PlanShop(int page, Collection<ItemStack> items) {
//        super(page, title, StructureCategories, items);
//    }
    

    public String getCategory(int category) {
        System.out.println("category: " + category);
        return StructureCategories[category].getItemMeta().getDisplayName();
    }

}
