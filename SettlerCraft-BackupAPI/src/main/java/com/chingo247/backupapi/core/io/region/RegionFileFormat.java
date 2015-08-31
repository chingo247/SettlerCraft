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

    RegionFileFormat(File regionFile) {
        this.file = regionFile;
        
        System.out.println("[RegionFileFormat]: Region file: " + regionFile.getName());
        
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
