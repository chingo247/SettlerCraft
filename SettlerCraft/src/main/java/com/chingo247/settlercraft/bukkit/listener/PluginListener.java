/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit.listener;

import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.AsyncStructureAPI;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 *
 * @author Chingo
 */
public class PluginListener implements Listener {

    @EventHandler
    public void shutdown(PluginDisableEvent pde) {
        if (pde.getPlugin().getName().equals(SettlerCraft.getInstance().getName())) {
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + " Shutting down...");
            AsyncStructureAPI.getInstance().shutdown();
            StructurePlanManager.getInstance().shutdown();
            HibernateUtil.shutdown();
        }
    }

}
