/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SettlerCraftTools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.persistence.service.StructurePlanService;
import com.sc.api.structure.util.CuboidUtil;
import com.sc.plugin.commands.SettlerCraftCommandExecutor;
import com.sc.plugin.menu.MenuManager;
import com.sc.plugin.menu.MenuSlot;
import com.sc.plugin.menu.shop.ItemShopCategoryMenu;
import com.sc.plugin.menu.shop.ShopListener;
import com.sk89q.worldedit.CuboidClipboard;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {
    
    public static final String PLAN_MENU_NAME = "Plan Menu";
    private static ItemShopCategoryMenu planMenu;
    public static final String PLANSHOP = "Buy & Build"; // Unique Identifier for shop
    private static ItemShopCategoryMenu planShop;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("SCStructureAPI") == null) {
            System.out.println("Couldn't find SCStructureAPI, DISABLING SettlerCraft");
            this.setEnabled(false);
            return;
        }

        setupMenu();
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupPlanShop();
        }

        Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor());
    }

    private static void setupMenu() {
        planMenu = new ItemShopCategoryMenu(PLAN_MENU_NAME, true, true, new ItemShopCategoryMenu.ShopCallback() {

            @Override
            public void onItemSold(final Player buyer, final ItemStack stack, final double price) {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("[Value]: " + ChatColor.GOLD + " " + price);
                lore.add("[Type]: " + ChatColor.GOLD + "PLAN");
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }
        });

        // Add Plan Categories
        planMenu.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planMenu.addCategory(1, new ItemStack(Material.WORKBENCH), "General", "Town Center");
        planMenu.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planMenu.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planMenu.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planMenu.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planMenu.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planMenu.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planMenu.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planMenu.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planMenu.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planMenu.setLocked(10, 11, 12, 13, 14, 15, 16);
        planMenu.setDefaultCategory("All");
        planMenu.setChooseDefaultCategory(true);

        StructurePlanService planService = new StructurePlanService();
        for (StructurePlan plan : planService.getPlans()) {
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            CuboidClipboard cc = plan.getSchematic();
            int size = CuboidUtil.count(cc, true);
            String sizeString = size < 999 ? String.valueOf(size) : ((Math.round(size / 1000)) + "K");

            slot.setData("Size", cc.getLength() + "x" + cc.getWidth() + "x" + cc.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            planMenu.addItem(slot, plan.getCategory(), 0f); // FOR FREEEE
        }

        MenuManager.getInstance().register(planMenu);
    }

    private static void setupPlanShop() {
        planShop = new ItemShopCategoryMenu(PLANSHOP, true, true, new ItemShopCategoryMenu.ShopCallback() {

            @Override
            public void onItemSold(final Player buyer, final ItemStack stack, final double price) {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("[Value]: " + ChatColor.GOLD + " " + price);
                lore.add("[Type]: " + ChatColor.GOLD + "PLAN");
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }
        });

        // Add Plan Categories
        planShop.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planShop.addCategory(1, new ItemStack(Material.WORKBENCH), "General", "Town Center");
        planShop.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planShop.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planShop.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planShop.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planShop.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planShop.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planShop.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planShop.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planShop.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planShop.setLocked(10, 11, 12, 13, 14, 15, 16);
        planShop.setDefaultCategory("All");
        planShop.setChooseDefaultCategory(true);

        StructurePlanService planService = new StructurePlanService();
        for (StructurePlan plan : planService.getPlans()) {
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            CuboidClipboard cc = plan.getSchematic();
            int size = CuboidUtil.count(cc, true);
            String sizeString = size < 999 ? String.valueOf(size) : ((Math.round(size / 1000)) + "K");

            slot.setData("Size", cc.getLength() + "x" + cc.getWidth() + "x" + cc.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            planShop.addItem(slot, plan.getCategory(), plan.getPrice());
        }

        MenuManager.getInstance().register(planShop);
    }

}
