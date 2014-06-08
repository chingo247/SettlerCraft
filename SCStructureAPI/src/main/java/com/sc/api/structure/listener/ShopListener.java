/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listener;

import com.cc.plugin.api.menu.Menu;
import com.cc.plugin.api.menu.MenuManager;
import com.cc.plugin.api.menu.MenuSlot;
import com.cc.plugin.api.menu.ShopCategoryMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 *
 * @author Chingo
 */
public class ShopListener implements Listener {

    @EventHandler
    public void onPlayerLeavesShop(InventoryCloseEvent ice) {
//        if (ice.getInventory().getTitle().hasMenu(":")) {
            String title = ice.getInventory().getTitle();
            if (MenuManager.getInstance().hasMenu(title)) {
                Menu menu = MenuManager.getInstance().getMenu(title);
                if (ice.getPlayer() instanceof Player) {
                    if (menu instanceof ShopCategoryMenu) {
                        ShopCategoryMenu iscm = (ShopCategoryMenu) menu;
                        iscm.playerLeave((Player) ice.getPlayer());
                        
                    }
                }
            }
//        }
    }
    

    

    @EventHandler
    public void onMenuSlotClicked(InventoryClickEvent ice) {
//
//        System.out.println("Inventory Click!");
            String title = ice.getInventory().getTitle().trim();
            if(MenuManager.getInstance().hasMenu(title) && (ice.getClick() == ClickType.RIGHT || ice.getClick() == ClickType.SHIFT_RIGHT || ice.getClick() == ClickType.SHIFT_LEFT)) { 
                ice.setCancelled(true);
            }
            if (MenuManager.getInstance().hasMenu(title) && ice.getRawSlot() < Menu.MENUSIZE && ice.getRawSlot()>= 0) {
                ice.setCancelled(true);
                
                Menu menu = MenuManager.getInstance().getMenu(title);
//                System.out.println("Menu: " + menu.getTitle());
                if (ice.getWhoClicked() instanceof Player) {
                    if (menu instanceof ShopCategoryMenu) {
                        ShopCategoryMenu iscm = (ShopCategoryMenu) menu;
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
