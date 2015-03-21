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

import com.chingo247.settlercraft.model.util.LogLevel;
import com.chingo247.settlercraft.model.util.SCLogger;
import com.chingo247.settlercraft.model.entities.QSchematicEntity;
import com.chingo247.settlercraft.model.persistence.entities.SchematicEntity;
import com.chingo247.settlercraft.model.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.model.persistence.dao.SchematicDAO;
import com.chingo247.settlercraft.structure.plan.exception.SchematicException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
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
public class SchematicManager {

    private static final int BULK_INSERT_SIZE = 1000;
    private final SCLogger LOG = SCLogger.getLogger();
    private final Map<Long, Schematic> schematics;
    private final Map<Long, SchematicEntity> entities;
    private static SchematicManager instance;

    private SchematicManager() {
        this.schematics = Maps.newHashMap();
        
        // Populate the map with existing
        LOG.print(LogLevel.INFO, "Retrieving schematic_data...");
        Session session = HibernateUtil.getSession();
        QSchematicEntity qsd = QSchematicEntity.schematicEntity;
        HibernateQuery query = new HibernateQuery(session);
        this.entities = query.from(qsd).map(qsd.checksum, qsd);
        session.close();

    }
    
    public static SchematicManager getInstance() {
        if (instance == null) {
            instance = new SchematicManager();
        }
        return instance;
    }

    /**
     * Will attempt to get the schematic from the cache. Otherwise the schematic will be loaded and returned
     * @param schematicFile The schematicFile
     * @return The schematic
     */
    public Schematic getOrLoadSchematic(File schematicFile) {
        try {
            long checksum = FileUtils.checksumCRC32(schematicFile);
            Schematic schematic = getSchematic(checksum);
            if(schematic == null) {
                schematic = new SchematicImpl(schematicFile);
                schematics.put(checksum, schematic);
                SchematicEntity entity = new SchematicEntity(checksum, schematic.getWidth(), schematic.getHeight(), schematic.getLength());
                SchematicDAO schematicDAO = new SchematicDAO();
                entity = schematicDAO.save(entity);
                entities.put(checksum, entity);
            }
            return schematic;
        } catch (IOException ex) {
            throw new SchematicException(ex);
        }
        
    }
    
    public Schematic getSchematic(Long checksum) {
        Schematic schematic;
        synchronized(schematics) {
            schematic =  schematics.get(checksum);
        }
        return schematic;
    }
    
    public synchronized void load(File directory) {
        Preconditions.checkArgument(directory.isDirectory());
       
        ForkJoinPool pool;
        Iterator<File> fit = FileUtils.iterateFiles(directory, new String[]{"schematic"}, true);
        if (fit.hasNext()) {
            pool = new ForkJoinPool(); // only create the pool if we have schematics
        } else {
            return;
        }

        Map<Long,File> files = Maps.newHashMap();
        
        // Process the schematics that need to be loaded
        List<SchematicProcessor> tasks = new ArrayList<>();
        while (fit.hasNext()) {
            File schematicFile = fit.next();
            try {
                long checksum = FileUtils.checksumCRC32(schematicFile);
                // Only load schematic data that wasn't yet loaded...
                files.put(checksum, schematicFile);
                SchematicEntity entity = entities.get(checksum);
                if (entity == null) {
                    SchematicProcessor processor = new SchematicProcessor(schematicFile);
                    tasks.add(processor);
                    pool.execute(processor);
                } else {
                    schematics.put(checksum, new SchematicImpl(schematicFile, entity));
                }
            } catch (IOException ex) {
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Wait for the processes the finish and queue them for bulk insert
        List<SchematicEntity> toPersist = new ArrayList<>(tasks.size());
        for (SchematicProcessor sp : tasks) {
            SchematicEntity schematicEntity = sp.join();
            schematics.put(schematicEntity.getId(), new SchematicImpl(files.get(schematicEntity.getId()), schematicEntity));
            toPersist.add(schematicEntity);
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
            for (SchematicEntity data : toPersist) {
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
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, "Couldn't roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    private class SchematicProcessor extends RecursiveTask<SchematicEntity> {

        private final File schematic;

        public SchematicProcessor(File schematic) {
            this.schematic = schematic;
        }

        @Override
        protected SchematicEntity compute() {
            try {
                CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);
                long checksum = FileUtils.checksumCRC32(schematic);
                
                return new SchematicEntity(checksum, cc.getWidth(), cc.getHeight(), cc.getLength());
            } catch (IOException | DataException ex) {
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

}
