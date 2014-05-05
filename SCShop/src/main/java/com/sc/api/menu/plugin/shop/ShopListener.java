/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class ShopListener implements Listener {
    
    @EventHandler
    public void onPlayerLeavesShop(InventoryCloseEvent ice) {
        if(ice.getPlayer() instanceof Player) {
            MenuManager.getInstance().removeVisitorFromShop((Player)ice.getPlayer());
        }
    }
    
    @EventHandler
    public void onMenuSlotClicked(InventoryClickEvent ice) {
        System.out.println("Inventory Click!");
        ItemStack stack = ice.getInventory().all(ice.getCurrentItem()).get(ice.getSlot());
        System.out.println(stack + ": " + (stack instanceof MenuSlot));
        
        if(stack instanceof MenuSlot && (ice.getWhoClicked() instanceof Player)) {
            MenuSlot s = (MenuSlot) ice.getCurrentItem();
            Menu menu = MenuManager.getInstance().getMenu(s.getMenuId());
            if(menu instanceof ItemShopCategoryMenu) {
                ItemShopCategoryMenu iscm = (ItemShopCategoryMenu) menu;
                if(s instanceof MenuItemSlot) {
                   if(iscm.wontDeplete) {
                       ice.setCancelled(true);
                   }
                   iscm.onItemClicked((MenuItemSlot) s, (Player) ice.getWhoClicked());
                } else if(s instanceof MenuActionSlot) {
                   ice.setCancelled(true);
                   iscm.onActionClicked((MenuActionSlot)s, (Player) ice.getWhoClicked());
                } else if(s instanceof MenuCategorySlot) {
                   ice.setCancelled(true);
                   iscm.onCategoryClicked((MenuCategorySlot) s, (Player) ice.getWhoClicked());
                } else {
                    throw new AssertionError("Unreachable");
                }
            }
        }
    }
    
}
