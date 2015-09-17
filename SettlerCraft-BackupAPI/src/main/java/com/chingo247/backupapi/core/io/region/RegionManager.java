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

import com.chingo247.backupapi.core.io.nbt.NBTChunk;
import com.chingo247.backupapi.core.io.nbt.NBTLevel;
import com.chingo247.backupapi.core.io.nbt.NBTSection;
import com.chingo247.backupapi.core.io.nbt.TagNotFoundException;
import com.chingo247.backupapi.core.util.PositionUtils;
import com.chingo247.xplatform.core.APlatform;
import com.google.common.collect.Maps;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.FileUtils;
import scala.reflect.api.Positions;

/**
 *
 * @author Chingo
 */
public class RegionManager {

    private APlatform platform;

    public RegionManager(APlatform platform) {
        this.platform = platform;
    }
    
    private static void print(String s) {
//        System.out.println("[RegionManager]: " + s);
    }

    public void copy(String world, CuboidRegion region, File copyTo) throws IOException, Exception {
//        File regionsDirectory = platform.getServer().getWorldRegionFolder(world);
        File regionsDirectory = new File("F:\\GAMES\\MineCraftServers\\bukkit\\1.8\\Bukkit 1.8-SettlerCraft-2.1.0\\world\\region");

        Set<RegionFileFormat> rffs = getRegionFiles(regionsDirectory, region);
        HashMap<String, Tag> chunkTagMap = Maps.newHashMap();

        Set<Vector2D> chunks = region.getChunks();
        for (RegionFileFormat rff : rffs) {
            setTags(rff, region, chunks, chunkTagMap);
        }

        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();

        Vector2D minPos = PositionUtils.getChunkPosition(min.getBlockX(), min.getBlockZ());
        Vector2D maxPos = PositionUtils.getChunkPosition(max.getBlockX(), max.getBlockZ());

        chunkTagMap.put("MinX", new IntTag(minPos.getBlockX()));
        chunkTagMap.put("MinZ", new IntTag(minPos.getBlockZ()));
        chunkTagMap.put("MaxX", new IntTag(maxPos.getBlockX()));
        chunkTagMap.put("MaxZ", new IntTag(maxPos.getBlockZ()));

        CompoundTag root = new CompoundTag(chunkTagMap);

        try (NBTOutputStream outputStream = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(copyTo)))) {
            outputStream.writeNamedTag("Snapshot", root);
        }
    }

    private void setTags(RegionFileFormat rff, CuboidRegion region, Set<Vector2D> chunks, Map<String, Tag> chunkTags) throws IOException, Exception {
        try (RegionFile rf = rff.read()) {
            for (Iterator<Vector2D> chunkIt = chunks.iterator(); chunkIt.hasNext();) {
                Vector2D v = chunkIt.next();
                
                int xPos = v.getBlockX() * RegionFileFormat.CHUNK_SIZE;
                int zPos = v.getBlockZ() * RegionFileFormat.CHUNK_SIZE;
                Vector2D regionPos = PositionUtils.getRegionPosition(xPos, zPos);
                
                if(!(regionPos.getBlockX() == rff.getX() && regionPos.getBlockZ() == rff.getZ())) {
                    continue;
                }
 
                xPos = (xPos - regionPos.getBlockX()) >> 4;
                zPos = (zPos - regionPos.getBlockZ()) >> 4;
                
                
                if (rf.hasChunk(xPos, zPos)) {
                    // It's in here
                    chunkIt.remove();

                // Chunk
                    //  - Level
                    //      - Sections
                    //      - Entities
                    //      - TileEntities
                    Tag t;
                    try (NBTInputStream inputStream = new NBTInputStream(rf.getChunkDataInputStream(xPos, zPos))) {
                        NamedTag nt = inputStream.readNamedTag();
                        if (nt == null) {
                            throw new TagNotFoundException("Couldn't find chunk tag!");
                        }
                        t = nt.getTag();

                        NBTChunk nbtc = new NBTChunk(new Vector2D(xPos, zPos), new Vector2D(v.getBlockX() * RegionFileFormat.CHUNK_SIZE, v.getBlockZ() * RegionFileFormat.CHUNK_SIZE), t);
                        NBTLevel nbtl = nbtc.getLevelTag();

                        HashMap<String, Tag> chunkDataMap = new HashMap<>();

                        int minY = region.getMinimumY() >> 4;
                        int maxY = region.getMaximumY() >> 4;

                        for (NBTSection section : nbtl.getSections(minY, maxY)) {
                            chunkDataMap.put("Section-[" + section.getY() + "]", section.getSectionTag());
                        }

                        ListTag tileEntities = nbtl.getTileEntityData();
                        if (tileEntities != null) {
                            chunkDataMap.put("TileEntities", tileEntities);
                        }

                        ListTag entities = nbtl.getEntityData();
                        if (entities != null) {
                            chunkDataMap.put("Entities", entities);
                        }

                        NamedTag sectionsCopyTag = new NamedTag("Sections", new CompoundTag(chunkDataMap));
                        NamedTag chunkCopyTag = new NamedTag("Chunk", sectionsCopyTag.getTag());
                        chunkTags.put("Chunk-[" + v.getBlockX() + "," + v.getBlockZ() + "]", chunkCopyTag.getTag());

                    }
                } else {
//                System.out.println(" ");
//                System.out.println("[RegionManager]: Doesn't have chunk " + xPos + ", " + zPos);
//                System.out.println("[RegionManager]: Chunk at " + (rff.getX() + (xPos * RegionFileFormat.CHUNK_SIZE)) + ", " + (rff.getZ() + (zPos * RegionFileFormat.CHUNK_SIZE)));
//                System.out.println(" ");
                }
            }
        }
    }

    public List<RegionFileFormat> listRegionFiles(String world) {
        return listRegionFiles(platform.getServer().getWorldRegionFolder(world));
    }

    public List<RegionFileFormat> listRegionFiles(File regionDirectory) {
        Iterator<File> fileIt = FileUtils.iterateFiles(regionDirectory, new String[]{"mca"}, false);
        List<RegionFileFormat> regionFiles = new ArrayList<>();

        while (fileIt.hasNext()) {
            File f = fileIt.next();

            regionFiles.add(new RegionFileFormat(f));
        }

        return regionFiles;
    }

    public RegionFileFormat getRegionFile(String world, int x, int z) throws FileNotFoundException {
        return getRegionFile(platform.getServer().getWorldRegionFolder(world), x, z);
    }

    public RegionFileFormat getRegionFile(File regionDirectory, int x, int z) throws FileNotFoundException {
        Vector2D regionPos = PositionUtils.getRegionCoordinate(x, z);
        int rX = regionPos.getBlockX();
        int rZ = regionPos.getBlockZ();
        File regionFile = new File(regionDirectory, "r." + rX + "." + rZ + RegionFileFormat.REGION_FILE_EXTENSION);

        if (!regionFile.exists()) {
            throw new FileNotFoundException("File '" + regionFile.getAbsolutePath() + "' doesn't exist!");
        }

        return new RegionFileFormat(regionFile);
    }

    public Set<RegionFileFormat> getRegionFiles(String world, CuboidRegion region) throws FileNotFoundException {
        return getRegionFiles(platform.getServer().getWorldRegionFolder(world), region);
    }

    public Set<RegionFileFormat> getRegionFiles(File regionDirectory, CuboidRegion region) throws FileNotFoundException {
        Vector min = region.getMinimumPoint();
        Vector2D regionPos = PositionUtils.getRegionPosition(min.getBlockX(), min.getBlockZ());
        int regionX = regionPos.getBlockX();
        int regionZ = regionPos.getBlockZ();
        Set<RegionFileFormat> regionFiles = new HashSet<>();

        for (int x = regionX; x <= region.getMaximumPoint().getBlockX(); x += RegionFileFormat.REGION_SIZE) {
            for (int z = regionZ; z <= region.getMaximumPoint().getBlockZ(); z += RegionFileFormat.REGION_SIZE) {
                regionFiles.add(getRegionFile(regionDirectory, x, z));
            }
        }

        return regionFiles;
    }

    public static void main(String[] args) {

        RegionManager regionManager = new RegionManager(null);

        Vector min = new Vector(1.0, 63.0, 511);
        Vector max = new Vector(107.0, 123.0, 722.0);

        try {
            regionManager.copy("world", new CuboidRegion(min, max), new File("test.snapshot"));
        } catch (Exception ex) {
            Logger.getLogger(RegionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
