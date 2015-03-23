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
package com.chingo247.settlercraft.core.persistence.entities.structure;

import com.chingo247.settlercraft.core.world.Direction;
import com.chingo247.settlercraft.core.persistence.entities.world.CuboidDimension;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Index;

/**
 *
 * @author Chingo
 */
@Entity(name = "structure_entity")
public class StructureEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Index(name = "parent_structure")
    private Long parent;
    
    @Column(name = "structure_name")
    private String name;
    
    @Column(name = "structure_world")
    private String world;
    
    @Index(name = "structure_world_id")
    private UUID worldUUID;
    
    @Embedded
    private CuboidDimension dimension;
       
    private Direction direction;
    
    @Column(name = "value")
    private Double value;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "structureentity")
    private Set<StructurePlayerEntity> players;
    
    @Column(name = "structure_state")
    private StructureState state;

    /**
     * JPA Constructor.
     */
    protected StructureEntity() {
    }
    
    public StructureEntity(String world, UUID worldUUID, CuboidDimension dimension, Direction direction) {
        this.state = StructureState.INITIALIZING;
        this.world = world;
        this.worldUUID = worldUUID;
        this.dimension = dimension;
        this.direction = direction;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setParent(Long id) {
        this.parent = id;
    }

    public Direction getDirection() {
        return direction;
    }
    
    public Long getParent() {
        return parent;
    }

  
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setState(StructureState state) {
        this.state = state;
    }

    public StructureState getState() {
        return state;
    }

    public CuboidDimension getDimension() {
        return dimension;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public String getWorld() {
        return world;
    }

    public Set<StructurePlayerEntity> getPlayers() {
        return players;
    }
    
    
    
    
//    private void notNullOwners() {
//        if(owners == null) {
//            owners = new HashSet<>();
//        }
//    }
//    
//    private void notNullMembers() {
//        if(members == null) {
//            members = new HashSet<>();
//        }
//    }
//
//    public void addMember(StructureMemberEntity member) {
//        notNullMembers();
//        members.add(member);
//    }
//    
//    public void removeMember(StructureMemberEntity member) {
//        notNullMembers();
//        members.remove(member);
//    }
//    
//    public void addOwner(StructureOwnerEntity owner) {
//        notNullOwners();
//        owners.add(owner);
//    }
//    
//    public void removeOwner(StructureOwnerEntity owner) {
//        notNullOwners();
//        owners.remove(owner);
//    }

    public Double getValue() {
        return value;
    }
    
}
