/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.settlercraft.listener;

import org.bukkit.event.Listener;

/**
 *
 * @author Chingo
 */
public class ShopListener implements Listener {
//
//    @EventHandler
//    public void onPlayerLeavesShop(InventoryCloseEvent ice) {
//        String title = ice.getInventory().getTitle();
//        if (MenuManager.getInstance().hasMenu(title)) {
//            Menu menu = MenuManager.getInstance().getMenu(title);
//            if (ice.getPlayer() instanceof Player) {
//                if (menu instanceof ShopCategoryMenu) {
//                    ShopCategoryMenu iscm = (ShopCategoryMenu) menu;
//                    iscm.playerLeave((Player) ice.getPlayer());
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onMenuSlotClicked(InventoryClickEvent ice) {
//        String title = ice.getInventory().getTitle().trim();
//        if (MenuManager.getInstance().hasMenu(title)
//                && (ice.getClick() == ClickType.RIGHT 
//                || ice.getClick() == ClickType.SHIFT_RIGHT 
//                || ice.getClick() == ClickType.SHIFT_LEFT 
//                || ice.getRawSlot() < Menu.MENUSIZE && ice.getRawSlot() >= 0
//                || ice.getClick() == ClickType.DOUBLE_CLICK 
//                ))  {
//            ice.setCancelled(true);
//        }
//
//        if (MenuManager.getInstance().hasMenu(title)
//                && ice.getRawSlot() < Menu.MENUSIZE && ice.getRawSlot() >= 0) {
//            Menu menu = MenuManager.getInstance().getMenu(title);
//            ice.setCancelled(true);
//            ItemStack stack = ice.getCursor();
//            
//            
//            if (stack != null
//                    && stack.getType() != Material.AIR
//                    && stack.getAmount() > 0) {
//                
//                if (menu instanceof ShopCategoryMenu) {
//                    ShopCategoryMenu scm = (ShopCategoryMenu) menu;
//                    if(scm.sellItem(stack, (Player) ice.getWhoClicked())) {
//                       ice.getWhoClicked().setItemOnCursor(null);
//                    }
//                }
//                return;
//            }
//        }
//
//        if (MenuManager.getInstance().hasMenu(title) && ice.getRawSlot() < Menu.MENUSIZE && ice.getRawSlot() >= 0) {
//            ice.setCancelled(true);
//
//            Menu menu = MenuManager.getInstance().getMenu(title);
//            if (ice.getWhoClicked() instanceof Player) {
//                if (menu instanceof ShopCategoryMenu) {
//                    ShopCategoryMenu iscm = (ShopCategoryMenu) menu;
//                    MenuSlot slot = iscm.getSlot(ice.getSlot());
//
//                    if (slot != null) {
//                        iscm.onMenuSlotClicked(slot, (Player) ice.getWhoClicked());
//                    }
//                }
//
//            }
//        }
//    }

}
