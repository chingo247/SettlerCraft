/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.core.snapshot;

import com.chingo247.backupapi.core.snapshot.WorldPartSnapshot;
import com.chingo247.structureapi.construction.backup.IWorldPartSnapshot;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Chingo
 */
public class SnapshotReader {
    
    public IWorldPartSnapshot read(File snapshotFile) throws IOException {
        WorldPartSnapshot worldSnapshot = null;
        try(NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(snapshotFile)))) {
            
            NamedTag root = nbtStream.readNamedTag();
            
            Map<String,Tag> snapshotMap = (Map) root.getTag().getValue();
            
            int minX = getChildTag(snapshotMap, "MinX", IntTag.class).getValue();
            int minZ = getChildTag(snapshotMap, "MinZ", IntTag.class).getValue();
            int maxX = getChildTag(snapshotMap, "MaxX", IntTag.class).getValue();
            int maxZ = getChildTag(snapshotMap, "MaxZ", IntTag.class).getValue();
            
            worldSnapshot = new WorldPartSnapshot(snapshotMap, minX, minZ, maxX, maxZ);
            
            Vector min = new Vector(-30, 63, 130);
            Vector max = new Vector(-7, 76, 153);
            
            for(int x = min.getBlockX(); x < max.getBlockX(); x++) {
                for(int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    for(int y = min.getBlockY(); y < max.getBlockY(); y++) {
                        worldSnapshot.getWorldBlockAt(x, y, z);
                    }
                }
            }
            
            
        }
        
        
        
        return worldSnapshot;
    }
    
    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the
     * expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key,
            Class<T> expected) {

        if (!items.containsKey(key)) {
            throw new RuntimeException("Snapshot file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new RuntimeException(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
    
    public static void main(String[] args) {
        SnapshotReader reader = new SnapshotReader();
        try {
            reader.read(new File("F:\\GAMES\\MineCraftServers\\bukkit\\1.8\\Bukkit 1.8-SettlerCraft-2.1.0\\plugins\\SettlerCraft-StructureAPI\\worlds\\world\\structures\\26\\backups\\restore.snapshot"));
        } catch (IOException ex) {
            Logger.getLogger(SnapshotReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
