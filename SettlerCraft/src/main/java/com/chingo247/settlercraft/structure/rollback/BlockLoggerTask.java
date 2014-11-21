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
package com.chingo247.settlercraft.structure.rollback;

import com.chingo247.settlercraft.structure.persistence.orientdb.document.BlockDatabase;
import com.chingo247.settlercraft.structure.persistence.orientdb.document.BlockDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.PrimeSoft.blocksHub.blocklogger.IBlockLogger;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class BlockLoggerTask extends TimerTask implements IBlockLogger {
    private static final int MAX_BATCH = 100 * 1000; // 100k
    private final Queue<BlockDocument> queue = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();
    private final boolean enabled;
    private final BlockDatabase database;
    private final ODatabaseDocumentTx dbtx;

    public BlockLoggerTask(BlockDatabase database)  {
        this.enabled = true;
        this.database = database;
        this.dbtx = database.getDatabase();
    }

    public void enQueue(BlockDocument block) {
        this.queue.add(block);
    }

    @Override
    public void run() {
        if (!queue.isEmpty() && lock.tryLock()) {
            try {
                int count = 0;
                if(database.isClosed()) {
                    database.open();
                }
                dbtx.declareIntent( new OIntentMassiveInsert());
                dbtx.begin();
                System.out.println("INSERTING: " + queue.size());
                while (queue.peek() != null && count < MAX_BATCH) {
                    BlockDocument b = queue.poll();
                    dbtx.save(b.asDocument());
                    count++;
                }
                dbtx.commit();
                
                System.out.println("BULK INSERT");
                dbtx.declareIntent(null);
                System.out.println("Block count: " + dbtx.countClass("Block"));
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "SettlerCraft-BlockLogger";
    }

    @Override
    public void logBlock(Location lctn, String string, World world, int oldMaterial, byte oldData, int newMaterial, byte newData) {
        enQueue(new BlockDocument(world.getName(), lctn.getBlockX(), lctn.getBlockY(), lctn.getBlockZ(), oldMaterial, oldData, newMaterial, newData));
    }

}
