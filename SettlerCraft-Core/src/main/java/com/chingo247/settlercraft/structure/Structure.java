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
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.persistence.dao.StructurePlayerDAO;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.StructurePlanReader;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class Structure {

    private static final String STRUCTURE_PLAN_FILE = "StructurePlan.xml";
    private static final StructureDAO STRUCTURE_DAO = new StructureDAO();
    private static final StructurePlayerDAO STRUCTURE_PLAYER_DAO = new StructurePlayerDAO();

    protected final StructureEntity structureEntity;
    private final StructureManager structureManager;
    private StructurePlan plan;

    Structure(StructureManager structureManager, StructureEntity structureEntity) {
        Preconditions.checkNotNull(structureManager);
        Preconditions.checkNotNull(structureEntity);
        this.structureEntity = structureEntity;
        this.structureManager = structureManager;
    }

    private File getDirectory() {
        return new File(structureManager.getStructuresDirectory(), String.valueOf(structureEntity.getId()));
    }

    private File getStructurePlanFile() {

        return new File(getDirectory(), STRUCTURE_PLAN_FILE);
    }

    public CuboidDimension getDimension() {
        return structureEntity.getDimension();
    }

    public Direction getDirection() {
        return structureEntity.getDirection();
    }

    public final Long getId() {
        return structureEntity.getId();
    }

    public final String getName() {
        return structureEntity.getName();
    }

    public final void setName(String name) {
        structureEntity.setName(name);
    }

    public final Double getValue() {
        return structureEntity.getValue();
    }

    public final void setValue(double value) {
        structureEntity.setValue(value);
    }

    public final void setState(StructureState state) {
//        if (state != structureEntity.getState()) {
//            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(this));
//        }
        structureEntity.setState(state);
    }

    public synchronized StructurePlan getStructurePlan() {
        if (plan == null) {
            StructurePlanReader reader = new StructurePlanReader();
            plan = reader.readFile(getStructurePlanFile());
            if (plan != null) {
                Placement placement = plan.getPlacement();
                placement.rotate(structureEntity.getDirection());
            }
        }

        return plan;
    }

    public final void demolish(UUID anUUID, Options options, boolean force) throws StructureException {
        demolish(getSession(anUUID), options, force);
    }

    public final void demolish(Player player, Options options, boolean force) throws StructureException {
        demolish(getSession(player.getUniqueId()), options, force);
    }

    public final void demolish(AsyncEditSession editSession, Options options, boolean force) throws StructureException {
        structureManager.demolish(editSession, options, force);
    }

    public final void build(UUID anUUID, Options options, boolean force) throws StructureException {
        build(getSession(anUUID), options, force);
    }

    public final void build(Player player, Options options, boolean force) throws StructureException {
        build(getSession(player.getUniqueId()), options, force);
    }

    public final void build(AsyncEditSession editSession, Options options, boolean force) throws StructureException {
        structureManager.build(this, editSession, options, force);
    }

    public Structure getParentStructure() {
        if (structureEntity.getParent() == null) {
            return null;
        }
        return structureManager.getStructure(structureEntity.getParent());
    }

    public List<Structure> getSubstructures() {
        if (structureEntity.getId() == null) {
            return null;
        }
        List<StructureEntity> entities = STRUCTURE_DAO.findChildren(structureEntity.getId());
        List<Structure> structures = new ArrayList<>(entities.size());
        for (StructureEntity entity : entities) {
            structures.add(structureManager.getStructure(entity.getId()));
        }
        return structures;
    }

    public List<Player> getOwners() {
        if (structureEntity.getId() == null) {
            return new ArrayList<>();
        }
        List<StructurePlayerEntity> playerEntitys = STRUCTURE_PLAYER_DAO.getOwnersForStructure(structureEntity.getId());

        List<Player> players = new ArrayList<>();
        for (StructurePlayerEntity p : playerEntitys) {
            Player player = SettlerCraft.getInstance().getStructureAPI().getPlayer(p.getPlayerUUID());
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    private AsyncEditSession getSession(UUID anUUID) {
        return structureManager.getSession(anUUID);
    }

}
