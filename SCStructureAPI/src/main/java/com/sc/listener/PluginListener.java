/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.listener;

import com.cc.plugin.api.menu.MenuManager;
import com.sc.plugin.SettlerCraft;
import com.sc.api.structure.StructureManager;
import org.apache.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * Ensures that players won't be able to buy plans for free at the shop, by closing their inventory
 * if they were visiting a shop
 * @author Chingo
 */
public class PluginListener implements Listener {
    
    private final Logger LOGGER = Logger.getLogger(PluginListener.class);
    
    @EventHandler
    public void onReload(PluginDisableEvent disableEvent) {
         String plugin = disableEvent.getPlugin().getName();
         if(plugin.equals(SettlerCraft.getSettlerCraft().getName())) {
             MenuManager.getInstance().clearVisitors();
             StructureManager.getInstance().shutdown();
             LOGGER.debug("StructureManager shutting down");
         }
    }
    
    
    
}
