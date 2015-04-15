/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi.persistence.repository;

import com.chingo247.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldRepository;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.structure.State;
import com.chingo247.structureapi.world.Direction;
import com.sk89q.worldedit.Vector;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.com.google.common.collect.Lists;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Chingo
 */
public class StructureNode {

    public static final Label LABEL = DynamicLabel.label("Structure");
    public static final String NAME_PROPERTY = "name";
    public static final String DIRECTION_PROPERTY = "direction";
    public static final String WORLD_PROPERTY = "world";
    public static final String WORLD_ID_PROPERTY = "worldId";
    public static final String CONSTRUCTION_STATUS_PROPERTY = "constructionStatus";
    public static final String STATE_PROPERTY = "state";
    public static final String MIN_X_PROPERTY = "minX", MIN_Y_PROPERTY = "minY", MIN_Z_PROPERTY = "minZ", MAX_X_PROPERTY = "maxX", MAX_Y_PROPERTY = "maxY", MAX_Z_PROPERTY = "MAX_Z";
    public static final String CREATED_AT_PROPERTY = "createdAt", DELETED_AT_PROPERTY = "deletedAt";

    // RelationShips
    public static final String RELATION_SUBSTRUCTURE = "Substructure";
    public static final String RELATION_OWNED_BY = "OwnedBy";

    private final Node underlyingNode;

    protected StructureNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Long getId() {
        return underlyingNode.getId();
    }
    
    /**
     * Gets the actual node 
     * @return The actual Node
     */
    public Node getRawNode() {
        return underlyingNode;
    }
   

    public String getName() {
        return (String) underlyingNode.getProperty(NAME_PROPERTY);
    }

    public void setName(String name) {
        underlyingNode.setProperty(NAME_PROPERTY, name);
    }

    public State getState() {
        return State.match((int) underlyingNode.getProperty(STATE_PROPERTY));
    }

    public void setState(State state) {
        underlyingNode.setProperty(STATE_PROPERTY, state);
        if (state == State.DELETED) {
            underlyingNode.setProperty(DELETED_AT_PROPERTY, System.currentTimeMillis());
        }
    }
    
    public Date getCreatedAt() {
        return new Date((Long) underlyingNode.getProperty(CREATED_AT_PROPERTY));
    }
    
    public Date getDeletedAt() {
        Long deletedAt = (Long) underlyingNode.getProperty(DELETED_AT_PROPERTY);
        return deletedAt != null ?  new Date(deletedAt) : null;
    }

    public ConstructionStatus getConstructionStatus() {
        return ConstructionStatus.match((int) underlyingNode.getProperty(CONSTRUCTION_STATUS_PROPERTY));
    }
    
    public void setConstructionStatus(ConstructionStatus status) {
        if(getConstructionStatus() != status) {
            //TODO FIRE STATE CHANGE EVENT!
            underlyingNode.setProperty(CONSTRUCTION_STATUS_PROPERTY, status.getStatusId());
        }
    }

    public WorldNode getWorld() {
        Relationship rel = underlyingNode.getSingleRelationship(DynamicRelationshipType.withName(WorldNode.RELATION_WITHIN), org.neo4j.graphdb.Direction.OUTGOING);
        Node node = rel.getOtherNode(underlyingNode);
        return new WorldRepository().makeWorldNode(node);
    }


    public Direction getDirection() {
        return Direction.match((int) underlyingNode.getProperty(DIRECTION_PROPERTY));
    }

    public CuboidDimension getDimension() {
        int minX = (int) underlyingNode.getProperty(MIN_X_PROPERTY);
        int minY = (int) underlyingNode.getProperty(MIN_Y_PROPERTY);
        int minZ = (int) underlyingNode.getProperty(MIN_Z_PROPERTY);
        int maxX = (int) underlyingNode.getProperty(MAX_X_PROPERTY);
        int maxY = (int) underlyingNode.getProperty(MAX_Y_PROPERTY);
        int maxZ = (int) underlyingNode.getProperty(MAX_Z_PROPERTY);
        return new CuboidDimension(new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

    public StructureNode getParent() {
        Relationship rel = underlyingNode.getSingleRelationship(DynamicRelationshipType.withName(RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.INCOMING);
        if (rel != null) {
            Node parentNode = rel.getOtherNode(underlyingNode);
            return new StructureNode(parentNode);
        }
        return null;
    }

    public List<StructureNode> getSubstructures() {
        Iterable<Relationship> relationships = underlyingNode.getRelationships(DynamicRelationshipType.withName(RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING);
        List<StructureNode> substructures = Lists.newArrayList();
        for (Relationship rel : relationships) {
            substructures.add(new StructureNode(rel.getOtherNode(underlyingNode)));
        }
        return substructures;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructureNode other = (StructureNode) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

}
