/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.structureapi.structure.plan.schematic;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.settlercraft.structureapi.persistence.dao.SchematicDataDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.persistence.entities.schematic.SchematicDataFactory;
import com.chingo247.settlercraft.structureapi.persistence.entities.schematic.SchematicDataNode;
import com.chingo247.settlercraft.structureapi.structure.plan.exception.SchematicException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicManager {

//    private final SCLogger LOG = new SCLogger();
    private final Map<Long, Schematic> schematics;
    private static SchematicManager instance;
    private final SchematicDataDAO schematicDAO;
    private final GraphDatabaseService graph;
    private final long TWO_DAYS = 1000 * 60 * 60 * 24 * 2;

    private SchematicManager() {
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.schematicDAO = new SchematicDataDAO(graph);
        this.schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
    }

    public static SchematicManager getInstance() {
        if (instance == null) {
            instance = new SchematicManager();
        }
        return instance;
    }

    /**
     * Will attempt to get the schematic from the cache. Otherwise the schematic
     * will be loaded and returned
     *
     * @param schematicFile The schematicFile
     * @return The schematic
     */
    public Schematic getOrLoadSchematic(File schematicFile) {
        try {
            XXHasher hasher = new XXHasher();
            long checksum = hasher.hash64(schematicFile);
            Schematic schematic = getSchematic(checksum);
            if (schematic == null) {
                FastClipboard clipboard = FastClipboard.read(schematicFile);
                schematic = new SchematicImpl(schematicFile, clipboard);
                schematics.put(checksum, schematic);
                clipboard = null;
            }
            return schematic;
        } catch (IOException  ex) {
            throw new SchematicException(ex);
        }

    }

    public synchronized Schematic getSchematic(Long checksum) {
        return schematics.get(checksum);
    }

    public synchronized void load(File directory) {
        Preconditions.checkArgument(directory.isDirectory());

        ForkJoinPool pool;
        Iterator<File> fit = FileUtils.iterateFiles(directory, new String[]{"schematic"}, true);
        if (fit.hasNext()) {
            pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()); // only create the pool if we have schematics
        } else {
            return;
        }
        
        
        Map<Long, SchematicData> alreadyHere = Maps.newHashMap();
        try(Transaction tx = graph.beginTx()) {
            List<SchematicDataNode> schematicNodes = schematicDAO.findSchematicsAfterDate(System.currentTimeMillis() - TWO_DAYS);
            SchematicDataFactory schematicDataFactory = new SchematicDataFactory();
            for(SchematicDataNode node : schematicNodes) {
                SchematicData data = schematicDataFactory.make(node);
                alreadyHere.put(data.getXXHash64(), data);
            }
            
            tx.success();
        }
        
        // Process the schematics that need to be loaded
        List<SchematicProcessor> tasks =  Lists.newArrayList();
        List<Schematic> alreadyDone = Lists.newArrayList();
        XXHasher hasher = new XXHasher();
        while (fit.hasNext()) {
            File schematicFile = fit.next();
            try {
                long checksum = hasher.hash64(schematicFile);
                // Only load schematic data that wasn't yet loaded...
                SchematicData existingData = alreadyHere.get(checksum);
                if(existingData != null) {
                    Schematic s = new SchematicImpl(schematicFile, existingData.getWidth(), existingData.getHeight(), existingData.getLength());
                    alreadyDone.add(s);
                } else if (getSchematic(checksum) == null) {
                    SchematicProcessor processor = new SchematicProcessor(schematicFile);
                    tasks.add(processor);
                    pool.execute(processor);
                }
            } catch (IOException ex) {
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        // Wait for the processes the finish and queue them for bulk insert
        List<Schematic> newSchematics = Lists.newArrayList();
        for (SchematicProcessor sp : tasks) {
            Schematic schematic = sp.join();
            if (schematic != null) {
                newSchematics.add(schematic);
            }
        }

        // Close the pool!
        pool.shutdown();
        
        // Update the database
        try(Transaction tx = graph.beginTx()) {
            for(Schematic data : alreadyDone) {
                SchematicDataNode sdn = schematicDAO.find(data.getHash());
                sdn.setLastImport(System.currentTimeMillis());
            }
            for(Schematic newData : newSchematics) {
                String name = newData.getFile().getName();
                long xxhash = newData.getHash();
                int width = newData.getWidth();
                int height = newData.getHeight();
                int length = newData.getLength();
                schematicDAO.addSchematic(name, xxhash, width, height, length, System.currentTimeMillis());
            }
            
            // Delete unused
            int removed = 0;
            for(SchematicDataNode sdn : schematicDAO.findSchematicsBeforeDate(System.currentTimeMillis() - TWO_DAYS)) {
                sdn.delete();
                removed++;
            }
            if(removed > 0) {
                System.out.println("[SettlerCraft]: Deleted " + removed + " schematics from cache");
            }
            
            tx.success();
        }
        
        synchronized(schematics) {
            for(Schematic schematic : newSchematics) schematics.put(schematic.getHash(), schematic);
            for(Schematic schematic : alreadyDone) schematics.put(schematic.getHash(), schematic);
        }
        

    }

    private class SchematicProcessor extends RecursiveTask<Schematic> {

        private final File schematicFile;

        public SchematicProcessor(File schematic) {
            Preconditions.checkNotNull(schematic);
            Preconditions.checkArgument(schematic.exists());
            this.schematicFile = schematic;
        }

        @Override
        protected Schematic compute() {
            try {
                long start = System.currentTimeMillis();
                FastClipboard clipboard = FastClipboard.read(schematicFile);
//                LOG.print(LogLevel.INFO, schematicFile, "Schematic", System.currentTimeMillis() - start);
                return new SchematicImpl(schematicFile, clipboard);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
        }

    }

}
