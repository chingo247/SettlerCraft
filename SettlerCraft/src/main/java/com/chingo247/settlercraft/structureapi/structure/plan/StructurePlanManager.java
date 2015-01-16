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
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.structure.plan.schematic.PlacementSchematicProcessorManager;
import com.chingo247.settlercraft.structureapi.persistence.HSQLServer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import org.apache.commons.io.FileUtils;

/**
 * Contains all the StructurePlan data
 *
 * @author Chingo
 */
public class StructurePlanManager {

    private final Map<String, StructurePlan> plans = new HashMap<>();

    private static StructurePlanManager instance;

    /**
     * Gets the StructurePlanManager instances
     *
     * @return The StructurePlanManager instance
     */
    public static StructurePlanManager getInstance() {
        if (instance == null) {
            instance = new StructurePlanManager();
        }

        return instance;
    }

    public StructurePlan getPlan(String plan) {
        synchronized (plans) {
            return plans.get(plan);
        }
    }

    public void putStructurePlan(StructurePlan plan) {
        synchronized (plans) {
            plans.put(plan.getFile().getAbsolutePath(), plan);
        }
    }

    private void load(File directory) {
        Iterator<File> fit = FileUtils.iterateFiles(directory, new String[]{"xml"}, true);
        ForkJoinPool pool = new ForkJoinPool();
        StructurePlanProcessorManager sppm = new StructurePlanProcessorManager();
        PlacementSchematicProcessorManager pspm = new PlacementSchematicProcessorManager();
        
        List<StructurePlanProcessor> processors = new ArrayList<>();
        long start = System.currentTimeMillis();
        while (fit.hasNext()) {
            StructurePlanProcessor spp = new StructurePlanProcessor(fit.next(), sppm, pspm);
            processors.add(spp);
            pool.execute(spp);
        }

        pool.shutdown();
        for(StructurePlanProcessor spp : processors) spp.join();
        
        // Close the database connection
        
        
        System.out.printf("******************************************\n");
        System.out.printf("[SettlerCraft]: Cores: %d\n", pool.getParallelism());
        System.out.printf("[SettlerCraft]: Steal Count: %d\n", pool.getStealCount());
        System.out.printf("[SettlerCraft]: Time: %d ms\n", (System.currentTimeMillis() - start));
        System.out.printf("******************************************\n");

//        for(StructurePlan p : plans.values()) {
//            System.out.println("Name: " + p.getName() + " Placeable: " + p.getPlaceable());
//        }
        
    }

    public static void main(String[] args) {
        
        HSQLServer.getInstance().start();
        StructurePlanManager manager = getInstance();
        
        manager.load(new File("C:\\Users\\Chingo\\Desktop\\SettlerCraft"));
        
        
        
        HSQLServer.getInstance().stop();
    }

}
