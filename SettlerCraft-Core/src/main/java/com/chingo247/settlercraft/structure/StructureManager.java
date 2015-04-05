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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.event.EventManager;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.event.StructureCreateEvent;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.placement.SchematicPlacement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.SubStructuredPlan;
import com.chingo247.settlercraft.util.PlacementUtil;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import com.chingo247.xcore.core.ILocation;
import com.chingo247.xcore.core.IWorld;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * Manages all the structures of a certain world
 *
 * @author Chingo
 */
public class StructureManager {

    private static final String STRUCTURE_PLAN_FILE = "structureplan.xml";
    private static final StructureDAO STRUCTURE_DAO = new StructureDAO();
    private final String world;
    private final ExecutorService service;
//    private final Map<Long, Structure> structures;
    private final StructureAPI structureAPI;

    protected StructureManager(String world, ExecutorService service, StructureAPI structureAPI) {
        this.world = world;
        this.service = service;
//        this.structures = Maps.newHashMap();
        this.structureAPI = structureAPI;
    }

    protected final ExecutorService getService() {
        return service;
    }

 

    public final Structure getStructure(long id) {
        StructureEntity entity = STRUCTURE_DAO.find(id);
        if (entity == null) {
            return null;
        }
        return new Structure(this, entity);
    }

    public Structure createStructure(StructurePlan plan, Vector position, Direction direction) throws StructureException {
        Structure structure = createUninitizalizedStructure(plan, direction, position);
        if (structure != null) {
            structure.setState(StructureState.CREATED);
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }

        return structure;
    }

    public Structure createStructure(Placement placement, Vector position, Direction direction) throws StructureException {
        Structure structure = createUninitizalizedStructure(placement, direction, position);
        if (structure != null) {
            structure.setState(StructureState.CREATED);
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));

        }

        return structure;
    }

    public Structure createUninitizalizedStructure(StructurePlan plan, Direction direction, Vector position) throws StructureException {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        System.out.println("Position is " + position);
        if (plan instanceof SubStructuredPlan) {
            System.out.println("Structure is SubstructurePlan!");
            return null;
        } else {
            System.out.println("Structure is StructurePlan!");
            Placement placement = plan.getPlacement();

            checkLocation(placement, position);

            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getMaxPosition());

            System.out.println("Min: " + min);
            System.out.println("Max: " + max);
            System.out.println("Direction: " + direction);
            
            SettlerCraftWorld w = SettlerCraft.getInstance().getWorld(world);

            StructureEntity entity = new StructureEntity(w.getName(), w.getUUID(), new CuboidDimension(min, max), direction);
            entity = STRUCTURE_DAO.save(entity); // store and get the ID
            Structure structure = new Structure(this, entity);
            File structureDir = new File(getStructuresDirectory(), String.valueOf(entity.getId()));

            // Overwrite old one if exists...
            if (structureDir.exists()) {
                structureDir.delete();
            }
            structureDir.mkdirs();

            File planFile = plan.getFile();

            try {
                if (placement instanceof SchematicPlacement) {
                    SchematicPlacement schematicPlacement = (SchematicPlacement) placement;
                    File schematicFile = schematicPlacement.getSchematic().getFile();
                    Files.copy(schematicFile, new File(structureDir, schematicFile.getName()));
                }
                Files.copy(planFile, new File(structureDir, STRUCTURE_PLAN_FILE));

            } catch (IOException ex) {
                structureDir.delete();
                Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return structure;
        }
    }

    private void checkLocation(Placement p, Vector position) throws StructureException {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world);
        ILocation l = w.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());

        CuboidDimension placementDimension = new CuboidDimension(p.getMinPosition().add(position), p.getMaxPosition().add(position));

        if (placementDimension.getMinY() <= 1) {
            throw new StructureException("Structure must be placed at a minimum height of 1");
        }

        if (placementDimension.getMaxY() > w.getMaxHeight()) {
            throw new StructureException("Structure will reach above the world's max height (" + w.getMaxHeight() + ")");
        }

        if (CuboidDimension.isPositionWithin(placementDimension, spawnPos)) {
            throw new StructureException("Structure overlaps the world's spawn...");
        }
        

        if (STRUCTURE_DAO.overlaps(w.getUUID(), p)) {
            throw new StructureException("Structure overlaps another structure...");
        }
    }

    protected final File getStructuresDirectory() {
        File f = new File(SettlerCraft.getInstance().getWorkingDirectory(), "worlds//" + world+ "//structures");
        f.mkdirs(); // creates if not exists..
        return f;
    }

    public Structure createUninitizalizedStructure(Placement placement, Direction direction, Vector position) {
        //putStructure(null);
        throw new UnsupportedOperationException();
    }

    protected void load() {
       
    }

    void build(Structure structure, AsyncEditSession editSession, Options options, boolean force) throws StructureException {
        //TODO: implement build structure
        //TODO: ignore substructure areas using masks (Ignore Mask)
        System.out.println("Build structure....");
        PlayerEntry entry = editSession.getPlayer();
        AsyncPlacement placement = new AsyncPlacement(entry, structure.getStructurePlan().getPlacement());
        Vector position = structure.getDimension().getMinPosition();
        
        placement.place(editSession, position, options);
    }

    public final void demolish(AsyncEditSession editSession, Options options, boolean force) throws StructureException {
        //TODO: implement demolish structure
        //TODO: Check if there are substructures...
    }

    public void stop(Structure structure, boolean force) {

    }
    
    AsyncEditSession getSession(UUID anUUID) {
        SettlerCraftWorld w = SettlerCraft.getInstance().getWorld(world);
        Player ply = structureAPI.getPlayer(anUUID);
        AsyncEditSession editSession;
        if (ply == null) {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1);
        } else {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1, ply);
        }
        return editSession;
    }

}
