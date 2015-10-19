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
package com.chingo247.structureapi.model.owner;

import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.structureapi.model.Relations;
import com.chingo247.structureapi.model.settler.Settler;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.parboiled.common.Preconditions;

/**
 *
 * @author Chingo
 */
public class OwnerDomain {
    
    private final Node underlyingNode;

    public OwnerDomain(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }
    
    public boolean isOwner(UUID settler) {
        return getOwnership(settler) != null;
    }
    
    public boolean isOwnerOfType(UUID settler, OwnerType ownerType) {
        Ownership o = getOwnership(settler);
        return o != null && o.getOwnerType() == ownerType;
    }
    
    public Ownership getOwnership(UUID settler) {
        Ownership ownership = null;
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(Relations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            Settler ownerNode = new Settler(rel.getOtherNode(underlyingNode));
            if (ownerNode.getUniqueIndentifier().equals(settler)) {
                ownership = new Ownership(ownerNode, rel);
                break;
            }
        }
        return ownership;
    }
    
    public void setOwnership(IBaseSettler settler, OwnerType ownerType) {
        // if exists... update it
        for(Ownership o : getOwnerships()) {
            if(o.getOwner().getUniqueIndentifier().equals(settler.getUniqueIndentifier())) {
                o.getRelation().setProperty("Type", ownerType.getTypeId());
                return;
            }
        }
        // otherwise create a new one
        Relationship r = underlyingNode.createRelationshipTo(settler.getNode(), DynamicRelationshipType.withName(Relations.RELATION_OWNED_BY));
        r.setProperty("Type", ownerType.getTypeId());
    }
    
    public boolean removeOwnership(IBaseSettler settler) {
        return removeOwnership(settler.getUniqueIndentifier());
    }
    
    public boolean removeOwnership(UUID settler) {
        for(Ownership o : getOwnerships()) {
            if(o.getOwner().getUniqueIndentifier().equals(settler)) {
                o.getRelation().delete();
                return true;
            }
        }
        return false;
    }
    
    public List<Ownership> getOwnerships() {
        List<Ownership> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(Relations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Settler ownerNode = new Settler(rel.getOtherNode(underlyingNode));
                owners.add(new Ownership(ownerNode, rel));
            }
        }
        return owners;
    }
    
    public List<Settler> getOwners(OwnerType ownerType) {
        Preconditions.checkNotNull(ownerType, "OwnerType may not be null");

        List<Settler> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(Relations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Integer typeId = (Integer) rel.getProperty("Type");
                OwnerType type = OwnerType.match(typeId);
                if (type == ownerType) {
                    Settler ownerNode = new Settler(rel.getOtherNode(underlyingNode));
                    owners.add(ownerNode);
                }
            }
        }
        return owners;
    }
    
}
