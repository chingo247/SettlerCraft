/*
 * Copyright (C) 2015 Chingo
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
