/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.model.BaseSettlerRepository;
import com.chingo247.settlercraft.core.model.BaseSettlerNode;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.structure.StructureRepository;
import com.chingo247.settlercraft.structureapi.persistence.legacy.Structure;
import com.chingo247.settlercraft.structureapi.persistence.legacy.hibernate.HibernateUtil;
import com.chingo247.xplatform.core.IWorld;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import com.chingo247.settlercraft.structureapi.persistence.legacy.*;
import com.chingo247.settlercraft.structureapi.model.structure.StructureStatus;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldRepository;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureWorldRepository;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldNode;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.IStructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.PlacementTypes;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.PlacementXMLConstants;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.StructurePlanXMLConstants;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.BaseElement;
import org.hibernate.HibernateException;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
class BukkitSettlerCraftUpdater {

    private static final String PREFIX = "[SettlerCraft]: ";
    private static final int BULK_SIZE = 1000;
    private final GraphDatabaseService graph;
    private final Set<String> worldsProcessed;
    private final IStructureWorldRepository structureWorldRepository;
    private final IStructureRepository structureRepository;
    private final BaseSettlerRepository settlerDAO;
    private final IStructureAPI structureAPI;
    private final File oldStructuresDirectory;
    private final ExecutorService executorService;
    private final Map<String,IStructurePlan> plans;

    public BukkitSettlerCraftUpdater(GraphDatabaseService graph, IStructureAPI structureAPI) {
        this.graph = graph;
        this.worldsProcessed = new HashSet<>();
        this.structureWorldRepository = new StructureWorldRepository(graph);
        this.structureRepository = new StructureRepository(graph);
        this.settlerDAO = new BaseSettlerRepository(graph);
        this.structureAPI = structureAPI;
        this.oldStructuresDirectory = new File("plugins//SettlerCraft//Structures");
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.plans = Maps.newHashMap();
    }

    public void update() {
        System.out.println(PREFIX + "Transferring SettlerCraft-1.x structures to SettlerCraft-2.x");
        
        
        long total = getStructureCount();
        if(total > 10_000) {
            System.out.println(PREFIX + "This might take a while...");
        }

        System.out.println(PREFIX + "Retrieving structures...");
       
        
        List<Structure> structures = getStructures();
        if(structures.isEmpty()) {
            System.out.println(PREFIX + "There are no structures that need updating...");
            return;
        }
        
        System.out.println(PREFIX + "Updating a total of " + total + " structures");
        
        System.out.println(PREFIX + "Preparing structures");
        prepareStructures(structures);
        
        int startIndex = 0;
        
        System.out.println(PREFIX + "Copying updating structures");
        
        while(startIndex < total) {
            processStructures(structures.listIterator(startIndex));
            startIndex+=BULK_SIZE;
        }
        
        System.out.println(PREFIX + "Update complete");
        
        executorService.shutdown();
    }

    private void processStructures(Iterator<Structure> structureIterator) {

        try (Transaction tx = graph.beginTx()) {
            Map<String, StructureWorldNode> worlds = Maps.newHashMap();

            List<Structure> structures = Lists.newArrayList();
             while(structureIterator.hasNext()) {
                Structure s = structureIterator.next();
                StructureWorldNode w;
                if (!worldsProcessed.contains(s.getWorldName())) {
                    w = registerWorld(s.getWorldName());
                    if (w != null) {
                        worldsProcessed.add(w.getName());
                        worlds.put(w.getName(), w);
                    } else {
                        continue;
                    }
                }
                
                w = worlds.get(s.getWorldName());
                try {
                    processStructure(s, w);
                    structures.add(s);
                } catch (IOException ex) {
                    Logger.getLogger(BukkitSettlerCraftUpdater.class.getName()).log(Level.SEVERE, null, ex);
                    tx.failure();
                    return;
                }
            }
            
            bulkDelete(structures);
            
            tx.success();
        }

    }
    
    
    

    private void processStructure(Structure structure, StructureWorldNode world) throws IOException {
        Vector position = structure.getPosition();

        Dimension dim = structure.getDimension();
        CuboidRegion region = new CuboidRegion(dim.getMinPosition(), dim.getMaxPosition());

        com.chingo247.settlercraft.structureapi.model.structure.StructureNode sn = structureRepository.addStructure(world, structure.getName(), position, region, structure.getDirection(), structure.getRefundValue());
        if (structure.getStructureRegion() != null) {
            sn.getNode().setProperty("WGRegion", structure.getStructureRegion()); // Set worldguardregion
        }

        world.addStructure(sn);

        for (PlayerOwnership owner : structure.getOwnerships()) {
            IBaseSettler settler = settlerDAO.findByUUID(owner.getPlayerUUID());
            if (settler == null) {
                settler = addSettler(owner.getName(), owner.getPlayerUUID());
            }
            
            if(owner.getOwnerType() == PlayerOwnership.Type.FULL) {
                sn.addOwner(settler, StructureOwnerType.MASTER);
            } else {
                sn.addOwner(settler, StructureOwnerType.OWNER);
            }
        }
        
        for(PlayerMembership member : structure.getMemberships()) {
            IBaseSettler settler = settlerDAO.findByUUID(member.getUUID());
            if (settler == null) {
                settler = addSettler(member.getName(), member.getUUID());
            }
            
            sn.addOwner(settler, StructureOwnerType.MEMBER);
        }
        
        sn.setStatus(getStatus(structure));
        sn.setCreatedAt(structure.getLog().getCreatedAt().getTime());
        
        File structureDirectory = new File(structureAPI.getStructuresDirectory(world.getName()),String.valueOf(sn.getId()));
        File oldTempDirectory = new File(getOldDirectory(structure), "temp");
        if(structureDirectory.exists()) {
            structureDirectory.delete();
        }
        structureDirectory.mkdirs();
        
        for(File tempFile : oldTempDirectory.listFiles()) {
            File newDestinaion = new File(structureDirectory, tempFile.getName());
            Files.copy(tempFile, newDestinaion);
        }
        

    }
    
    private StructureStatus getStatus(Structure structure) {
        switch(structure.getState()) {
            case BUILDING : return StructureStatus.BUILDING;
            case COMPLETE : return StructureStatus.COMPLETED;
            case DEMOLISHING : return StructureStatus.DEMOLISHING;
            default: return StructureStatus.ON_HOLD;
        }
    }

    private StructureWorldNode registerWorld(String worldName) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(worldName);
        if (w == null) {
            return null;
        }

        StructureWorldNode world = structureWorldRepository.registerWorld(w.getName(), w.getUUID());
        
        return world;
    }

    private List<Structure> getStructures() {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structure = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        session.close();
        return structure;
    }

    private void bulkDelete(List<Structure> structures) {
        org.hibernate.Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            for(Structure s : structures) {
                session.delete(s);
            }
            
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println("[SettlerCraft]: Something went wrong during bulkdelete");
                    System.out.println("[SettlerCraft]: Couldn't rollback transaction");
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private long getStructureCount() {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        long structure = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).count();
        session.close();
        return structure;
    }

    private BaseSettlerNode addSettler(String playerName, UUID playerUUID) {
        Node settlerNode = graph.createNode(com.chingo247.settlercraft.core.model.BaseSettlerNode.LABEL);
        settlerNode.setProperty(com.chingo247.settlercraft.core.model.BaseSettlerNode.UUID_PROPERTY, playerUUID.toString());
        settlerNode.setProperty(com.chingo247.settlercraft.core.model.BaseSettlerNode.NAME_PROPERTY, playerName);
        return new BaseSettlerNode(settlerNode);
    }
    
    private File getOldDirectory(Structure structure) {
        File directory = new File(oldStructuresDirectory, structure.getWorldName() + "//" + structure.getId());
        return directory;
    }
    
    
    private void prepareStructures(final List<Structure> structures) {
        List<Future> tasks = new ArrayList<>(structures.size());
        for(final Structure s : structures) {
            
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    
                    // No world for this structure
                    if(SettlerCraft.getInstance().getWorld(s.getWorldName()) == null) {
                        return;
                    }
                    
                    File oldDirectory = getOldDirectory(s);
                    
                    if(!oldDirectory.exists()) {
                        return; // Resolved
                    }
                    
                    File structurePlanFile = new File(oldDirectory, "StructurePlan.xml");
                    if(!structurePlanFile.exists()) {
                        System.out.println(PREFIX + "Missing 'StructurePlan.xml' within " + structurePlanFile.getAbsolutePath());
                        System.out.println(PREFIX + "Please resolve the issue or remove the directory");
                        return;
                    }
                    
                    try {
                        Document planDoc = new SAXReader().read(structurePlanFile);
                        
                        org.dom4j.Node schematicNode = planDoc.selectSingleNode("StructurePlan/SettlerCraft/Schematic");
                        if(schematicNode == null) {
                            System.out.println(PREFIX + "Missing 'Schematic' element in " + structurePlanFile.getAbsolutePath());
                            System.out.println(PREFIX + "Please resolve the issue or remove the directory");
                            return;
                        }
                        
                        File schematicFile = new File(oldDirectory, schematicNode.getStringValue().trim());
                        if(!schematicFile.exists()) {
                            System.out.println(PREFIX + "Missing schematic file '" + schematicFile.getName() + " within " + oldDirectory.getAbsolutePath());
                            System.out.println(PREFIX + "Please resolve the issue or remove the directory");
                            return;
                        }
                        
                        File tempDirectory = new File(oldDirectory, "temp");
                        if(tempDirectory.exists()) {
                            tempDirectory.delete();
                        }
                        tempDirectory.mkdirs();
                        
                        File tempPlanFile = new File(tempDirectory, "structureplan.xml");
                        
                        
                        Document d = DocumentHelper.createDocument();
                        Element root = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT);
                        d.add(root);
                        
                        Element nameElement = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_NAME_ELEMENT);
                        nameElement.setText(s.getName());
                        root.add(nameElement);
                        
                        Element placementElement = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);
                        
                        Element typeElement = new BaseElement(PlacementXMLConstants.TYPE_ELEMENT);
                        typeElement.setText(PlacementTypes.SCHEMATIC);
                        
                        Element schematicElement = new BaseElement(PlacementXMLConstants.SCHEMATIC_ELEMENT);
                        schematicElement.setText(schematicFile.getName());
                        
                        placementElement.add(typeElement);
                        placementElement.add(schematicElement);
                        
                        root.add(placementElement);
                        
                        OutputFormat format = OutputFormat.createPrettyPrint();
                        XMLWriter writer = null;
                        try {
                            
                            tempPlanFile.createNewFile();
                            writer = new XMLWriter(new FileOutputStream(tempPlanFile), format);
                            writer.write(d);
                            Files.copy(schematicFile, new File(tempDirectory, schematicFile.getName()));
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(BukkitSettlerCraftUpdater.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(BukkitSettlerCraftUpdater.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            if(writer != null) {
                                try {
                                    writer.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(BukkitSettlerCraftUpdater.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    } catch (DocumentException ex) {
                        Logger.getLogger(BukkitSettlerCraftUpdater.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
        }
        
        for(Future f : tasks) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(BukkitSettlerCraftUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    protected File getDirectoryForStructure(WorldNode worldNode, Structure structureNode) {
        File structuresDirectory = structureAPI.getStructuresDirectory(worldNode.getName());
        File structureDir = new File(structuresDirectory, String.valueOf(structureNode.getId()));
        return structureDir;
    }
    
    
    
}