/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.plan.data.Elements;
import com.chingo247.settlercraft.structure.schematic.SchematicManager;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {

    public final File PLAN_FOLDER = new File(SettlerCraft.getInstance().getDataFolder(), "Plans");
    public final File SCHEMATIC_TO_PLAN_FOLDER = new File(SettlerCraft.getInstance().getDataFolder(), "SchematicToPlan");
    private static StructurePlanManager instance;
    private final Map<String, StructurePlan> plans = new HashMap<>();
    private final Map<Long, StructurePlan> structures = new HashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private boolean loading = false;

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
     * Creates the Folders for StructurePlans and SchematicToPlan if they don't exist
     */
    public void init() {
        if (!PLAN_FOLDER.exists()) {
            PLAN_FOLDER.mkdirs();
        }
        if (!SCHEMATIC_TO_PLAN_FOLDER.exists()) {
            SCHEMATIC_TO_PLAN_FOLDER.mkdirs();
        }
    }

    /**
     * Gets the folder where all StructurePlans are located
     *
     * @return The StructurePlan folder
     */
    public File getPlanFolder() {
        return PLAN_FOLDER;
    }
    
    public String getRelativePath(File config) {
        String path = config.getAbsolutePath();
        String minus = "\\plugins\\SettlerCraft\\";
        path = path.substring(path.indexOf(minus) + minus.length());
        int length = path.length();
        path = path.substring(0, length - 4); // minus XML
        return path;
    }

    public synchronized void load(final Callback callback) {
        if (!loading) {
            loading = true;
            plans.clear();

            String[] extensions = {"xml"};
            File planFolder = getPlanFolder();

            Iterator<File> it = FileUtils.iterateFiles(planFolder, extensions, true);
            List<File> files = new LinkedList();
            while (it.hasNext()) {
                files.add(it.next());
            }

            Iterator<File> fileIterator = files.iterator();
            final int total = files.size();
            final List<File> done = Collections.synchronizedList(new ArrayList<File>());
            
//            System.out.println("Total files: " + total);
            

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
                            putPlan(plan);
                        } catch (DocumentException | IOException | DataException | StructureDataException ex) {
                            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            done.add(file);
                            int count = done.size();
//                            System.out.println("Loaded ["+count+"]["+((count*100)/(total))+"%] " + file.getName());
                            if (count == total) {
                                callback.onComplete();
//                                System.out.println("on complete: " + total);
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (!executor.isShutdown()) {
                                            executor.shutdownNow();
                                            try {
                                                executor.awaitTermination(10, TimeUnit.MILLISECONDS);
                                            } catch (InterruptedException ex) {
                                                java.util.logging.Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        loading = false;
                                    }
                                }).start();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Private Constructor
     */
    private StructurePlanManager() {
    }

    /**
     * Automatically generates structure plans in SchematicToPlan folder
     */
    public void generate() {
        // Scan the folder called 'SchematicToPlan' for schematic files
        Iterator<File> it = FileUtils.iterateFiles(SCHEMATIC_TO_PLAN_FOLDER, new String[]{"schematic"}, true);

        int count = 0;
        long start = System.currentTimeMillis();
        
        // Generate Plans
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
                if (sp.getCategory().equals("Default") && !schematic.getParentFile().getName().equals(SCHEMATIC_TO_PLAN_FOLDER.getName())) {
                    sp.setCategory(schematic.getParentFile().getName());
                }

                sp.save();

            } catch (DocumentException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | StructureDataException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            count++;
        }
        if(count > 0) {
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "Generated " + count + " plans in " + (System.currentTimeMillis() - start) + "ms");
        }
        
    }

    private void putPlan(StructurePlan plan) {
        if (plan == null) {
            throw new AssertionError("Plan was null");
        }

        String path = getRelativePath(plan.getConfigXML());

        plans.put(path, plan);

    }

    /**
     * Gets a plan by it's corresponding id
     *
     * @param id The id of the plan
     * @return The structure plan with the corresponding id
     */
    public StructurePlan getPlan(String id) {
        return plans.get(id);
    }

    public StructurePlan getPlan(Structure structure) throws StructureDataException, DocumentException {
        StructurePlan plan = structures.get(structure.getId());
        if (plan != null) {
            return plan;
        }
        File config = structure.getConfig();
        if (config == null) {
            throw new StructureDataException("Missing 'StructurePlan.xml' for structure: " + structure);
        }
        
        StructurePlan sp = new StructurePlan(config);
        try {
            sp.load();
            structures.put(structure.getId(), plan);
        } catch (IOException ex) {
            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sp;

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

    public void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

}
