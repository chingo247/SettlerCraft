/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

//import com.sc.api.structure.vendor.PlanShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Chingo
 */
public class InventoryListener implements Listener {

    @EventHandler()
    public void onShopInventoryClicked(InventoryClickEvent ice) {
       
//        if (ice.getCurrentItem() instanceof MenuSlot) {
//            ItemStack cat = ice.getInventory().getContents()[0]; // Reserved Slot, ALWAYS FILLED
//            if (cat instanceof MenuCategorySlot) {
//                MenuCategorySlot slot = (MenuCategorySlot) cat;
//                Menu menu = MenuManager.getInstance().getShop(slot.getMenuId());
//                if (menu instanceof ItemShopCategoryMenu) {
//                    ItemShopCategoryMenu iscm = (ItemShopCategoryMenu) menu;
//                    Player player = (Player) ice.getWhoClicked();
//                    ice.setCancelled(menu.getWontDeplete());
//
//                    if (ice.getCurrentItem() instanceof MenuActionSlot) {
//                        String action = ((MenuActionSlot)ice.getCurrentItem()).getName();
//                        if(action.equalsIgnoreCase("next")) {
//                            iscm.nextPage(player);
//                        } else if(action.equalsIgnoreCase("previous")) {
//                            iscm.prevPage(player);
//                        } else {
//                            throw new UnsupportedOperationException("No action known for " + action);
//                        }
//                        
//                    } else if(ice.getCurrentItem() instanceof MenuCategorySlot) {
//                        iscm.changeCategory(player, ((MenuCategorySlot)ice.getCurrentItem()).getName());
//                    } else if(ice.getCurrentItem() instanceof MenuItem) {
//                        iscm.sellItem(player, (MenuItem) ice.getCurrentItem());
//                    } else if(ice.getCurrentItem() instanceof MenuSkillSlot) {
//                        throw new UnsupportedOperationException("Not supported yet");
//                    } else {
//                        throw new UnsupportedOperationException("Not supported yet");
//                    }
//                }
//            }
//
//        }
    }

}
