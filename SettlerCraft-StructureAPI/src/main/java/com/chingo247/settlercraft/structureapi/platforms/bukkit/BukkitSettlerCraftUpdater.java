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
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldDAO;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureWorldNode;
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
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
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
    private static final long VERY_LONG = 1000*60*60*24*7*52;
    private final GraphDatabaseService graph;
    private final Set<String> worldsProcessed;
    private final WorldDAO worldDAO;
    private final StructureDAO structureDAO;
    private final SettlerDAO settlerDAO;
    private final IStructureAPI structureAPI;
    private final File oldStructuresDirectory;
    private final ExecutorService executorService;
    private Map<String,StructurePlan> plans;

    public BukkitSettlerCraftUpdater(GraphDatabaseService graph, IStructureAPI structureAPI) {
        this.graph = graph;
        this.worldsProcessed = new HashSet<>();
        this.worldDAO = new WorldDAO(graph);
        this.structureDAO = new StructureDAO(graph);
        this.settlerDAO = new SettlerDAO(graph);
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
                if (!worldsProcessed.contains(s.getWorldName())) {
                    WorldNode w = registerWorld(s.getWorldName());
                    if (w != null) {
                        worldsProcessed.add(w.getName());
                        worlds.put(w.getName(), new StructureWorldNode(w));
                    } else {
                        continue;
                    }
                }
                StructureWorldNode w = worlds.get(s.getWorldName());
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
    
    
    

    private void processStructure(Structure structure, StructureWorldNode worldNode) throws IOException {
        Vector position = structure.getPosition();

        Dimension dim = structure.getDimension();
        CuboidRegion region = new CuboidRegion(dim.getMinPosition(), dim.getMaxPosition());

        StructureNode sn = structureDAO.addStructure(structure.getName(), position, region, structure.getDirection(), structure.getRefundValue());
        if (structure.getStructureRegion() != null) {
            sn.getRawNode().setProperty("WGRegion", structure.getStructureRegion()); // Set worldguardregion
        }

        worldNode.addStructure(sn);

        for (PlayerOwnership owner : structure.getOwnerships()) {
            SettlerNode settler = settlerDAO.find(owner.getPlayerUUID());
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
            SettlerNode settler = settlerDAO.find(member.getUUID());
            if (settler == null) {
                settler = addSettler(member.getName(), member.getUUID());
            }
            
            sn.addOwner(settler, StructureOwnerType.MEMBER);
        }
        
        sn.setConstructionStatus(getStatus(structure));
        sn.setCreatedAt(structure.getLog().getCreatedAt().getTime());
        
        File structureDirectory = new File(structureAPI.getStructuresDirectory(worldNode.getName()),String.valueOf(sn.getId()));
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
    
    private ConstructionStatus getStatus(Structure structure) {
        switch(structure.getState()) {
            case BUILDING : return ConstructionStatus.BUILDING;
            case COMPLETE : return ConstructionStatus.COMPLETED;
            case DEMOLISHING : return ConstructionStatus.DEMOLISHING;
            default: return ConstructionStatus.ON_HOLD;
        }
    }

    private WorldNode registerWorld(String world) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world);
        if (w == null) {
            return null;
        }

        WorldNode worldNode = worldDAO.find(w.getUUID());
        if (worldNode == null) {
            worldDAO.addWorld(w.getName(), w.getUUID());
            worldNode = worldDAO.find(w.getUUID());
            if (worldNode == null) {
                System.out.println("Something went wrong during creation of the 'WorldNode' for " + world); // SHOULD NEVER HAPPEN
                return null;
            }
        }
        return worldNode;
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

    private SettlerNode addSettler(String playerName, UUID playerUUID) {
        Node settlerNode = graph.createNode(com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode.LABEL);
        settlerNode.setProperty(com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode.UUID_PROPERTY, playerUUID.toString());
        settlerNode.setProperty(com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode.NAME_PROPERTY, playerName);
        return new SettlerNode(settlerNode);
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
    
    
    protected File getDirectoryForStructure(WorldNode worldNode, StructureNode structureNode) {
        File structuresDirectory = structureAPI.getStructuresDirectory(worldNode.getName());
        File structureDir = new File(structuresDirectory, String.valueOf(structureNode.getId()));
        return structureDir;
    }
    
}