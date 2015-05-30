/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.persistence.entities.structure;

import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class StructureOwnerNode extends SettlerNode {

    private StructureOwnerType type;
    
    StructureOwnerNode(Node node, StructureOwnerType type) {
        super(node);
        this.type = type;
    }

    public StructureOwnerType getType() {
        return type;
    }
    
    
    
    
    
}
