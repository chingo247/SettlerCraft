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
package com.chingo247.settlercraft.structure.plan.processing;

import com.chingo247.settlercraft.persistence.HSQLServer;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.schematic.SchematicDataManager;
import com.chingo247.xcore.util.ChatColors;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class StructurePlanReader {

    public List<StructurePlan> readDirectory(File structurePlanDirectory) {
        System.out.println("[SettlerCraft]: Loading plans...");
        ForkJoinPool pool = new ForkJoinPool();
        Iterator<File> fit = FileUtils.iterateFiles(structurePlanDirectory, new String[]{"xml"}, true);

        SchematicDataManager sdm = SchematicDataManager.getInstance();
        sdm.load(structurePlanDirectory);

        Map<String, StructurePlanProcessor> processors = Collections.synchronizedMap(new HashMap<String, StructurePlanProcessor>());

        long start = System.currentTimeMillis();
        while (fit.hasNext()) {
            File structurePlanFile = fit.next();
            StructurePlanProcessor spp = new StructurePlanProcessor(structurePlanFile, processors);
            processors.put(structurePlanFile.getAbsolutePath(), spp);
            pool.execute(spp);
        }

        pool.shutdown();
        List<StructurePlan> plans = new ArrayList<>();
        for (StructurePlanProcessor spp : processors.values()) {
            StructurePlan plan = spp.join();
            if (plan != null) {
                plans.add(plan);
            }
        }

        // Close the database connection
        System.out.println("******************************************");
        System.out.println(ChatColors.YELLOW + "[SettlerCraft]: "+ChatColors.RESET+"Parallel Threads: " + pool.getParallelism());
        System.out.println(ChatColors.YELLOW + "[SettlerCraft]: "+ChatColors.RESET +"Plans loaded: " +  plans.size());
//        System.out.printf("[SettlerCraft]: Steal Count: %d\n", pool.getStealCount());
        System.out.println(ChatColors.YELLOW + "[SettlerCraft]: "+ChatColors.RESET + (System.currentTimeMillis() - start) + " ms");
        System.out.println("******************************************");
        return plans;
    }

    public StructurePlan readFile(File structurePlanFile, ForkJoinPool pool) {
        Preconditions.checkNotNull(pool);
        System.out.println(ChatColors.YELLOW + "[SettlerCraft]: "+ChatColors.RESET + "Loading plans...");

        SchematicDataManager sdm = SchematicDataManager.getInstance();
        sdm.load(structurePlanFile.getParentFile());

        Map<String, StructurePlanProcessor> processors = Collections.synchronizedMap(new HashMap<String, StructurePlanProcessor>());

        StructurePlanProcessor spp = new StructurePlanProcessor(structurePlanFile, processors);
        processors.put(structurePlanFile.getAbsolutePath(), spp);
        pool.execute(spp);
        pool.shutdown();
        return spp.join();
    }
    
    
    

    public static void main(String[] args) {
        StructurePlanReader reader = new StructurePlanReader();
        HSQLServer server = HSQLServer.getInstance();
        server.start();

        reader.readDirectory(new File("C:\\Users\\Chingo\\Documents\\NetBeansProjects\\SettlerCraft\\SettlerCraft\\SettlerCraft\\plugins\\SettlerCraft\\Plans"));
        server.stop();
    }

}
