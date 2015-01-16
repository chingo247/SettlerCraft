/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.schematic;

import com.chingo247.settlercraft.structureapi.persistence.hibernate.AbstractDAO;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structureapi.structure.plan.QSchematicData;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicDataManager {

    private static final int BULK_INSERT_SIZE = 1000;
    private final Lock lock;
    private final Map<Long, SchematicData> schematicData;
    private final Map<Long, File> schematicsToLoad;
    private final ForkJoinPool pool;

    SchematicDataManager(ForkJoinPool pool) {
        this.schematicsToLoad = Collections.synchronizedMap(new HashMap<Long, File>());
        this.lock = new ReentrantLock();
        this.pool = pool;

        // Populate the map with existing
        Session session = HibernateUtil.getSession();
        QSchematicData qsd = QSchematicData.schematicData;
        HibernateQuery query = new HibernateQuery(session);
        schematicData = query.from(qsd).map(qsd.checksum, qsd);
        session.close();
    }

    void queueForLoadIfAbsent(Long checksum, File schematic) {
        if (schematicData.get(checksum) == null) {
            schematicsToLoad.put(checksum, schematic);
        }
    }

    public void processQueuedSchematics() {
        if (lock.tryLock()) {
            try {
                // Process the schematics that need to be loaded
                List<SchematicProcessor> tasks = new ArrayList<>(schematicsToLoad.size());
                for (File f : schematicsToLoad.values()) {
                    SchematicProcessor processor = new SchematicProcessor(f);
                    tasks.add(processor);
                    processor.fork();
                }

                // Wait for the processes the finish and queue them for bulk insert
                List<SchematicData> toPersist = new ArrayList<>(schematicsToLoad.size());
                for (SchematicProcessor sp : tasks) {
                    SchematicData sd = sp.join();
                    schematicData.put(sd.getChecksum(), sd);
                    toPersist.add(sd);
                }

                Session session = null;
                Transaction tx = null;
                try {
                    session = HibernateUtil.getSession();

                    session.setFlushMode(FlushMode.MANUAL);
                    int persisted = 0;

                    tx = session.beginTransaction();
                    for (SchematicData data : toPersist) {
                        session.persist(data);
                        if (persisted % BULK_INSERT_SIZE == 0) {
                            tx.commit();
                            tx.begin();
                        }
                        persisted++;
                    }
                    tx.commit();
                } catch (HibernateException e) {
                    try {
                        if (tx != null) {
                            tx.rollback();
                        }
                    } catch (HibernateException rbe) {
                        Logger.getLogger(AbstractDAO.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
                    }
                    throw e;
                } finally {
                    if (session != null) {
                        session.close();
                    }
                }
            } finally {
                lock.unlock();
            }
        }

    }

    private class SchematicProcessor extends RecursiveTask<SchematicData> {

        private final File schematic;

        public SchematicProcessor(File schematic) {
            this.schematic = schematic;
        }

        @Override
        protected SchematicData compute() {
            try {
                return SchematicData.load(schematic);
            } catch (IOException | DataException ex) {
                Logger.getLogger(SchematicDataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

}