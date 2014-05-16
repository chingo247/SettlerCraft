/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
