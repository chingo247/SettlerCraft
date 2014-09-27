/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.plan;

import construction.exception.StructureDataException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {

    public static final String PLANSHOP = "Buy & Build";
    private static StructurePlanManager instance;
    private final Map<String, StructurePlan> plans = new HashMap<>();
    private final Executor executor;
    

    /**
     * Gets the instance of this API
     *
     * @return instance of StructurePlanManager
     */
    public static StructurePlanManager getInstance() {
        if (instance == null) {
            instance = new StructurePlanManager();
        }
        return instance;
    }

    /**
     * Private Constructor
     */
    private StructurePlanManager() {
        final int THREADS = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(THREADS);
    }
    


    /**
     * Loads all structure plans from target directory
     *
     * @param directory The directory
     * @param callback
     */
    public final void load(final File directory, final Callback callback) {
        String[] extensions = {"xml"};

        Iterator<File> it = FileUtils.iterateFiles(directory, extensions, true);
        List<File> files = new LinkedList();
        while (it.hasNext()) {
            files.add(it.next());
        }

        Iterator<File> fileIterator = files.iterator();
        //TODO Preload schematics??
        final Set<File> schematics = new HashSet<>();
        final int THREADS = Runtime.getRuntime().availableProcessors();
        final int total = files.size();
        final AtomicInteger count = new AtomicInteger(0);

        while (fileIterator.hasNext()) {
            final File file = fileIterator.next();
            executor.execute(new StructurePlanTask(file) {

                @Override
                public void onComplete(StructurePlan plan) {
                    System.out.println("Plan: " + plan.getConfig().getAbsolutePath());
                    System.out.println("Schematic: " + plan.getSchematic().getAbsolutePath());
                    System.out.println("Relative: " + plan.getRelativePath());
                    putStructurePlan(plan);
                    schematics.add(plan.getSchematic());
                    
                    if (count.incrementAndGet() == total) {
                        callback.onComplete();
//                        loadSchematics(schematics, callback);
                        return;
                    }
                }
            });
        }
    }
    
    private void putStructurePlan(StructurePlan plan) {
        if(plan == null) {
            throw new AssertionError("Plan was null");
        }
        
        String id = plan.getId();
        if(id == null) {
            throw new AssertionError("Id was null for " + plan.getConfig().getAbsolutePath());
        }
        
        plans.put(id, plan);
        
    }
    

    /**
     * Gets a plan by it's corresponding id
     *
     * @param id The id of the plan
     * @return The structure plan with the corresponding id
     */
    public StructurePlan getPlan(String id) {
        StructurePlan plan;
        return plans.get(id);
    }

    /**
     * Gets the list of structureplans
     *
     * @return A list of structureplans
     */
    public List<StructurePlan> getPlans() {
        return new ArrayList<>(plans.values());
    }

 


    

 

    private abstract class StructurePlanTask implements Runnable {

        private final File xml;

        public StructurePlanTask(File xml) {
            this.xml = xml;
        }

        @Override
        public void run() {
            try {
                StructurePlan plan = StructurePlan.load(xml);
                
                
                onComplete(plan);
            } catch (StructureDataException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        

        public abstract void onComplete(StructurePlan plan);

    }

    
    public interface Callback {
        
        public void onComplete();
    }
    

}
