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
package com.sc.api.structure;

import com.sc.api.structure.progress.ConstructionStrategyType;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import java.util.Iterator;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Chingo
 */
public class SyncPlaceTask extends BukkitRunnable {

    private final Iterator<Vector> it;
    private final int blocksPerSecond;
    private final Location location;
    private final EditSession editSession;
    private final CuboidClipboard whole;
    private final Callback callback;

    public SyncPlaceTask(EditSession session, CuboidClipboard whole, Location location,int blocksPerSecond, Callback callback) {
        this.whole = whole;
        this.it = ConstructionStrategyType.LAYERED.getList(whole).iterator();
        this.editSession = session;
        this.blocksPerSecond = blocksPerSecond;
        this.location = location;
        this.callback = callback;
    }

    @Override
    public void run() {
        for (int i = 0; i < blocksPerSecond; i++) {
            if (it.hasNext()) {
                Vector v = it.next();
                editSession.rawSetBlock(location.getPosition().add(v), whole.getBlock(v));
            } else {
                callback.onComplete();
                this.cancel();
                break;
            }
        }
    }

    public interface Callback {
        public void onComplete();
    }

}
