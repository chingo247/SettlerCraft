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
package com.chingo247.structureapi.structure.plan;

import com.chingo247.structureapi.structure.plan.schematic.SchematicManager;
import java.io.File;
import java.util.ArrayList;
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
        return readDirectory(structurePlanDirectory, false, new ForkJoinPool());
    }

    public List<StructurePlan> readDirectory(File structurePlanDirectory, boolean printstuff, ForkJoinPool pool) {
        Iterator<File> fit = FileUtils.iterateFiles(structurePlanDirectory, new String[]{"xml"}, true);
        SchematicManager sdm = SchematicManager.getInstance();
        sdm.load(structurePlanDirectory);

        Map<String, StructurePlanProcessor> processors = Collections.synchronizedMap(new HashMap<String, StructurePlanProcessor>());

        long start = System.currentTimeMillis();
        while (fit.hasNext()) {
            File structurePlanFile = fit.next();
            StructurePlanProcessor spp = new StructurePlanProcessor(structurePlanFile);
            
            processors.put(structurePlanFile.getAbsolutePath(), spp);
            pool.execute(spp);
        }

        List<StructurePlan> plans = new ArrayList<>();
        for (StructurePlanProcessor spp : processors.values()) {
            StructurePlan plan = spp.join();
            if (plan != null) {
                plans.add(plan);
            }
        }
        return plans;
    }
    
   
    public StructurePlan readFile(File structurePlanFile) {
        SchematicManager sdm = SchematicManager.getInstance();
        sdm.load(structurePlanFile.getParentFile());
        StructurePlanProcessor spp = new StructurePlanProcessor(structurePlanFile);
        return spp.compute();
    }

  

}
