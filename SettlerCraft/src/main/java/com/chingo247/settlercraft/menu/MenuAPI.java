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

package com.chingo247.settlercraft.menu;

import com.chingo247.settlercraft.SettlerCraft;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class MenuAPI {
    
    private final Map<UUID, ACategoryMenu> openMenus;
    
    protected MenuAPI() {
        openMenus = Maps.newHashMap();
    }
    
    public void registerMenu(ACategoryMenu menu) {
        synchronized(openMenus) {
            this.openMenus.put(menu.getPlayer().getUniqueId(), menu);
        }
        menu.register(this);
    }
    
    public ACategoryMenu getMenu(UUID player) {
        synchronized(openMenus) {
            return openMenus.get(player);
        }
    }
    
    private void unRegisterMenu(UUID player) {
        synchronized(openMenus) {
            openMenus.remove(player);
        }
    }
    
    protected void onPlayerLeave(UUID player) {
        unRegisterMenu(player);
    }
    
    protected void onServerReload() {
        for(Iterator<ACategoryMenu> it = openMenus.values().iterator(); it.hasNext();) {
            ACategoryMenu menu = it.next();
            if(menu != null) {
                menu.close("Server is reloading...");
                it.remove();
            }
        }
    }
    
    protected void onPlayerClick(int slot, UUID player) {
        SettlerCraft.getInstance().getWorld(null).
    }
    
    protected void onPlayerCloseInventory(UUID player) {
        unRegisterMenu(player);
    }
    
}
