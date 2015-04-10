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
package com.chingo247.structureapi.structure;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.structureapi.structure.construction.options.Options;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.sk89q.worldedit.EditSession;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {
    
    private final KeyPool<Long> pool;
    private static ConstructionManager instance;
    private final StructureAPI structureAPI = StructureAPI.getInstance();
    
    static ConstructionManager getInstance() {
        if(instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
    }

    private ConstructionManager() {
        ExecutorService service = SettlerCraft.getInstance().getExecutor();
        this.pool = new KeyPool<>(service);
    }
    
    public void build(final Structure structure, final UUID player, final EditSession session, final Options options, boolean force) {
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                AsyncPlacement placement = new AsyncPlacement(playerEntry, structure.getPlan().getPlacement());
                placement.rotate(structure.getDirection());
                placement.place(session, structure.getDimension().getMinPosition(), options);
            }
        });
    }
    
    public void demolish(final Structure structure, final UUID player, final EditSession session, final Options options, boolean force) {
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                AsyncPlacement placement = new AsyncPlacement(playerEntry, structure.getPlan().getPlacement());
                placement.rotate(structure.getDirection());
                
                // Set Negative MASK
                // Set Negative not natural MASK
                
                placement.place(session, structure.getDimension().getMinPosition(), options);throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
    public void stop(final Structure structure, boolean force) {
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
    
    
}
