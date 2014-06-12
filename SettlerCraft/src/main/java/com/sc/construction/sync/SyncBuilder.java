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
package com.sc.construction.sync;

import com.sc.plugin.SettlerCraft;
import com.sc.util.Ticks;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Chingo
 */
public class SyncBuilder {
    
    public static BukkitTask placeBuffered(final EditSession editSession, 
            final CuboidClipboard whole, final Vector location, int blocksPerSecond, TaskCompleteCallback callback) {
         SyncPlaceTask task = new SyncPlaceTask(editSession, whole, location, blocksPerSecond, callback);
         return task.runTaskTimer(SettlerCraft.getSettlerCraft(), Ticks.ONE_SECOND, Ticks.ONE_SECOND);
    }

    public static BukkitTask placeLayered(final EditSession editSession, final CuboidClipboard whole, final Vector location, TaskCompleteCallback callback) {
        return placeBuffered(editSession, whole, location, whole.getLength() * whole.getWidth() , callback);
    }



}
