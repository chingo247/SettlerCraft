/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.world;

import com.chingo247.settlercraft.core.model.WorldRepository;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Chingo
 */
public class StructureWorldRepository extends WorldRepository implements IStructureWorldRepository {

    public StructureWorldRepository(GraphDatabaseService graph) {
        super(graph);
    }

    @Override
    public StructureWorldNode findByUUID(UUID worldUUID) {
        IWorld world = super.findByUUID(worldUUID); //To change body of generated methods, choose Tools | Templates.
        if(world != null) {
            return new StructureWorldNode(world.getNode());
        }
        return null;
    }

    @Override
    public StructureWorldNode registerWorld(String worldName, UUID worldUUID) {
        IWorld world = super.registerWorld(worldName, worldUUID); //To change body of generated methods, choose Tools | Templates.
        return new StructureWorldNode(world.getNode());
    }
    
    
    
}
