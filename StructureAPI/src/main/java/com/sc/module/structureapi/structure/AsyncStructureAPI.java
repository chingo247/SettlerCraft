/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure;

import com.sc.module.structureapi.structure.plan.StructurePlan;
import com.sc.module.structureapi.world.Cardinal;
import com.sk89q.worldedit.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class AsyncStructureAPI {
    
    private final Executor executor = Executors.newCachedThreadPool();
    private static AsyncStructureAPI instance;
    
    public static AsyncStructureAPI getInstance() {
        if(instance == null) {
            instance = new AsyncStructureAPI();
        }
        return instance;
    }
    
    private AsyncStructureAPI() {}
    
    public void create(final Player player, final StructurePlan plan, final World world, final Vector location, final Cardinal cardinal, final StructureCallback callback) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                Structure structure = StructureAPI.create(player, plan, world, location, cardinal);
                callback.onComplete(structure);
            }
        });
    }
    
    public interface StructureCallback {
        
        void onComplete(Structure structure);
        
    }
    
    
    
}
