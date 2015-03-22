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

import com.chingo247.settlercraft.event.EventManager;
import com.chingo247.settlercraft.structure.placement.event.PlacementHandlerRegisterEvent;
import com.chingo247.settlercraft.structure.event.StructurePlanLoadEvent;
import com.chingo247.settlercraft.structure.plan.exception.PlanException;
import com.chingo247.settlercraft.structure.placement.handlers.PlacementHandler;
import com.chingo247.settlercraft.structure.plan.document.PlacementElement;
import com.chingo247.settlercraft.structure.plan.document.SimpleElement;
import com.chingo247.settlercraft.structure.plan.document.StructurePlanDocument;
import com.chingo247.settlercraft.structure.plan.document.SubStructureElement;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * The GlobalPlanManager is meant for very heavy and excessive reading of
 * StructurePlans. All StructurePlans that are loaded
 *
 * @author Chingo
 */
public class GlobalPlanManager extends StructurePlanManager {

    private static final String DEFAULT_PLUGIN = "SettlerCraft";
    private final Map<String, String> idReferences;
    private final List<StructurePlanJob> waitingJobs;
    private final List<StructurePlanJob> stagedJobs;
    private ForkJoinPool jobPool;
    private ForkJoinPool stagePool;
    private ForkJoinPool planPool;

    public GlobalPlanManager(File planDirectory) {
        super(planDirectory);
        this.waitingJobs = new ArrayList<>();
        this.stagedJobs = new ArrayList<>();
        this.idReferences = new HashMap<>();
    }

    @Override
    public final synchronized void loadPlans() {
        System.out.println("[" + DEFAULT_PLUGIN + "]: Loading StructurePlans");
        shutdownQuietly(stagePool);
        shutdownQuietly(jobPool);
        shutdownQuietly(planPool);

        try {
            System.out.println("Stopping existing jobs...");
            waitingJobs.clear();
            stagedJobs.clear();

            File planDirectory = getPlanDirectory();
            planPool = new ForkJoinPool();

            System.out.println("Searching for id references");
            SearchReferencesTask srt = new SearchReferencesTask(planDirectory);
            planPool.execute(srt);
            idReferences.putAll(srt.join());

            System.out.println("Found references: " + idReferences.size());

            getJobs(planDirectory);

            StructurePlanReader reader = new StructurePlanReader();
            List<StructurePlan> plansList = reader.readDirectory(planDirectory, true, planPool);
            planPool.shutdown();
            for (StructurePlan plan : plansList) {
                StructurePlan existingPlan = getPlan(plan.getId());
                if (existingPlan != null) {
                    if (!existingPlan.getFile().getAbsolutePath().equals(plan.getFile().getAbsolutePath())) {
                        throw new AssertionError("Well this is embarrasing... The MD5 Hash has failed us to create an unique String for 2 different paths"
                                + "Paths: [" + existingPlan.getFile().getAbsolutePath() + " , " + plan.getFile().getAbsolutePath() + "]");
                        // Chance is 1 in 2^64, im probably being paranoid...
                    }
                    continue; // it's exact the same plan...
                }
                putPlan(plan);
            }
        } catch (PlanException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        System.out.println("Done loading!");
    }

    private void shutdownQuietly(ForkJoinPool pool) {
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
            try {
                if (!pool.isTerminated()) {
                    pool.awaitTermination(1, TimeUnit.SECONDS);
                }
            } catch (InterruptedException ex) {
                // Terminate silently...
            }
        }

    }

    private void getJobs(File planDirectory) {
        System.out.println("Retrieving jobs...");
        Iterator<File> fit = FileUtils.iterateFiles(planDirectory, new String[]{"xml"}, true);
        jobPool = new ForkJoinPool();
        List<ForkJoinTask<StructurePlanJob>> tasks = new ArrayList<>();
        while (fit.hasNext()) {
            File f = fit.next();
            try {
                if (isStructurePlan(f)) {
                    ForkJoinTask<StructurePlanJob> task = jobPool.submit(new StructurePlanJobTask(f));
                    tasks.add(task);
                }
            } catch (DocumentException ex) {
                Logger.getLogger(GlobalPlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (ForkJoinTask<StructurePlanJob> task : tasks) {
            StructurePlanJob job = task.join();
            if (job != null) {
                waitingJobs.add(job);
            }
        }
        jobPool.shutdown();
        System.out.println("Waiting jobs: " + waitingJobs.size());
    }

    private boolean isStructurePlan(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document d = reader.read(file);
        return d.getRootElement().getName().equals("StructurePlan");
    }

    private void scanWaitingPlans(String plugin, String handlerType) {
        for (int i = 0; i < waitingJobs.size(); i++) {
            synchronized (waitingJobs.get(i)) {
                StructurePlanJob job = waitingJobs.get(i);
                if (job.checkSupportAndRemove(plugin, handlerType)) {
                    if (job.isReadyToStage()) {
                        waitingJobs.remove(i);
                        stagedJobs.add(job);
                    }
                }
            }

        }
    }

    private synchronized void processStagedJobs() {
        stagePool = new ForkJoinPool();

        List<ForkJoinTask<StructurePlan>> tasks = new ArrayList<>();
        for (StructurePlanJob job : stagedJobs) {
            ForkJoinTask<StructurePlan> task = stagePool.submit(new StructurePlanProcessor(job.structurePlanFile));
            tasks.add(task);
        }

        for (ForkJoinTask<StructurePlan> task : tasks) {
            StructurePlan plan = task.join();
            if (plan != null) {
                EventManager.getInstance().getEventBus().post(new StructurePlanLoadEvent(plan));
                plans.put(plan.getId(), plan);
            }
        }

        stagePool.shutdown();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRegisterHandlerEvent(PlacementHandlerRegisterEvent event) {
        PlacementHandler handler = event.getPlacementHandler();
        scanWaitingPlans(handler.getPlugin(), handler.getType());
        if (!stagedJobs.isEmpty()) {
            processStagedJobs();
        }
    }

    private class StructurePlanJob {

//        private Set<PlacementElement> requiredElements; // elements that need to be supported before the task can be executed...
        private Map<String, Set<String>> required;
        private File structurePlanFile;

        public StructurePlanJob(File structurePlanFile) throws Exception {
            this.structurePlanFile = structurePlanFile;
            this.required = Maps.newHashMap();

            StructurePlanDocument planDocument = StructurePlanDocument.read(structurePlanFile);
//            SAXReader reader = new SAXReader();
//            Document d = reader.read(structurePlanFile);
//            Element root = d.getRootElement();

            PlacementElement placementElement = planDocument.getPlacementElement();

            if (!StructureAPI.canHandle(placementElement)) {
                String[] types = splitType(placementElement, placementElement.getType());
                putRequired(types[0], types[1]);
            }

            List<SubStructureElement> subStructureElements = planDocument.getSubStructureElements();
            for (SubStructureElement subStructureElement : subStructureElements) {
                String type = subStructureElement.getType();

                if (type.equals("StructurePlan")) { // Embedded StructurePlan

                    String referencedPath = subStructureElement.getPlacementReferencePath();
                    if (referencedPath == null) {
                        // Try to get the StructurePlanFile by reference of Id
                        String referencedId = subStructureElement.getPlacementReferenceId();
                        if (referencedId == null) {
                            throw new PlanException("None of either <Path> or <Id> was defined within element  <" + subStructureElement.getElementName() + ">"
                                    + " on line " + subStructureElement.getLine()
                                    + " in '"+subStructureElement.getFile().getAbsolutePath()+"' does not exist!");
                        } else {

                            String referencePath = idReferences.get(referencedId);
                            if (referencePath == null) {
                                throw new PlanException("Reference defined within <Id> as '" + referencePath + "'"
                                        + " on line " + subStructureElement.getLine() 
                                        + " in '"+subStructureElement.getFile().getAbsolutePath()+"' does not exist!");
                            } else {
                                File referenceFile = new File(referencePath);
                                if (!referenceFile.exists()) {
                                    throw new PlanException("The reference for '" + referencePath + "' within '<Id>'"
                                            + " on line " + subStructureElement.getLine()
                                            + " in '"+subStructureElement.getFile().getAbsolutePath()+"'does not exist!");
                                } else {
                                    StructurePlanJob job = new StructurePlanJob(referenceFile);
                                    required.putAll(job.required);
                                }
                            }
                        }
                    } else {
                        subStructureElement.selectSingleElement("Path").checkNotEmpty();
                        File file = new File(referencedPath);
                        if (!file.exists()) {
                            throw new PlanException("Element <SubStructure> on line " + subStructureElement.getLine() 
                                    + " in '"+subStructureElement.getFile().getAbsolutePath()+"'"
                                    + " referes to a file that doesn't exist!");
                        }
                        StructurePlanJob job = new StructurePlanJob(file);
                        required.putAll(job.required);
                    }

                } else {

                    if (!StructureAPI.canHandle(subStructureElement)) {
                        String[] types = splitType(placementElement, placementElement.getType());
                        putRequired(types[0], types[1]);
                    }
                }
            }

        }

        private String[] splitType(SimpleElement element, String type) throws Exception {
            String[] pluginPlacement = type.split(".");
            if (pluginPlacement.length == 0) {
                return new String[]{"SettlerCraft", type};
            } else if (pluginPlacement.length == 2) {
                return new String[]{pluginPlacement[0], pluginPlacement[1]};
            } else {
                throw new PlanException("Invalid format for element <" + element.getElementName() + ">"
                        + " on line " + element.getLine() + " of '"+element.getFile().getAbsolutePath()+"'"
                        + ". Format should be: SomePluginName.SomeTypeName");
            }
        }

        private void putRequired(String plugin, String type) {
            Set<String> requiredSet = required.get(plugin);
            if (requiredSet == null) {
                requiredSet = new HashSet<>();
                required.put(plugin, requiredSet);
            }
            requiredSet.add(type);
        }

        private boolean checkSupportAndRemove(String plugin, String handlerType) {
            Set<String> requiredSet = required.get(plugin);
            if (requiredSet != null) {
                if (requiredSet.remove(handlerType) && requiredSet.isEmpty()) {
                    required.remove(plugin);
                    return true;
                }
            }
            return false;
        }

        public boolean isReadyToStage() {
            return required.isEmpty();
        }

    }

    private class StructurePlanJobTask extends RecursiveTask<StructurePlanJob> {

        private final File structurePlanFile;
        private StructurePlanJob job;

        public StructurePlanJobTask(File structurePlanFile) {
            this.structurePlanFile = structurePlanFile;
        }

        @Override
        public StructurePlanJob compute() {
            try {
                job = new StructurePlanJob(structurePlanFile);
            
            } catch (PlanException exception) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, exception.getMessage(), exception);
            } catch (Exception ex) {
                Logger.getLogger(GlobalPlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return job;
        }

        public StructurePlanJob getJob() {
            return job;
        }

    }

    private class SearchReferencesTask extends RecursiveTask<Map<String, String>> {

        private File searchDirectory;
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
                        Logger.getLogger(GlobalPlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Entry<String, String> entry = getEntry(f);
                    if (entry != null) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            for (SearchReferencesTask task : tasks) {
                Map<String, String> childReferences = task.join();
                for (Entry<String, String> s : childReferences.entrySet()) {
                    if (result.get(s.getKey()) == null) {
                        result.put(s.getKey(), s.getValue());
                    } else {
                        throw new PlanException("Duplicate id references! StructurePlanFile: " + s.getValue() + " and file " + result.get(s.getKey()) + " have the same id!");
                    }
                }
            }
            return result;
        }

        private Entry<String, String> getEntry(File f) {
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
                Logger.getLogger(GlobalPlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

}
