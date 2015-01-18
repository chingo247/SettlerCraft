
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
package com.chingo247.structureapi.construction.worldedit;

import com.google.common.base.Preconditions;
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
import java.util.List;

/**
 *
 * @author Chingo
 */
public class SmartClipboard extends CuboidClipboard {

    private final CuboidClipboard parent;

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

   
        
  
}