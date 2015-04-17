/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.menuapi.menu;

import com.chingo247.menuapi.menu.exception.MenuAPIException;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.services.IEconomyProvider;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class MenuAPI {
    
    private final Map<UUID, ACategoryMenu> openMenus;
    private  IEconomyProvider economyProvider;
    private  APlatform platform;
    
    private static MenuAPI instance;
    
    private MenuAPI() {
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.openMenus = Maps.newHashMap();
    }
    
    
    public static MenuAPI getInstance() {
        if(instance == null) {
            instance = new MenuAPI();
        }
        return instance;
    }
    
    public void registerEconomyService(IEconomyProvider iEconomyProvider) throws MenuAPIException {
        if(economyProvider != null) {
            throw new MenuAPIException("Already registered an Economy Provider!");
        }
        this.economyProvider = iEconomyProvider;
    }
    
    public APlatform getPlatform() {
        return platform;
    }
   
    
    void registerMenu(ACategoryMenu menu) {
        synchronized(openMenus) {
            this.openMenus.put(menu.getPlayer().getUniqueId(), menu);
        }
    }
    
    public final ACategoryMenu getMenu(UUID player) {
        synchronized(openMenus) {
            return openMenus.get(player);
        }
    }
    
    private void unRegisterMenu(UUID player) {
        synchronized(openMenus) {
            openMenus.remove(player);
        }
    }

    public IEconomyProvider getEconomyProvider() {
        return economyProvider;
    }
    
    protected final void onPlayerLeave(UUID player) {
        unRegisterMenu(player);
    }
    
    protected final void onServerReload() {
        for(Iterator<ACategoryMenu> it = openMenus.values().iterator(); it.hasNext();) {
            ACategoryMenu menu = it.next();
            if(menu != null) {
                menu.close("Server is reloading...");
                it.remove();
            }
        }
    }
    
    protected final boolean onPlayerClick(int slot, UUID player, int clickType, AItemStack clicked) {
        if(clicked == null) return false;
        
        ACategoryMenu menu = openMenus.get(player);
        if(menu != null) {
            return menu.onMenuSlotClicked(slot, clickType, clicked);
        } else {
            return false;
        }
    }
    
}
