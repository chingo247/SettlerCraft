/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
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

package com.sc.api.structure.construction.builder.async;

import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalEntity;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.Region;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.primesoft.asyncworldedit.PluginMain;

/**
 *
 * @author Chingo
 */
public class SCProxyCuboidClipBoard extends CuboidClipboard{
    
    /**
     * The parrent clipboard
     */
    protected final SCLayeredCuboidClipBoard m_parrent;

    public SCProxyCuboidClipBoard(CuboidClipboard parrent) {
        super(parrent.getSize(), parrent.getOrigin(), parrent.getOffset());

        m_parrent = new SCLayeredCuboidClipBoard(parrent);
    }

    @Override
    public void copy(EditSession editSession) {
        synchronized (m_parrent) {
            m_parrent.copy(editSession);
        }
        updateProps();
    }

    @Override
    public void copy(EditSession editSession, Region region) {
        synchronized (m_parrent) {
            super.copy(editSession, region);
        }
        updateProps();        
    }

    @Override
    public void flip(CuboidClipboard.FlipDirection dir) {
        synchronized (m_parrent) {
            m_parrent.flip(dir);
        }
        updateProps();
    }

    @Override
    public void flip(CuboidClipboard.FlipDirection dir, boolean aroundPlayer) {
        synchronized (m_parrent) {
            m_parrent.flip(dir, aroundPlayer);
        }
        updateProps();
    }

    @Override
    public List<Countable<Integer>> getBlockDistribution() {
        synchronized (m_parrent) {
            return m_parrent.getBlockDistribution();
        }
    }

    @Override
    public List<Countable<BaseBlock>> getBlockDistributionWithData() {
        synchronized (m_parrent) {
            return m_parrent.getBlockDistributionWithData();
        }
    }

    @Override
    public int getHeight() {
        synchronized (m_parrent) {
            return m_parrent.getHeight();
        }
    }

    @Override
    public int getLength() {
        synchronized (m_parrent) {
            return m_parrent.getLength();
        }
    }

    @Override
    public Vector getOffset() {
        synchronized (m_parrent) {
            return m_parrent.getOffset();
        }
    }

    @Override
    public Vector getOrigin() {
        synchronized (m_parrent) {
            return m_parrent.getOrigin();
        }
    }

    @Override
    public BaseBlock getPoint(Vector pos)
            throws ArrayIndexOutOfBoundsException {
        synchronized (m_parrent) {
            return m_parrent.getPoint(pos);
        }
    }

    @Override
    public Vector getSize() {
        synchronized (m_parrent) {
            return m_parrent.getSize();
        }
    }

    @Override
    public int getWidth() {
        synchronized (m_parrent) {
            return m_parrent.getWidth();
        }
    }

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir)
            throws MaxChangedBlocksException {
        synchronized (m_parrent) {
            m_parrent.paste(editSession, newOrigin, noAir);
        }
        updateProps();
    }

    @Override
    public LocalEntity[] pasteEntities(Vector pos) {
        synchronized (m_parrent) {
            return m_parrent.pasteEntities(pos);
        }
    }

    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir)
            throws MaxChangedBlocksException {
        synchronized (m_parrent) {
            m_parrent.place(editSession, pos, noAir);
        }
        updateProps();
    }

    @Override
    public void rotate2D(int angle) {
        synchronized (m_parrent) {
            m_parrent.rotate2D(angle);
        }
        updateProps();
    }

    @Override
    public void saveSchematic(File path)
            throws IOException, DataException {
        synchronized (m_parrent) {
            m_parrent.saveSchematic(path);
        }
    }

    @Override
    public void setBlock(Vector pt, BaseBlock block) {
        synchronized (m_parrent) {
            m_parrent.setBlock(pt, block);
        }
    }

    @Override
    public void setOffset(Vector offset) {
        synchronized (m_parrent) {
            m_parrent.setOffset(offset);
            
            super.setOffset(offset);
        }
    }

    @Override
    public void setOrigin(Vector origin) {
        synchronized (m_parrent) {
            m_parrent.setOrigin(origin);
            
            super.setOrigin(origin);
        }
    }

    @Override
    public void storeEntity(LocalEntity entity) {
        synchronized (m_parrent) {
            m_parrent.storeEntity(entity);
        }
    }    

    /**
     * Inject a LocalSession wrapper factory using reflection
     */
    public void setSize(Vector size) {
        try {
            Field field = CuboidClipboard.class.getDeclaredField("size");
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            
            field.set(this, size);
        } catch (IllegalArgumentException ex) {
            PluginMain.log("Unable to set clipboard size: unsupported WorldEdit version.");
        } catch (IllegalAccessException ex) {
            PluginMain.log("Unable to set clipboard size: security exception.");
        } catch (NoSuchFieldException ex) {
            PluginMain.log("Unable to set clipboard size: unsupported WorldEdit version.");
        } catch (SecurityException ex) {
            PluginMain.log("Unable to set clipboard size: security exception.");
        }
    }
    
    
    /** 
     * Update all properties based on the parrent
     */
    protected void updateProps() {
        synchronized (m_parrent) {
            setOffset(m_parrent.getOffset());
            setOrigin(m_parrent.getOrigin());
            setSize(m_parrent.getSize());
        }
    }
    
}
