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
package com.chingo247.settlercraft.structure.plan.schematic;

import com.chingo247.settlercraft.commons.logging.SCLogger;
import com.chingo247.settlercraft.entities.SchematicDataEntity;
import com.chingo247.settlercraft.structure.persistence.service.AbstractDAO;
import com.chingo247.settlercraft.structure.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.commons.util.LogLevel;
import com.chingo247.settlercraft.entities.QSchematicDataEntity;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicDataManager {

    private static final int BULK_INSERT_SIZE = 1000;
    private final SCLogger LOG = SCLogger.getLogger();
    private final Lock lock;
    private final Map<Long, SchematicDataEntity> schematicData;
    private static SchematicDataManager instance;
    private final File structurePlanDirectory = new File("plugins/SettlerCraft/Plans"); //TODO Read from properties file and maven profile!

    private SchematicDataManager() {
        this.lock = new ReentrantLock();

        // Populate the map with existing
        LOG.print(LogLevel.INFO, "Retrieving saved schematics...");
        Session session = HibernateUtil.getSession();
        QSchematicDataEntity qsd = QSchematicDataEntity.schematicDataEntity;
        HibernateQuery query = new HibernateQuery(session);
        schematicData = query.from(qsd).map(qsd.checksum, qsd);
        session.close();

    }

    public static SchematicDataManager getInstance() {
        if (instance == null) {
            instance = new SchematicDataManager();
        }
        return instance;
    }
    
    public SchematicDataEntity getData(Long checksum) {
        return schematicData.get(checksum);
    }

  
    public void load() {
        if (lock.tryLock()) {
            
            ForkJoinPool pool = new ForkJoinPool();
            try {
                Iterator<File> fit = FileUtils.iterateFiles(structurePlanDirectory, new String[]{"schematic"}, true);

                // Process the schematics that need to be loaded
                List<SchematicProcessor> tasks = new ArrayList<>();
                while (fit.hasNext()) {
                    File schematicFile = fit.next();
                    try {
                        long checksum = FileUtils.checksumCRC32(schematicFile);
                        // Only load schematic data that wasn't yet loaded...
                        if(schematicData.get(checksum) == null) {
                            SchematicProcessor processor = new SchematicProcessor(schematicFile);
                            tasks.add(processor);
                            pool.execute(processor);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicDataManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }

                // Wait for the processes the finish and queue them for bulk insert
                List<SchematicDataEntity> toPersist = new ArrayList<>(tasks.size());
                for (SchematicProcessor sp : tasks) {
                    SchematicDataEntity sd = sp.join();
                    schematicData.put(sd.getChecksum(), sd);
                    toPersist.add(sd);
                }
                
                // Close the pool!
                pool.shutdown();
                

                // Bulk insert the new schematics
                Session session = null;
                Transaction tx = null;
                try {
                    session = HibernateUtil.getSession();

                    
                    int persisted = 0;

                    tx = session.beginTransaction();
                    for (SchematicDataEntity data : toPersist) {
                        session.persist(data);
                        if (persisted % BULK_INSERT_SIZE == 0) {
                            tx.commit();
                            tx.begin();
                        }
                        persisted++;
                    }
                    tx.commit();
                    session.flush();
                } catch (HibernateException e) {
                    try {
                        if (tx != null) {
                            tx.rollback();
                        }
                    } catch (HibernateException rbe) {
                        Logger.getLogger(AbstractDAO.class.getName()).log(Level.SEVERE, "Couldnâ€™t roll back transaction", rbe);
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

    private class SchematicProcessor extends RecursiveTask<SchematicDataEntity> {

        private final File schematic;

        public SchematicProcessor(File schematic) {
            this.schematic = schematic;
        }

        @Override
        protected SchematicDataEntity compute() {
            try {
                return SchematicDataEntity.load(schematic);
            } catch (IOException | DataException ex) {
                Logger.getLogger(SchematicDataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

}
