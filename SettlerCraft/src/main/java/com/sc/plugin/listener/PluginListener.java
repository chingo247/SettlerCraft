/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.listener;

import com.sc.api.structure.construction.StructureManager;
import com.sc.plugin.SettlerCraft;
import com.sc.plugin.menu.MenuManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * Ensures that players won't be able to buy plans for free at the shop, by closing their inventory
 * if they were visiting a shop
 * @author Chingo
 */
public class PluginListener implements Listener {
    
    @EventHandler
    public void onReload(PluginDisableEvent disableEvent) {
         String plugin = disableEvent.getPlugin().getName();
         if(plugin.equals(SettlerCraft.getSettlerCraft().getName())) {
             MenuManager.getInstance().clearVisitors();
             StructureManager.getInstance().stopAll();
         }
    }
    
}
