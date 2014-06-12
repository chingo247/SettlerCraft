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
package com.sc.structure.sync;

import com.sc.StructureBlock;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Chingo
 */
public class SyncPlaceTask extends BukkitRunnable {

    private final int blocksPerSecond;
    private final Location location;
    private final EditSession editSession;
    private final CuboidClipboard whole;
    private final PlaceCallback callback;
    private final StructureBlockQueue queue;

    SyncPlaceTask(EditSession session, CuboidClipboard whole, Location location, int blocksPerInterval) {
        this(session, whole, location, blocksPerInterval, null);
    }

    SyncPlaceTask(EditSession session, CuboidClipboard whole, Location location, int blocksPerInterval, PlaceCallback callback) {
        this.whole = whole;
        this.queue = new StructureBlockQueue(whole);
        this.editSession = session;
        this.blocksPerSecond = blocksPerInterval;
        this.location = location;
        this.callback = callback;
    }

    @Override
    public void run() {
        for (int i = 0; i < blocksPerSecond; i++) {
            if(queue.peek() != null) {
                StructureBlock b = queue.poll();
                editSession.rawSetBlock(location.getPosition().add(b.getPosition()), b.getBlock());
            } else {
                if(callback != null) {
                    callback.onComplete();
                }
                this.cancel();
                break;
            }
        }
    }

    public interface PlaceCallback {
        public void onComplete();
    }

}
