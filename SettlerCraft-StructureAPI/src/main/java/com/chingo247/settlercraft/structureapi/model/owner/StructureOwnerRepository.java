/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.model.owner;

import com.chingo247.settlercraft.core.model.BaseSettlerRepository;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwnerRepository;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Defines methods to find nodes that represent a StructureOwner. All methods in this class require an active Transaction.
 * @author Chingo
 */
public class StructureOwnerRepository extends BaseSettlerRepository implements IStructureOwnerRepository {


    public StructureOwnerRepository(GraphDatabaseService graph) {
        super(graph);
    }

    @Override
    public StructureOwnerNode findByUUID(UUID uuid) {
        IBaseSettler settler = super.findByUUID(uuid);
        if(settler != null) {
            return new StructureOwnerNode(settler.getNode());
        }
        return null;
    }

    @Override
    public StructureOwnerNode findById(Long id) {
        IBaseSettler settler = super.findById(id);
        if(settler != null) {
            return new StructureOwnerNode(settler.getNode());
        }
        return null;
    }
    
    
   
    
}
