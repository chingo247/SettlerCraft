/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.world;

import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.structureapi.model.RelTypes;
import com.chingo247.structureapi.model.zone.ConstructionZone;
import com.chingo247.structureapi.model.zone.IConstructionZone;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author Chingo
 */
public class StructureWorld extends WorldNode implements IStructureWorld {

    public StructureWorld(Node worldNode) {
        super(worldNode);
    }

    @Override
    public void addStructure(StructureNode structure) {
        structure.getNode().createRelationshipTo(getNode(), RelTypes.WITHIN);
    }
    
    
    @Override
    public boolean deleteStructure(long id) {
        Node rawNode = getNode();
        for (Relationship rel : rawNode.getRelationships(RelTypes.WITHIN)) {
            StructureNode otherNode = new StructureNode(rel.getOtherNode(getNode()));
            if (otherNode.getNode().hasLabel(StructureNode.label()) && otherNode.getId() == id) {
                rel.delete();
                return true;
            }
        }
        return false;
    }
    
    
    @Override
    public void addZone(IConstructionZone zone) {
        zone.getNode().createRelationshipTo(getNode(), RelTypes.WITHIN);
    }
    

    @Override
    public boolean deleteZone(long id) {
        Node rawNode = getNode();
        for (Relationship rel : rawNode.getRelationships(RelTypes.WITHIN)) {
            ConstructionZone otherNode = new ConstructionZone(rel.getOtherNode(getNode()));
            if (otherNode.getNode().hasLabel(ConstructionZone.label()) && otherNode.getId() == id) {
                rel.delete();
                return true;
            }
        }
        return false;
    }

}
