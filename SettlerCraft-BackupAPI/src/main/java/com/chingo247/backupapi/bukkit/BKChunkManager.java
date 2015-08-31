/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.bukkit;

import com.chingo247.structureapi.construction.backup.IChunkManager;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

/**
 *
 * @author Chingo
 */
public class BKChunkManager implements IChunkManager {

    @Override
    public void save(com.sk89q.worldedit.world.World world, Set<Vector2D> chunkPositions) {
        World bukkitWorld = Bukkit.getWorld(world.getName());
        WorldServer handle = ((CraftWorld) bukkitWorld).getHandle();

        for (Iterator<Vector2D> iterator = chunkPositions.iterator(); iterator.hasNext();) {
            Vector2D next = iterator.next();
            Chunk c = handle.chunkProviderServer.getChunkAt(next.getBlockX(), next.getBlockZ());
            handle.chunkProviderServer.saveChunkNOP(c);

            if (c.a(true)) {
                handle.chunkProviderServer.saveChunk(c);
                c.f(false);
            }
        }

    }

    @Override
    public void save(com.sk89q.worldedit.world.World w, CuboidRegion region) {
        save(w, region.getChunks());
    }

    @Override
    public void save(com.sk89q.worldedit.world.World world, int x, int z) {
        World bukkitWorld = Bukkit.getWorld(world.getName());
        WorldServer handle = ((CraftWorld) bukkitWorld).getHandle();
        Chunk c = handle.chunkProviderServer.getChunkAt(x, z);
        handle.chunkProviderServer.saveChunkNOP(c);

        if (c.a(true)) {
            handle.chunkProviderServer.saveChunk(c);
            c.f(false);
        }
    }

    @Override
    public void load(com.sk89q.worldedit.world.World world, int x, int z) {
        Bukkit.getWorld(world.getName()).loadChunk(x, z);
    }

    @Override
    public boolean isLoaded(com.sk89q.worldedit.world.World world, int x, int z) {
        org.bukkit.Chunk c = Bukkit.getWorld(world.getName()).getChunkAt(x, z);
        return c.isLoaded();
    }
    

}
