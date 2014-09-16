/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.settlercraft.listener;

import com.sc.module.settlercraft.plugin.SettlerCraft;
import org.apache.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * @author Chingo
 */
public class PluginListener implements Listener {
    
    private final Logger LOGGER = Logger.getLogger(PluginListener.class);
    
    @EventHandler
    public void onReload(PluginDisableEvent disableEvent) {
         String plugin = disableEvent.getPlugin().getName();
         if(plugin.equals(SettlerCraft.getInstance().getName())) {
//             SettlerCraft.getInstance().stop();
             LOGGER.debug("StructureManager shutting down");
         }
    }
    
    
    
}
