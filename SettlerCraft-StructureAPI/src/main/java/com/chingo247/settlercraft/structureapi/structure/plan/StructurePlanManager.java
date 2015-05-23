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

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.event.StructurePlansLoadedEvent;
import com.chingo247.settlercraft.structureapi.event.StructurePlansReloadEvent;
import com.chingo247.settlercraft.structureapi.structure.plan.exception.PlanException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * StructurePlan manager is a storage class for StructurePlans. All methods
 * within this class are synchronised and can thus be called from multiple
 * threads
 *
 * @author Chingo
 */
public class StructurePlanManager {

    private final Map<String, String> idReferences;
    private final Map<String, StructurePlan> plans;
    private final String planDirectoryPath;
    private ForkJoinPool forkJoinPool;
    private final int parallelism;
    private final File planDirectory;
    private static StructurePlanManager instance;
    private Logger LOG = Logger.getLogger(StructurePlanManager.class.getName());

    private StructurePlanManager() {
        this.planDirectory = StructureAPI.getInstance().getPlanDirectory();
        this.plans = Maps.newHashMap();
        this.planDirectoryPath = planDirectory.getAbsolutePath();
        this.idReferences = Maps.newHashMap();
        this.parallelism = Math.max(1, Runtime.getRuntime().availableProcessors() - 1); // Dont lag server on reload...
    }

    public static StructurePlanManager getInstance() {
        if (instance == null) {
            instance = new StructurePlanManager();
        }
        return instance;
    }
    
    public StructurePlan getPlan(String planId) {
        synchronized (plans) {
            return plans.get(planId);
        }
    }

    public void putPlan(StructurePlan plan) {
        synchronized (plans) {
            plans.put(plan.getId(), plan);
        }
    }

    public void loadPlans() {
        loadPlans(true);
    }

    public void loadPlans(final boolean verbose) {
        synchronized (plans) {
            plans.clear();
        }
        
        // Make dirs if not exist!
        planDirectory.mkdirs();

        EventManager.getInstance().getEventBus().post(new StructurePlansReloadEvent());

        // If it isn't null and there are still processes running... terminate them
        if (forkJoinPool != null && !forkJoinPool.isShutdown()) {
            forkJoinPool.shutdown();
        }
        forkJoinPool = new ForkJoinPool(parallelism);

        SettlerCraft.getInstance().getExecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    StructurePlanReader reader = new StructurePlanReader();
                    List<StructurePlan> plansList = reader.readDirectory(planDirectory, verbose, forkJoinPool);
                    for (StructurePlan plan : plansList) {
                        boolean exists =  getPlan(plan.getId()) != null;
                        if (exists) {
                            continue; // it's exact the same plan...
                        }
                        putPlan(plan);
                    }
                    if (!forkJoinPool.isShutdown()) {
                        forkJoinPool.shutdown();
                    }
                    EventManager.getInstance().getEventBus().post(new StructurePlansLoadedEvent());
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }

    protected File getPlanDirectory() {
        return new File(planDirectoryPath);
    }

    public List<StructurePlan> getPlans() {
        synchronized (plans) {
            return new ArrayList<>(plans.values());
        }
    }

    private class SearchReferencesTask extends RecursiveTask<Map<String, String>> {

        private final File searchDirectory;
        private final SAXReader reader;

        public SearchReferencesTask(File directory) {
            Preconditions.checkArgument(directory.isDirectory());
            this.searchDirectory = directory;
            this.reader = new SAXReader();
        }

        @Override
        protected Map<String, String> compute() {
            Map<String, String> result = Maps.newHashMap();

            List<SearchReferencesTask> tasks = new ArrayList<>();
            for (File f : searchDirectory.listFiles()) {

                if (f.isDirectory()) {
                    SearchReferencesTask task = new SearchReferencesTask(f);
                    task.fork();
                    tasks.add(task);
                } else {
                    if (!FilenameUtils.getExtension(f.getName()).equals("xml")) {
                        continue;
                    }
                    try {
                        if (!isStructurePlan(f)) {
                            continue;
                        }
                    } catch (DocumentException ex) {
                        Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Map.Entry<String, String> entry = getEntry(f);
                    if (entry != null) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            for (SearchReferencesTask task : tasks) {
                Map<String, String> childReferences = task.join();
                for (Map.Entry<String, String> s : childReferences.entrySet()) {
                    if (result.get(s.getKey()) == null) {
                        result.put(s.getKey(), s.getValue());
                    } else {
                        throw new PlanException("Duplicate id references! StructurePlanFile: " + s.getValue() + " and file " + result.get(s.getKey()) + " have the same id!");
                    }
                }
            }
            return result;
        }

        private Map.Entry<String, String> getEntry(File f) {
            try {
                Document d = reader.read(f);
                Element e = (Element) d.getRootElement().selectSingleNode("Id");

                if (e != null) {
                    String id = e.getText().trim();
                    if (id.isEmpty()) {
                        throw new PlanException("The <Id> tag of " + f.getAbsolutePath() + " is empty!");
                    }

                    return Maps.immutableEntry(id, f.getAbsolutePath());
                }
            } catch (DocumentException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

    private boolean isStructurePlan(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document d = reader.read(file);
        return d.getRootElement().getName().equals("StructurePlan");
    }

}
