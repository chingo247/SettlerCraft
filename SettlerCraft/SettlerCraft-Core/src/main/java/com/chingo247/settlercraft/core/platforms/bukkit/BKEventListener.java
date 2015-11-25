/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.core.platforms.bukkit;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.IEventDispatcher;
import com.chingo247.settlercraft.core.event.PlayerLoginEvent;
import com.chingo247.settlercraft.core.event.PlayerLogoutEvent;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chingo
 */
public class BKEventListener implements Listener {
    
    @EventHandler
    public void onPlayerLogout(org.bukkit.event.player.PlayerQuitEvent pqe) {
        IEventDispatcher dispatcher = SettlerCraft.getInstance().getEventDispatcher();
        dispatcher.dispatchEvent(new PlayerLogoutEvent(new BukkitPlayer(pqe.getPlayer())));
    }
    
    @EventHandler
    public void onPlayerLogin(org.bukkit.event.player.PlayerLoginEvent pqe) {
        IEventDispatcher dispatcher = SettlerCraft.getInstance().getEventDispatcher();
        dispatcher.dispatchEvent(new PlayerLoginEvent(new BukkitPlayer(pqe.getPlayer())));
    }
    
}
