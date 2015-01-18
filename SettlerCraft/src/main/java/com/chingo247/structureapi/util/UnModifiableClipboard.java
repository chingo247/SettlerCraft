
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
package com.chingo247.structureapi.util;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalEntity;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.world.DataException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A Clipboard that guarantees no modification, any method that modifies the clipboard will cause an exception
 * @author Chingo
 */
public final class UnModifiableClipboard extends CuboidClipboard {
    
    private final CuboidClipboard clipboard;

    public UnModifiableClipboard(CuboidClipboard clipboard) {
        super(Vector.ZERO);
        this.clipboard = clipboard;
    }

    @Override
    public BaseBlock getBlock(Vector position) throws ArrayIndexOutOfBoundsException {
        return clipboard.getBlock(position);
    }

    @Override
    public List<Countable<Integer>> getBlockDistribution() {
        return clipboard.getBlockDistribution();
    }

    @Override
    public int getHeight() {
        return clipboard.getHeight(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int getLength() {
        return clipboard.getLength(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getWidth() {
        return clipboard.getWidth(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getSize() {
        return clipboard.getSize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getOffset() {
        return clipboard.getOffset(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getOrigin() {
        return clipboard.getOrigin(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BaseBlock getPoint(Vector position) throws ArrayIndexOutOfBoundsException {
        return clipboard.getPoint(position); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copy(EditSession editSession) {
        clipboard.copy(editSession); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir) throws MaxChangedBlocksException {
        clipboard.paste(editSession, newOrigin, noAir); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir, boolean entities) throws MaxChangedBlocksException {
        clipboard.paste(editSession, newOrigin, noAir, entities); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalEntity[] pasteEntities(Vector newOrigin) {
        return clipboard.pasteEntities(newOrigin); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void place(EditSession editSession, Vector newOrigin, boolean noAir) throws MaxChangedBlocksException {
        clipboard.place(editSession, newOrigin, noAir); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveSchematic(File path) throws IOException, DataException {
        clipboard.saveSchematic(path); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copy(EditSession editSession, Region region) {
        clipboard.copy(editSession, region); //To change body of generated methods, choose Tools | Templates.
    }
    
     

    @Override
    public List<Countable<BaseBlock>> getBlockDistributionWithData() {
        return clipboard.getBlockDistributionWithData(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flip(FlipDirection dir) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void flip(FlipDirection dir, boolean aroundPlayer) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void rotate2D(int angle) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setBlock(Vector position, BaseBlock block) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setOffset(Vector offset) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setOrigin(Vector origin) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void storeEntity(LocalEntity entity) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    
    
}
