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
package com.sc.module.structureapi.construction.sync;

import com.sc.module.structureapi.construction.worldedit.StructureBlock;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.PriorityQueue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Chingo
 */
public class SyncTask extends BukkitRunnable {

    private final int blocksPerSecond;
    private final Vector location;
    private final EditSession editSession;
    private final CuboidClipboard whole;
    private final TaskCompleteCallback callback;
    private final PriorityQueue<StructureBlock> queue;

    SyncTask(EditSession session, CuboidClipboard whole, Vector location, int blocksPerInterval, TaskCompleteCallback callback) {
        this.whole = whole;
        this.queue = new PriorityQueue();
        for(int x = 0; x < whole.getWidth(); x++) {
            for(int z = 0; z < whole.getLength(); z++) {
                for(int y = 0; y < whole.getHeight(); y++) {
                    BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = whole.getBlock(v);
                    if(b == null) {
                        continue;
                    }
                    queue.add(new StructureBlock(v, whole.getBlock(v)));
                } 
            }
        }
        
        
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
                editSession.rawSetBlock(location.add(b.getPosition()), b.getBlock());
            } else {
                if(callback != null) {
                    callback.onComplete();
                }
                this.cancel();
                break;
            }
        }
    }

    
}
