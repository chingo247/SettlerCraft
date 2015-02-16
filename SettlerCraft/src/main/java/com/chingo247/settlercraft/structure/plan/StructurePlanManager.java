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
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.structure.plan.processing.StructurePlanComplex;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanProcessorManager;
import com.chingo247.settlercraft.commons.logging.SCLogger;
import com.chingo247.settlercraft.SettlerCraftContext;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanProcessor;
import com.chingo247.settlercraft.structure.plan.schematic.SchematicDataManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.io.FileUtils;

/**
 * Contains all the StructurePlan data
 *
 * @author Chingo
 */
public class StructurePlanManager {

    private final SCLogger LOG = SCLogger.getLogger();
    private final Map<String, StructurePlanComplex> plans;
    private final Lock planLock;

    private static StructurePlanManager instance;

    private StructurePlanManager() {
        this.planLock = new ReentrantLock();
        this.plans = new HashMap<>();

    }

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

    public StructurePlanComplex getPlan(String plan) {
        synchronized (plans) {
            return plans.get(plan);
        }
    }

    public void putStructurePlan(StructurePlanComplex plan) {
        synchronized (plans) {
            plans.put(plan.getFile().getAbsolutePath(), plan);
        }
    }

    public void loadPlans() {
        if (planLock.tryLock()) {
            try {
                plans.clear();
                File directory = SettlerCraftContext.getContext().getPlanDirectory();
                LOG.print("Loading plans from: " + directory.getAbsolutePath());
                LOG.print("Preparing Schematics...");
                // Load schematic data
                SchematicDataManager sdm = SchematicDataManager.getInstance();
                sdm.load();

                LOG.print("Loading StructurePlans...");
                System.out.printf("******************************************\n");
                Iterator<File> fit = FileUtils.iterateFiles(directory, new String[]{"xml"}, true);

                StructurePlanProcessorManager sppm = new StructurePlanProcessorManager();

                ForkJoinPool pool = new ForkJoinPool();
                List<StructurePlanProcessor> processors = new ArrayList<>();
                long start = System.currentTimeMillis();
                while (fit.hasNext()) {
                    StructurePlanProcessor spp = new StructurePlanProcessor(fit.next(), sppm);
                    processors.add(spp);
                    pool.execute(spp);
                }

                pool.shutdown();
                for (StructurePlanProcessor spp : processors) {
                    spp.join();
                }

                // Close the database connection
                System.out.printf("******************************************\n");
                System.out.printf("[SettlerCraft]: Parallel Threads: %d\n", pool.getParallelism());
                System.out.printf("[SettlerCraft]: Plans loaded: %d\n", plans.size());
                System.out.printf("[SettlerCraft]: Schematics processed: %d\n", SchematicDataManager.getInstance().getAmountOfSchematics());
//        System.out.printf("[SettlerCraft]: Steal Count: %d\n", pool.getStealCount());
                System.out.printf("[SettlerCraft]: Time: %d ms\n", (System.currentTimeMillis() - start));
                System.out.printf("******************************************\n");
            } finally {
                planLock.unlock();
            }

        } else {
            LOG.print("Already loading");
        }

    }

    public List<StructurePlan> getPlans() {
        return new ArrayList<StructurePlan>(plans.values());
    }

}
