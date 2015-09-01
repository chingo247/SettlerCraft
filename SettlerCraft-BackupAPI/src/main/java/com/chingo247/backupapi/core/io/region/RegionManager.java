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

/**
 *
 * @author Chingo
 */
public class RegionManager {

    private APlatform platform;

    public RegionManager(APlatform platform) {
        this.platform = platform;
    }

    public void copy(String world, CuboidRegion region, File copyTo) throws IOException, Exception {
//        File regionsDirectory = platform.getServer().getWorldRegionFolder(world);
        File regionsDirectory = new File("F:\\GAMES\\MineCraftServers\\bukkit\\1.8\\Bukkit 1.8-SettlerCraft-2.1.0\\world\\region");

        System.out.println("[RegionManager]: Searching regions in  " + regionsDirectory.getAbsolutePath());

        Set<RegionFileFormat> rffs = getRegionFiles(regionsDirectory, region);
        HashMap<String, Tag> chunkTagMap = Maps.newHashMap();

        System.out.println("[RegionManager]: Found " + rffs.size() + " regions");
        
        Set<Vector2D> chunks = region.getChunks();
        for (RegionFileFormat rff : rffs) {
            setTags(rff, region, chunks, chunkTagMap);
        }

        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();

        System.out.println("[RegionManager]: World positions " + min + ", " + max);

        Vector2D minPos = PositionUtils.getChunkPosition(min.getBlockX(), min.getBlockZ());
        Vector2D maxPos = PositionUtils.getChunkPosition(max.getBlockX(), max.getBlockZ());

        System.out.println("[RegionManager]: ChunkPositions " + minPos + ", " + maxPos);

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
        
        RegionFile rf = rff.read();

        System.out.println("[RegionManager]: Saving " + chunks.size() + " chunks...");

        for (Iterator<Vector2D> chunkIt = chunks.iterator(); chunkIt.hasNext();) {
            Vector2D v = chunkIt.next();
            int xPos = v.getBlockX() * RegionFileFormat.CHUNK_SIZE;
            int zPos = v.getBlockZ() * RegionFileFormat.CHUNK_SIZE;
            Vector2D regionPos = PositionUtils.getRegionPosition(xPos, zPos);

            xPos = (xPos - regionPos.getBlockX()) / RegionFileFormat.CHUNK_SIZE;
            zPos = (zPos - regionPos.getBlockZ()) / RegionFileFormat.CHUNK_SIZE;

            if (rf.hasChunk(xPos, zPos)) {
                // It's in here
                chunkIt.remove();

                // Chunk
                //  - Level
                //      - Sections
                //      - Entities
                //      - TileEntities
//                System.out.println("[RegionManager]: Saving chunk(" + xPos + ", " + zPos + ")");

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

//                    System.out.println("[RegionManager] Between " + minY + ", " + maxY);
                    
                    for (NBTSection section : nbtl.getSections(minY, maxY)) {
//                        System.out.println("[RegionManager]: writing section " + section.getY());
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
        Vector min = new Vector(-321, 71, 2978);
        Vector max = new Vector(-133, 105, 3133);
        RegionManager regionManager = new RegionManager(null);
        try {
            regionManager.copy("world", new CuboidRegion(min, new Vector(max)), new File("test.snapshot"));
        } catch (Exception ex) {
            Logger.getLogger(RegionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
