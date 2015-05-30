/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit;

import com.chingo247.settlercraft.structureapi.platforms.services.AsyncEditSessionFactoryProvider;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

/**
 *
 * @author Chingo
 */
class BKAsyncEditSessionFactoryProvider implements AsyncEditSessionFactoryProvider {
    
    private WorldEditPlugin plugin;
    private AsyncEditSessionFactory factory;

    public BKAsyncEditSessionFactoryProvider() {
        this.plugin = AsyncWorldEditMain.getWorldEdit(AsyncWorldEditMain.getInstance());
        this.factory = new AsyncEditSessionFactory(plugin, AsyncWorldEditMain.getInstance(), WorldEdit.getInstance().getEventBus());
    }
    
    

    @Override
    public AsyncEditSessionFactory getFactory() {
        return factory;
    }
    
    
    
}
