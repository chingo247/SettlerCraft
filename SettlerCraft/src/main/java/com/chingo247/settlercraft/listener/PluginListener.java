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
package com.chingo247.settlercraft.listener;

import com.chingo247.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.plugin.SettlerCraft;
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
            SettlerCraft.getInstance().getExecutorService().shutdown();
            HibernateUtil.shutdown();
        }
    }

}
