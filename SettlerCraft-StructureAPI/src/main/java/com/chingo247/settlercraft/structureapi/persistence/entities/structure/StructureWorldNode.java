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
package com.chingo247.settlercraft.structureapi.persistence.entities.structure;

import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Chingo
 */
public class StructureWorldNode extends WorldNode {

    public StructureWorldNode(Node worldNode) {
        super(worldNode);
    }
    
    public StructureWorldNode(WorldNode worldNode) {
        super(worldNode.getRawNode());
    }
    
    public void addStructure(StructureNode structureNode) {
        structureNode.getRawNode().createRelationshipTo(getRawNode(), DynamicRelationshipType.withName(StructureRelTypes.RELATION_WITHIN));
    }
    
    public void removeStructure(long id) {
        Node rawNode = getRawNode();
        for(Relationship rel : rawNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_WITHIN))) {
            StructureNode ownerNode = new StructureNode(rel.getOtherNode(getRawNode()));
            if(ownerNode.getId() == id) {
                rel.delete();
                break;
            }
        }
    }
    
}
