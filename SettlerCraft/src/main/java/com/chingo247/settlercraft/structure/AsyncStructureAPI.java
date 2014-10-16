/*
 * Copyright (C) 2014 Chingo247
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


package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.world.Direction;
import com.chingo247.settlercraft.structure.plan.SettlerCraftPlan;
import com.sk89q.worldedit.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class AsyncStructureAPI {
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private static AsyncStructureAPI instance;
    
    public static AsyncStructureAPI getInstance() {
        if(instance == null) {
            instance = new AsyncStructureAPI();
        }
        return instance;
    }
    
    private AsyncStructureAPI() {}
    
    public void create(final Player player, final SettlerCraftPlan plan, final World world, final Vector location, final Direction direction, final StructureCallback callback) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                Structure structure = Structure.create(player, plan, world, location, direction);
                callback.onComplete(structure);
            }
        });
    }
    
    public void create(final SettlerCraftPlan plan, final World world, final Vector location, final Direction direction, final StructureCallback callback) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                Structure structure = Structure.create(plan, world, location, direction);
                callback.onComplete(structure);
            }
        });
    }

    public void shutdown() {
        if(!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    public interface StructureCallback {
        
        void onComplete(Structure structure);
        
    }
    
    
    
}
