/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

//import com.sc.api.structure.vendor.PlanShop;
import com.cc.plugin.scshop.CategoryShop;
import com.cc.plugin.scshop.Shop;
import com.cc.plugin.scshop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 *
 * @author Chingo
 */
public class InventoryListener implements Listener {

    @EventHandler()
    public void onShopInventoryClicked(InventoryClickEvent ice) {
        String shopName = ice.getInventory().getTitle();
        if (ShopManager.getInstance().contains(shopName)) {
            Player player = (Player)ice.getWhoClicked();
            Shop shop = ShopManager.getInstance().getShop(shopName);
            ice.setCancelled(shop.isInfinite());
            if(shop instanceof CategoryShop) {
                
                CategoryShop cs = (CategoryShop) shop;
                if(cs.isCategorySlot(ice.getSlot())) {
                    cs.visit(player, ice.getCurrentItem().getItemMeta().getDisplayName());
                } else if(!cs.isReserved(ice.getSlot()) && ice.getCurrentItem() != null) {
                    player.getInventory().addItem(ice.getCurrentItem());
                }
                
                
            }
        }
    }
    
    @EventHandler
    public void onVisitorLeaves(InventoryCloseEvent ice) {
        String shopName = ice.getInventory().getTitle();
        if (ShopManager.getInstance().contains(shopName)) {
            Player player = (Player)ice.getPlayer();
            Shop shop = ShopManager.getInstance().getShop(shopName);
            if(shop instanceof CategoryShop) {
                CategoryShop cs = (CategoryShop) shop;
                cs.leave(player);
            }
        }
    }

}
