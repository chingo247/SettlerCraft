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
package com.chingo247.settlercraft;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.StructureManager;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.exception.WorldConfigException;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.SubStructuredPlan;
import com.chingo247.settlercraft.structure.restriction.StructureRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureWorldRestriction;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import com.chingo247.settlercraft.world.WorldConfig;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.AbstractWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BaseBiome;
import com.sk89q.worldedit.world.registry.WorldData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Maps;

/**
 *
 * @author Chingo
 */
public class SettlerCraftWorldImpl extends AbstractWorld implements SettlerCraftWorld{

    private final SettlerCraft sc;
    private final StructureManager structureManager;

    private final World world;
    private final File worldFile;
    private WorldConfig config;
    private Map<Class, StructureRestriction> restrictions;
    private UUID worldUUID;

    protected SettlerCraftWorldImpl(StructureManager structureManager, UUID worldUUID, World world, File worldFile) {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(worldFile);
        Preconditions.checkNotNull(worldUUID);
        
        this.worldUUID = worldUUID;
        this.world = world;
        this.worldFile = worldFile;
        this.sc = SettlerCraft.getInstance();
        this.structureManager = structureManager;
        this.restrictions = Maps.newHashMap();
        this.restrictions.put(StructureWorldRestriction.class, new StructureWorldRestriction());

    }
    
    @Override
    public List<StructureRestriction> getStructureRestrictions() {
        return new ArrayList<>(restrictions.values());
    }

    public void addStructureRestriction(StructureRestriction restriction) {
        this.restrictions.put(restriction.getClass(), restriction);
    }

    protected void load() {
        try {
            this.config = worldFile.exists() ? WorldConfig.load(worldFile) : WorldConfig.createDefault(worldFile);
        } catch (WorldConfigException ex) {
            Logger.getLogger(SettlerCraftWorldImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public WorldConfig getSettlerCraftConfig() {
        return config;
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public UUID getUUID() {
        return worldUUID;
    }

    @Override
    public int getMaxY() {
        return world.getMaxY();
    }

    @Override
    public Structure createStructure(Placement placement, Vector position, Direction direction) throws StructureException {
        for (StructureRestriction restriction : restrictions.values()) {
            restriction.allow(this, placement.getDimension(), placement);
        }

        return structureManager.createStructure(placement, position, direction);
    }

    @Override
    public Structure createStructure(StructurePlan plan, Vector position, Direction direction) throws StructureException {
        if (plan instanceof SubStructuredPlan) {
            // Check recursively
        } else {
            for (StructureRestriction restriction : restrictions.values()) {
                restriction.allow(this, plan.getPlacement(), plan.getPlacement());
            }
        }

        return structureManager.createStructure(plan, position, direction);
    }

    @Override
    public Structure getStructure(long id) {
        return structureManager.getStructure(id);
    }

    @Override
    public boolean setBlock(Vector vector, BaseBlock bb, boolean bln) throws WorldEditException {
        return world.setBlock(vector, bb, bln);
    }

    @Override
    public int getBlockLightLevel(Vector vector) {
        return world.getBlockLightLevel(vector);
    }

    @Override
    public boolean clearContainerBlockContents(Vector vector) {
        return world.clearContainerBlockContents(vector);
    }

    @Override
    public void dropItem(Vector vector, BaseItemStack bis) {
        world.dropItem(vector, bis);
    }

    @Override
    public boolean regenerate(Region region, EditSession es) {
        return world.regenerate(region, es);
    }

    @Override
    public boolean generateTree(TreeGenerator.TreeType tt, EditSession es, Vector vector) throws MaxChangedBlocksException {
        return world.generateTree(tt, es, vector);
    }

    @Override
    public WorldData getWorldData() {
        return world.getWorldData();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return world.getEntities(region);
    }

    @Override
    public List<? extends Entity> getEntities() {
        return world.getEntities();
    }

    @Override
    public Entity createEntity(Location lctn, BaseEntity be) {
        return world.createEntity(lctn, be);
    }

    @Override
    public BaseBlock getBlock(Vector vector) {
        return world.getBlock(vector);
    }

    @Override
    public BaseBlock getLazyBlock(Vector vector) {
        return world.getLazyBlock(vector);
    }

    @Override
    public BaseBiome getBiome(Vector2D vd) {
        return world.getBiome(vd);
    }

    @Override
    public boolean setBiome(Vector2D vd, BaseBiome bb) {
        return world.setBiome(vd, bb);
    }

}
