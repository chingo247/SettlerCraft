/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram;

import com.chingo247.settlercraft.structureapi.persistence.entities.features.LocationFeatureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Chingo
 */
public class StructureHologramNode extends LocationFeatureNode {
    
    public static final Label LABEL = DynamicLabel.label("StructureHologram");
    public static final RelationshipType RELATION_HAS_HOLOGRAM = DynamicRelationshipType.withName("hasHologram");

    public StructureHologramNode(Node underlyingNode) {
        super(underlyingNode);
    }
    
    public StructureNode getStructure() {
        Node n = getRawNode();
        Relationship r = n.getSingleRelationship(RELATION_HAS_HOLOGRAM, Direction.INCOMING);
        if(r == null) {
            return null;
        }
        Node other = r.getOtherNode(n);
        return other != null ? new StructureNode(other) : null; // Shouldn't be null...
    }
    
}
