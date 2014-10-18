package com.chingo247.settlercraft.structure.construction.worldedit;

/*
 * Copyright (C) 2014 Chingo247
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

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalEntity;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Countable;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Chingo
 */
public class SmartClipboard extends CuboidClipboard {

    private final CuboidClipboard parent;
    private Comparator<StructureBlock> buildMode;

    public SmartClipboard(CuboidClipboard parent) {
        super(Vector.ZERO);
        Preconditions.checkNotNull(parent);
        this.parent = parent;
    }

    public CuboidClipboard getParent() {
        return parent;
    }
    
    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir) throws MaxChangedBlocksException {
        paste(editSession, newOrigin, noAir, false);
    }

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir, boolean entities) throws MaxChangedBlocksException {
       place(editSession, newOrigin.add(parent.getOffset()), noAir);
        if (entities) {
            parent.pasteEntities(newOrigin.add(parent.getOffset()));
        }
    }

    /**
     * Place blocks from the minimum corner using an alternative algorithm that skips blocks that
     * are already there.
     *
     * @param editSession
     * @param pos
     * @param noAir
     * @throws MaxChangedBlocksException
     */
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        Queue<StructureBlock> structurequeu = new PriorityQueue<>(10, buildMode);
        for (int y = 0; y < parent.getHeight(); y++) {
            for (int x = 0; x < parent.getWidth(); x++) {
                for (int z = 0; z < parent.getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = parent.getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                    structurequeu.add(new StructureBlock(v, b));
                }
            }
        }

        while (structurequeu.peek() != null) {
            StructureBlock b = structurequeu.poll();
            buildBlock(editSession, b.getBlock(), b.getPosition(), pos);
        }
        
    }

    private void buildBlock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
        if (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData()) {
            return;
        }
        session.rawSetBlock(blockPos.add(pos), b);
    }

    @Override
    public void copy(EditSession editSession) {
        parent.copy(editSession);
    }

    @Override
    public void copy(EditSession editSession, Region region) {
        parent.copy(editSession, region); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flip(FlipDirection dir) {
        flip(dir,false); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flip(FlipDirection dir, boolean aroundPlayer) {
        parent.flip(dir, aroundPlayer); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BaseBlock getBlock(Vector pos) throws ArrayIndexOutOfBoundsException {
        return parent.getBlock(pos); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Countable<Integer>> getBlockDistribution() {
        return parent.getBlockDistribution(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Countable<BaseBlock>> getBlockDistributionWithData() {
        return parent.getBlockDistributionWithData(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int getHeight() {
        return parent.getHeight(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLength() {
        return parent.getLength(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getWidth() {
        return parent.getWidth(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getSize() {
        return parent.getSize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public BaseBlock getPoint(Vector pos) throws ArrayIndexOutOfBoundsException {
        return parent.getPoint(pos); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Vector getOffset() {
        return parent.getOffset(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getOrigin() {
        return parent.getOrigin(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalEntity[] pasteEntities(Vector pos) {
        return parent.pasteEntities(pos); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rotate2D(int angle) {
        parent.rotate2D(angle); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveSchematic(File path) throws IOException, com.sk89q.worldedit.world.DataException {
        parent.saveSchematic(path); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBlock(Vector pt, BaseBlock block) {
        parent.setBlock(pt, block); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOffset(Vector offset) {
        parent.setOffset(offset); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOrigin(Vector origin) {
        parent.setOrigin(origin); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void storeEntity(LocalEntity entity) {
        parent.storeEntity(entity); //To change body of generated methods, choose Tools | Templates.
    }

    public void setBuildMode(Comparator<StructureBlock> buildMode) {
        this.buildMode = buildMode;
    }

    public Comparator<StructureBlock> getBuildMode() {
        return buildMode;
    }
        
  
}