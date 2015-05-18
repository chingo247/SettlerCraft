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
package com.chingo247.settlercraft.structureapi.persistence.entities.structure;

import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.core.Direction;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    public static final String ID_PROPERTY = "structureId";
    public static final String NAME_PROPERTY = "name";
    public static final String DIRECTION_PROPERTY = "direction";
//    public static final String WORLD_PROPERTY = "world";
//    public static final String WORLD_ID_PROPERTY = "worldId";
    public static final String CONSTRUCTION_STATUS_PROPERTY = "constructionStatus";
    public static final String MIN_X_PROPERTY = "minX", MIN_Y_PROPERTY = "minY", MIN_Z_PROPERTY = "minZ", MAX_X_PROPERTY = "maxX", MAX_Y_PROPERTY = "maxY", MAX_Z_PROPERTY = "MAX_Z";
    public static final String POS_X_PROPERTY = "x", POS_Y_PROPERTY = "y", POS_Z_PROPERTY = "z";
    public static final String CREATED_AT_PROPERTY = "createdAt", DELETED_AT_PROPERTY = "deletedAt", COMPLETED_AT_PROPERTY = "completedAt";
    public static final String PRICE_PROPERTY = "price";
    public static final String SIZE_PROPERTY = "size";
    public static final String PLACEMENT_TYPE_PROPERTY = "placementType";
    
    

    // RelationShips
    private final Node underlyingNode;

    public StructureNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Long getId() {
        Object o = underlyingNode.getProperty(ID_PROPERTY);
        return o != null ? (Long) o : null;
    }
    
    
    /**
     * Gets the actual node
     *
     * @return The actual Node
     */
    public Node getRawNode() {
        return underlyingNode;
    }
    
    public Integer getX() {
        Object o = underlyingNode.getProperty(POS_X_PROPERTY);
        return (Integer) o;
    }
    
    public Integer getY() {
        Object o = underlyingNode.getProperty(POS_Y_PROPERTY);
        return (Integer) o;
    }
    
    public Integer getZ() {
        Object o = underlyingNode.getProperty(POS_Z_PROPERTY);
        return (Integer) o;
    }

    public Double getPrice() {
        Object o = underlyingNode.getProperty(PRICE_PROPERTY);
        return o != null ? (Double) o : 0;
    }
    
    public void setPrice(double price) {
        underlyingNode.setProperty(PRICE_PROPERTY, price);
    }
    
    public Integer getSize() {
        Object o = underlyingNode.getProperty(SIZE_PROPERTY);
        return o != null ? (int) o : null;
    }
    
    public void setSize(int size) {
        underlyingNode.setProperty(SIZE_PROPERTY, size);
    }

    public String getName() {
        return (String) underlyingNode.getProperty(NAME_PROPERTY);
    }

    public void setName(String name) {
        underlyingNode.setProperty(NAME_PROPERTY, name);
    }

    public Date getCreatedAt() {
        Object o = underlyingNode.getProperty(CREATED_AT_PROPERTY);
        return o != null ? new Date((Long) o) : null;
    }

    public Date getDeletedAt() {
        if (!underlyingNode.hasProperty(DELETED_AT_PROPERTY)) {
            return null;
        }
        Object o = underlyingNode.getProperty(DELETED_AT_PROPERTY);
        return o != null ? new Date((Long) o) : null;
    }

    public Date getCompletedAt() {
        if (!underlyingNode.hasProperty(COMPLETED_AT_PROPERTY)) {
            return null;
        }
        Object o = underlyingNode.getProperty(COMPLETED_AT_PROPERTY);
        return o != null ? new Date((Long) o) : null;
    }

    public ConstructionStatus getConstructionStatus() {
        Object o = underlyingNode.getProperty(CONSTRUCTION_STATUS_PROPERTY);
        return o != null ? ConstructionStatus.match((int) o) : null;
    }

    public void setConstructionStatus(ConstructionStatus status) {
        if (getConstructionStatus() != status) {
            //TODO FIRE STATE CHANGE EVENT!
            underlyingNode.setProperty(CONSTRUCTION_STATUS_PROPERTY, status.getStatusId());
            
            if(status == ConstructionStatus.COMPLETED) {
                underlyingNode.setProperty(COMPLETED_AT_PROPERTY, System.currentTimeMillis());
            } else if (status == ConstructionStatus.REMOVED) {
                underlyingNode.setProperty(DELETED_AT_PROPERTY, System.currentTimeMillis());
            }
            
        }
    }

    public StructureWorldNode getWorld() {
        Relationship rel = underlyingNode.getSingleRelationship(DynamicRelationshipType.withName(StructureRelTypes.RELATION_WITHIN), org.neo4j.graphdb.Direction.OUTGOING);
        Node node = rel.getOtherNode(underlyingNode);
        return new StructureWorldNode(node);
    }

    public Direction getDirection() {
        return Direction.match((int) underlyingNode.getProperty(DIRECTION_PROPERTY));
    }

    public CuboidRegion getCuboidRegion() {
        int minX = (int) underlyingNode.getProperty(MIN_X_PROPERTY);
        int minY = (int) underlyingNode.getProperty(MIN_Y_PROPERTY);
        int minZ = (int) underlyingNode.getProperty(MIN_Z_PROPERTY);
        int maxX = (int) underlyingNode.getProperty(MAX_X_PROPERTY);
        int maxY = (int) underlyingNode.getProperty(MAX_Y_PROPERTY);
        int maxZ = (int) underlyingNode.getProperty(MAX_Z_PROPERTY);
        return new CuboidRegion(new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

    public StructureNode getParent() {
        Relationship rel = underlyingNode.getSingleRelationship(DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING);
        if (rel != null) {
            Node parentNode = rel.getOtherNode(underlyingNode);
            return new StructureNode(parentNode);
        }
        return null;
    }

    public void addOwner(SettlerNode node, StructureOwnerType type) {
        Relationship relationship = underlyingNode.createRelationshipTo(node.getRawNode(), DynamicRelationshipType.withName("OwnedBy"));
        relationship.setProperty("Type", type.getTypeId());
    }

    public boolean isOwner(UUID possibleOwner) {
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
            SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
            if (ownerNode.getId().equals(possibleOwner)) {
                return true;
            }
        }
        return false;
    }

    public void removeOwner(UUID owner) {
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
            SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
            if (ownerNode.getId().equals(owner)) {
                rel.delete();
                break;
            }
        }
    }

    public List<SettlerNode> getOwners() {
        List<SettlerNode> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
            SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
            owners.add(ownerNode);
        }
        return owners;
    }

    public List<SettlerNode> getOwners(StructureOwnerType ownerType) {
        Preconditions.checkNotNull(ownerType, "owner type can not be null, use getOwners() instead");
        List<SettlerNode> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
            if (rel.hasProperty("Type")) {
                String type = (String) rel.getProperty("Type");
                if (StructureOwnerType.valueOf(type) == ownerType) {
                    SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
                    owners.add(ownerNode);
                }
            }
        }
        return owners;
    }

    public List<StructureNode> getSubstructures() {
        Iterable<Relationship> relationships = underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING);
        List<StructureNode> substructures = Lists.newArrayList();
        for (Relationship rel : relationships) {
            substructures.add(new StructureNode(rel.getOtherNode(underlyingNode)));
        }
        return substructures;
    }
    
    public void addSubstructure(StructureNode otherNode) {
        otherNode.getRawNode().createRelationshipTo(underlyingNode, DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE));
    }
    
    public String getPlacementType() {
       return (String) underlyingNode.getProperty(PLACEMENT_TYPE_PROPERTY);
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
