/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.plan;

import com.sc.module.structureapi.structure.StructureAPI;
import static com.sc.module.structureapi.structure.StructureAPI.getPlugin;
import com.sc.module.structureapi.structure.StructurePlan;
import com.sc.module.structureapi.structure.dataplans.Elements;
import com.sc.module.structureapi.structure.schematic.SchematicManager;
import com.sk89q.worldedit.data.DataException;
import construction.exception.StructureDataException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {

    public static final String PLANSHOP = "Buy & Build";
    public static final String PLAN_FOLDER = "Plans";
    public static final String SCHEMATIC_PLAN_FOLDER = "SchematicToPlan";
    private static StructurePlanManager instance;
    private final Map<String, StructurePlan> plans = new HashMap<>();
    
    

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
    
    private void init() {
        File planFolder = new File(StructureAPI.getPlugin().getDataFolder(), PLAN_FOLDER);
        if(!planFolder.exists()) {
            planFolder.mkdirs();
        }
        
        File schematicToPlanFolder = new File(StructureAPI.getPlugin().getDataFolder(), SCHEMATIC_PLAN_FOLDER);
        if(!schematicToPlanFolder.exists()){
            schematicToPlanFolder.mkdirs();
        }
    }
    
    public static File getStructurePlanFolder() {
        return new File(getPlugin().getDataFolder(), PLAN_FOLDER);
    }
    
    public final void load(final Callback callback) {
        init();
        generate();
       
        String[] extensions = {"xml"};
        File planFolder = getStructurePlanFolder();

        Iterator<File> it = FileUtils.iterateFiles(planFolder, extensions, true);
        List<File> files = new LinkedList();
        while (it.hasNext()) {
            files.add(it.next());
        }
        
        Iterator<File> fileIterator = files.iterator();
        //TODO Preload schematics??
//        final Set<File> schematics = new HashSet<>();
        final int total = files.size();
        final AtomicInteger count = new AtomicInteger(0);
        final ExecutorService executor = Executors.newCachedThreadPool();
        

        while (fileIterator.hasNext()) {
            final File file = fileIterator.next();
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        // Load plan
                        StructurePlan plan = new StructurePlan(file);
                        plan.load();
                        
//                      Load schematic if not loaded
                        SchematicManager.getInstance().getSchematic(plan.getSchematic());
                        put(plan);
                    } catch (DocumentException | IOException  | StructureDataException ex) {
                        Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (DataException ex) {
                        Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        count.incrementAndGet();
                        if(count.get() == total) {
                            callback.onComplete();
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    executor.shutdownNow();
                                    try {
                                        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
                                    } catch (InterruptedException ex) {
                                        java.util.logging.Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }).start();
                        }
                    }
                }
            });
        }
        
    }

    /**
     * Private Constructor
     */
    private StructurePlanManager() {
    }
    
    public void clear() {
        plans.clear();
    }
    
    public static void generate()  {
        File folder = new File(getPlugin().getDataFolder(), SCHEMATIC_PLAN_FOLDER);

        Iterator<File> it = FileUtils.iterateFiles(folder, new String[]{"schematic"}, true);
        while (it.hasNext()) {
            File schematic = it.next();

            Document d = DocumentHelper.createDocument();
            d.addElement(Elements.ROOT)
                    .addElement(Elements.STRUCTUREAPI)
                    .addElement(Elements.SCHEMATIC)
                    .addElement(Elements.STRUCTURE)
                    .setText(schematic.getName());

            File plan = new File(schematic.getParent(), FilenameUtils.getBaseName(schematic.getName()) + ".xml");

            
            try {
                XMLWriter writer = new XMLWriter(new FileWriter(plan));
                writer.write(d);
                writer.close();
                StructurePlan sp = new StructurePlan(plan);
                sp.load();
                if(sp.getCategory().equals("Default") && !schematic.getParentFile().getName().equals(SCHEMATIC_PLAN_FOLDER)) {
                    sp.setCategory(schematic.getParentFile().getName());
                }
                
                sp.save();
                
                
            } catch (DocumentException ex) {
                Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
            } catch (StructureDataException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    private void put(StructurePlan plan) {
        if(plan == null) {
            throw new AssertionError("Plan was null");
        }
        
        String path = plan.getRelativePath().substring(0, plan.getRelativePath().length() - 4);
        
        plans.put(path, plan);
        
    }
    

    /**
     * Gets a plan by it's corresponding id
     *
     * @param id The id of the plan
     * @return The structure plan with the corresponding id
     */
    public StructurePlan get(String id) {
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

 


    

 


    
    public interface Callback {
        
        public void onComplete();
    }
    

}
