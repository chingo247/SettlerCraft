/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.structure.sync;

import com.sc.structure.construction.ConstructionStrategyType;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Chingo
 */
public class SyncClearTask extends BukkitRunnable {
    private final Iterator<Vector> it;
    private final int blocksPerSecond;
    private final Location location;
    private final EditSession editSession;
    private final CuboidClipboard whole;
    private final ClearCallback callback;

    SyncClearTask(EditSession session, CuboidClipboard whole, Location location, int blocksPerInterval) {
        this(session, whole, location, blocksPerInterval, null);
    }

    SyncClearTask(EditSession session, CuboidClipboard whole, Location location, int blocksPerInterval, ClearCallback callback) {
        this.whole = whole;
        List<Vector> vertices = ConstructionStrategyType.LAYERED.getList(whole);
        Collections.reverse(vertices);
        this.it = vertices.iterator();
        this.editSession = session;
        this.blocksPerSecond = blocksPerInterval;
        this.location = location;
        this.callback = callback;
    }

    @Override
    public void run() {
        for (int i = 0; i < blocksPerSecond; i++) {
            if (it.hasNext()) {
                Vector v = it.next();
                editSession.rawSetBlock(location.getPosition().add(v), new BaseBlock(0));
            } else {
                if(callback != null) {
                    callback.onComplete();
                }
                this.cancel();
                break;
            }
        }
    }

    public interface ClearCallback {
        public void onComplete();
    }
}
