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
package com.chingo247.backupapi.bukkit;

import com.chingo247.backupapi.core.IChunkLoader;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class BKChunkLoader implements IChunkLoader {

    private final WorldServer server;
    private final World w;

    public BKChunkLoader(World w) {
        this.w = w;
        this.server = ((CraftWorld)w).getHandle();
    }

    @Override
    public void load(final int x, final int z) {
        w.loadChunk(x, z, true);
        Chunk c = server.chunkProviderServer.loadChunk(x, z);
        if(c == null) {
            c = server.chunkProviderServer.getOrCreateChunk(x, z);
        }
        c.mustSave = true;
        server.chunkProviderServer.saveChunk(c);
        server.chunkProviderServer.saveChunkNOP(c);
    }

    @Override
    public void unload(final int x, final int z) {
        w.unloadChunk(x, z);
    }

    @Override
    public boolean isLoaded(int x, int z) {
        return this.w.isChunkLoaded(x, z);
    }

}
