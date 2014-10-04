/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.structureapi.structure;

import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.entities.world.Direction;
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
    
    public void create(final Player player, final StructurePlan plan, final World world, final Vector location, final Direction direction, final StructureCallback callback) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                Structure structure = Structure.create(player, plan, world, location, direction);
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
