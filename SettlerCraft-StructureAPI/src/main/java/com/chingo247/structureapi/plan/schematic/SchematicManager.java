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
package com.chingo247.structureapi.plan.schematic;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.settlercraft.core.util.LogLevel;
import com.chingo247.settlercraft.core.util.SCLogger;
import com.chingo247.structureapi.persistence.dao.schematic.SchematicDataDAO;
import com.chingo247.structureapi.persistence.dao.schematic.SchematicData;
import com.chingo247.structureapi.persistence.dao.schematic.SchematicDataFactory;
import com.chingo247.structureapi.persistence.dao.schematic.SchematicDataNode;
import com.chingo247.structureapi.plan.exception.SchematicException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicManager {

    private final SCLogger LOG = new SCLogger();
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
        System.out.println("Directory: " + directory.getAbsolutePath());
        Preconditions.checkArgument(directory.isDirectory());
        System.out.println("Searching for schematics in: " + directory.getAbsolutePath());

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
                System.out.println("[SettlerCraft]: Imported " + name + " data to database");
                schematicDAO.addSchematic(name, xxhash, width, height, length, System.currentTimeMillis());
            }
            
            // Delete unused
            for(SchematicDataNode sdn : schematicDAO.findSchematicsBeforeDate(TWO_DAYS)) {
                System.out.println("[SettlerCraft]: Deleted " + sdn.getName() + " last import was " + new Date(sdn.getLastImport()).toString());
                sdn.delete();
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
                LOG.print(LogLevel.INFO, schematicFile, "Schematic", System.currentTimeMillis() - start);
                return new SchematicImpl(schematicFile, clipboard);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
        }

    }

}
