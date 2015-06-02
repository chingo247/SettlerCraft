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

import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.core.Direction;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;

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
    public static final String MIN_X_PROPERTY = "minX", MIN_Y_PROPERTY = "minY", MIN_Z_PROPERTY = "minZ", MAX_X_PROPERTY = "maxX", MAX_Y_PROPERTY = "maxY", MAX_Z_PROPERTY = "maxZ";
    public static final String POS_X_PROPERTY = "x", POS_Y_PROPERTY = "y", POS_Z_PROPERTY = "z";
    public static final String CREATED_AT_PROPERTY = "createdAt", DELETED_AT_PROPERTY = "deletedAt", COMPLETED_AT_PROPERTY = "completedAt";
    public static final String PRICE_PROPERTY = "price";
    public static final String SIZE_PROPERTY = "size";
    public static final String PLACEMENT_TYPE_PROPERTY = "placementType";
    public static final String AUTO_REMOVED_PROPERTY = "autoremoved";
    public static final String CHECKED_HOLOGRAM_PROPERTY = "checkedHologram";

    // RelationShips
    private final Node underlyingNode;

    public StructureNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Long getId() {
        Object o = underlyingNode.getProperty(ID_PROPERTY);
        return o != null ? (Long) o : null;
    }

    public void setCompletedAt(Long date) {
        if (date != null) {
            underlyingNode.setProperty(COMPLETED_AT_PROPERTY, date);
        } else if (underlyingNode.hasProperty(COMPLETED_AT_PROPERTY)) {
            underlyingNode.removeProperty(COMPLETED_AT_PROPERTY);
        }
    }

    public Vector getPosition() {
        return new Vector(getX(), getY(), getZ());
    }

    public void setAutoremoved(boolean removed) {
        underlyingNode.setProperty(AUTO_REMOVED_PROPERTY, removed);
    }

    public boolean isAutoremoved() {
        if (underlyingNode.hasProperty(AUTO_REMOVED_PROPERTY)) {
            return (Boolean) underlyingNode.getProperty(AUTO_REMOVED_PROPERTY);
        }
        return false;
    }

    public void setDeletedAt(Long date) {
        if (date != null) {
            underlyingNode.setProperty(DELETED_AT_PROPERTY, date);
        } else if (underlyingNode.hasProperty(DELETED_AT_PROPERTY)) {
            underlyingNode.removeProperty(DELETED_AT_PROPERTY);
        }
    }
    
    public void setCreatedAt(Long date) {
        if (date != null) {
            underlyingNode.setProperty(CREATED_AT_PROPERTY, date);
        } else if (underlyingNode.hasProperty(CREATED_AT_PROPERTY)) {
            underlyingNode.removeProperty(CREATED_AT_PROPERTY);
        }
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

            if (status == ConstructionStatus.COMPLETED) {
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
    
    public StructureOwnerNode findOwner(UUID possibleOwner) {
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
            if (ownerNode.getUUID().equals(possibleOwner)) {
                Integer typeId = (Integer) rel.getProperty("Type");
                StructureOwnerType type = StructureOwnerType.match(typeId);
                return new StructureOwnerNode(ownerNode.getRawNode(), type);
            }
        }
        return null;
    }

    public boolean addOwner(SettlerNode node, StructureOwnerType type) {
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
            if (ownerNode.getUUID().equals(node.getUUID())) {
                return false;
            }
            
        }
        Relationship relationship = underlyingNode.createRelationshipTo(node.getRawNode(), DynamicRelationshipType.withName(StructureRelTypes.RELATION_OWNED_BY));
        relationship.setProperty("Type", type.getTypeId());
        return true;
    }

    public boolean isOwner(UUID possibleOwner) {
        return findOwner(possibleOwner) != null;
    }
    
    public boolean isOwner(UUID possibleMaster, StructureOwnerType type) {
        StructureOwnerNode owner = findOwner(possibleMaster);
        if(owner == null) {
            return false;
        }
        return owner.getType() == type;
    }

    public void removeOwner(UUID owner) {
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            SettlerNode ownerNode = new SettlerNode(rel.getOtherNode(underlyingNode));
            if (ownerNode.getUUID().equals(owner)) {
                rel.delete();
                break;
            }
        }
    }

    public List<StructureOwnerNode> getOwners() {
        List<StructureOwnerNode> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Integer typeProp = (Integer) rel.getProperty("Type");
                StructureOwnerType type = StructureOwnerType.match(typeProp);
                StructureOwnerNode ownerNode = new StructureOwnerNode(rel.getOtherNode(underlyingNode), type);
                owners.add(ownerNode);
            }
        }
        return owners;
    }

    public List<StructureOwnerNode> getOwners(StructureOwnerType ownerType) {
        Preconditions.checkNotNull(ownerType, "owner type can not be null, use getOwners() instead");
        List<StructureOwnerNode> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Integer typeId = (Integer) rel.getProperty("Type");
                StructureOwnerType type = StructureOwnerType.match(typeId);
                if (type == ownerType) {
                    StructureOwnerNode ownerNode = new StructureOwnerNode(rel.getOtherNode(underlyingNode), type);
                    owners.add(ownerNode);
                }
            }
        }
        return owners;
    }

    public List<StructureNode> getSubstructures() {
        Iterable<Relationship> relationships = underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.INCOMING);
        List<StructureNode> substructures = Lists.newArrayList();
        for (Relationship rel : relationships) {
            StructureNode sno = new StructureNode(rel.getOtherNode(underlyingNode));
            if (sno.getConstructionStatus() != ConstructionStatus.REMOVED) {
                substructures.add(new StructureNode(rel.getOtherNode(underlyingNode)));
            }
        }
        return substructures;
    }

    public boolean hasSubstructures() {
        for (Relationship s : underlyingNode.getRelationships(org.neo4j.graphdb.Direction.INCOMING, DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE))) {
            StructureNode sn = new StructureNode(s.getOtherNode(underlyingNode));
            if (sn.getConstructionStatus() != ConstructionStatus.REMOVED) {
                return true;
            }
        }
        return false;
    }

    public StructureNode getRoot() {
        TraversalDescription traversalDescription = underlyingNode.getGraphDatabase().traversalDescription();
        Iterator<Node> nodeIt = traversalDescription.relationships(DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING)
                .depthFirst()
                .traverse(underlyingNode)
                .nodes()
                .iterator();

        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            if (!nodeIt.hasNext()) {
                return new StructureNode(n);
            }

        }
        return this;
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
