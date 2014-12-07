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
package com.chingo247.blocklogger.orientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class BlockLogger {
    
    private final Logger LOG = Logger.getLogger("BlockLogger");
    private final Timer timer;
    private final Queue<BlockLog> blocksQueue = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();
    private final long ONE_SECOND = 1000;
    private final int BATCH = 50_000;
    private final int THRESHOLD = 1000;
    private ODatabaseDocumentTx databaseDocumentTx;
    public static final String BLOCK_DATABASE = "Blocks";
    
    private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            System.out.println("Execute!");
            if(blocksQueue.isEmpty())  return;
            if(!lock.tryLock()) return;
            
                int commited = 0;
                long start = System.currentTimeMillis();
                
                if(databaseDocumentTx.isClosed()) {
                    databaseDocumentTx.open("admin", "admin");
                }
                databaseDocumentTx.begin();
                databaseDocumentTx.declareIntent(new OIntentMassiveInsert());
                while(blocksQueue.peek() != null && ((System.currentTimeMillis() - start) < THRESHOLD && commited < BATCH)) {
                    BlockLog log = blocksQueue.poll();
                    databaseDocumentTx.save(asODocument(log));
                    commited++;
                }
                databaseDocumentTx.commit();
                databaseDocumentTx.declareIntent(null);
                LOG.log(Level.INFO, "Committing: {0}", commited);
                LOG.log(Level.INFO, "Queue: {0}", blocksQueue.size());
                LOG.log(Level.INFO, "Count: {0}", databaseDocumentTx.countClass("BlockLog"));
                
                databaseDocumentTx.close();
                lock.unlock();
                
               
            
        }
    };

    public BlockLogger() {
        this.timer = new Timer();
    }
    
    private ODocument asODocument(BlockLog log) {
        ODocument d = new ODocument("BlockLog");
        d.field("x", log.x);
        d.field("y", log.y);
        d.field("z", log.z);
        d.field("newMaterial", log.newMaterial);
        d.field("newData", log.newData);
        d.field("oldData", log.oldData);
        d.field("oldMaterial", log.oldMaterial);
        d.field("date");
        d.field("world", log.world); 
        return d;
    }
    
    public void logBlock(int x, int y, int z, int newMaterial, byte newData, int oldMaterial, byte oldData, String world) {
        blocksQueue.add(new BlockLog(x, y, z, oldMaterial, oldData, newMaterial, newData, world));
    }

    public void setup() throws Exception {
        OrientDBServer server = OrientDBServer.getInstance();
        if(!server.hasDatabase(BLOCK_DATABASE)) {
            server.createDatabase(BLOCK_DATABASE, "document", false);
        }
        databaseDocumentTx = new ODatabaseDocumentTx("remote:localhost/Blocks");
        databaseDocumentTx.open("admin", "admin");
        timer.scheduleAtFixedRate(task, 0, ONE_SECOND);
    }
    
    public static void main(String[] args) {
        BlockLogger logger = new BlockLogger();
        try {
            logger.setup();
            System.out.println("\nSetup Logger");
        } catch (Exception ex) {
            Logger.getLogger(BlockLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i = 0; i < 100_000; i++) logger.logBlock(i+1, 90, i+2, 5, new Integer(0).byteValue(), i, new Integer(0).byteValue(), "MyWorld");
        
    }
    
    
    
}
