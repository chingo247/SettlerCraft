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
package com.chingo247.structureapi.structure.plan.schematic;

import com.chingo247.structureapi.structure.plan.exception.SchematicException;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class SchematicManager {

    private Map<Long, Schematic> schematics;
    private static SchematicManager instance;

    private SchematicManager() {
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
            long checksum = FileUtils.checksumCRC32(schematicFile);
            Schematic schematic = getSchematic(checksum);
            if (schematic == null) {
                CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(schematicFile);
                schematic = new SchematicImpl(schematicFile, clipboard);
                schematics.put(checksum, schematic);
            }
            return schematic;
        } catch (IOException | DataException ex) {
            throw new SchematicException(ex);
        }

    }

    public synchronized Schematic getSchematic(Long checksum) {
        return schematics.get(checksum);
    }

    public synchronized void load(File directory) {
        Preconditions.checkArgument(directory.isDirectory());
        System.out.println("Searching for schematics in: " + directory.getAbsolutePath());

        ForkJoinPool pool;
        Iterator<File> fit = FileUtils.iterateFiles(directory, new String[]{"schematic"}, true);
        if (fit.hasNext()) {
            pool = new ForkJoinPool(); // only create the pool if we have schematics
        } else {
            return;
        }

        // Process the schematics that need to be loaded
        List<SchematicProcessor> tasks = new ArrayList<>();
        while (fit.hasNext()) {
            File schematicFile = fit.next();
            try {
                long checksum = FileUtils.checksumCRC32(schematicFile);
                // Only load schematic data that wasn't yet loaded...
                if (getSchematic(checksum) == null) {
                    SchematicProcessor processor = new SchematicProcessor(schematicFile);
                    tasks.add(processor);
                    pool.execute(processor);
                }

            } catch (IOException ex) {
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Wait for the processes the finish and queue them for bulk insert
        for (SchematicProcessor sp : tasks) {
            Schematic schematic = sp.join();
            if (schematic != null) {
                schematics.put(schematic.getId(), schematic);
            }
        }

        // Close the pool!
        pool.shutdown();

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
                CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(schematicFile);
                return new SchematicImpl(schematicFile, clipboard);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
        }

    }

}
