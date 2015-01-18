/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.common.platforms.bukkit;

import com.chingo247.settlercraft.common.core.IScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class BukkitScheduler implements IScheduler {
    
    private final org.bukkit.scheduler.BukkitScheduler scheduler;
    private final Plugin plugin;

    public BukkitScheduler(Server server, Plugin plugin) {
        this.scheduler = server.getScheduler();
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable runnable) {
        scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void run(Runnable runnable) {
        scheduler.runTask(plugin, runnable);
    }

    @Override
    public void runLater(int delay, Runnable runnable) {
        scheduler.runTaskLater(plugin, runnable, delay);
    }

    @Override
    public void runLaterAsync(int delay, Runnable runnable) {
        scheduler.runTaskLaterAsynchronously(plugin, runnable, delay);
    }
    
    
    
}
