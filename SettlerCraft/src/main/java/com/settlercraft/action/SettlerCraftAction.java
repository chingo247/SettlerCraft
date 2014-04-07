/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.action;

import com.settlercraft.plugin.SettlerCraft;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public abstract class SettlerCraftAction {
    
    protected final SettlerCraft sc;
    
    protected SettlerCraftAction() {
        this.sc = (SettlerCraft) Bukkit.getPluginManager().getPlugin(SettlerCraft.name);
    }
    
}
