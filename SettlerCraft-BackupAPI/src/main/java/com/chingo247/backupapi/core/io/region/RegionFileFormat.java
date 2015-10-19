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
package com.chingo247.backupapi.core.io.region;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;

/**
 *
 * @author Chingo
 */
public class RegionFileFormat {
    
    public static final int CHUNK_SIZE = 16;
    public static final int REGION_SIZE = 32 * 16;
    public static final int SECTION_HEIGHT = 16;
    public static final String REGION_FILE_EXTENSION = ".mca";
    
    private File file;
    private int regionX;
    private int regionZ;

    public RegionFileFormat(File regionFile) {
        this.file = regionFile;
        
        Vector2D v = parsePosition(regionFile);
        this.regionX = v.getBlockX();
        this.regionZ = v.getBlockZ();
        
    }
    
    private Vector2D parsePosition(File regionFile) {
        String regionName = regionFile.getName();
        String xString = regionName.split("\\.")[1];
        String zString = regionName.split("\\.")[2];
        return new BlockVector2D(Integer.parseInt(xString), Integer.parseInt(zString));
    }

    public File getFile() {
        return file;
    }

    public RegionFile read() {
        return new RegionFile(file);
    }
    
    public boolean contains(int x, int z) {
        CuboidRegion region = new CuboidRegion(new Vector(getX(), 0, getZ()),new Vector(getX() + REGION_SIZE, 0, getZ() + REGION_SIZE));
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
//        System.out.println("[RegionFileFormat]: Contains x:" + x + ", z:" + z);
//        System.out.println("[RegionFileFormat]: " + min + ", " + max);
//        System.out.println("[RegionFileFormat]: True ? " + (x < max.getBlockX() && x >= min.getBlockX() && z >= min.getBlockZ() && z < max.getBlockZ()));

        return x < max.getBlockX() && x >= min.getBlockX() && z >= min.getBlockZ() && z < max.getBlockZ();
    }

    /**
     * Returns the regions x in region coordinate format. This x is divided by 512
     * @return The x
     */
    public int getRegionX() {
        return regionX;
    }

    /**
     * Returns the regions z in region coordinate format. This z is divided by 512
     * @return The z
     */
    public int getRegionZ() {
        return regionZ;
    }
    
    public int getX() {
        return regionX * REGION_SIZE;
    }
    
    public int getZ() {
        return regionZ * REGION_SIZE;
    }

  

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.regionX;
        hash = 67 * hash + this.regionZ;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegionFileFormat other = (RegionFileFormat) obj;
        if (this.regionX != other.regionX) {
            return false;
        }
        if (this.regionZ != other.regionZ) {
            return false;
        }
        return true;
    }
    
    
    
    
}
