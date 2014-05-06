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

/**
 *
 * @author Chingo
 */
public class ShopListener implements Listener {

    @EventHandler
    public void onPlayerLeavesShop(InventoryCloseEvent ice) {
//        if (ice.getInventory().getTitle().contains(":")) {
            String title = ice.getInventory().getTitle();
            if (MenuManager.getInstance().contains(title)) {
                Menu menu = MenuManager.getInstance().getMenu(title);
                if (ice.getPlayer() instanceof Player) {
                    if (menu instanceof ItemShopCategoryMenu) {
                        ItemShopCategoryMenu iscm = (ItemShopCategoryMenu) menu;
                        iscm.playerLeave((Player) ice.getPlayer());
                    }
                }
            }
//        }
    }

    @EventHandler
    public void onMenuSlotClicked(InventoryClickEvent ice) {
//        System.out.println("Inventory Click!");
//        if (ice.getInventory().getTitle().contains(":")) {
            String title = ice.getInventory().getTitle().trim();
            if (MenuManager.getInstance().contains(title) && ice.getSlot() < Menu.MENUSIZE && ice.getSlot() >= 0) {
                ice.setCancelled(true);
                Menu menu = MenuManager.getInstance().getMenu(title);
//                System.out.println("Menu: " + menu.getTitle());
                if (ice.getWhoClicked() instanceof Player) {
                    if (menu instanceof ItemShopCategoryMenu) {
                        ItemShopCategoryMenu iscm = (ItemShopCategoryMenu) menu;
                        MenuSlot slot = iscm.getSlot(ice.getSlot());
                        if (slot != null) {
                            iscm.onMenuSlotClicked(slot, (Player) ice.getWhoClicked());
                        }
                    }
                }
            }
//        }

    }

}
