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
package com.chingo247.settlercraft.entities;

import com.chingo247.settlercraft.structure.regions.CuboidDimension;
import com.sk89q.worldedit.world.World;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.validator.NotNull;

/**
 *
 * @author Chingo
 */
@Entity(name = "StructureEntity")
public class StructureEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private Long parent;
    
    private String name;
    
    private String world;
    
    @Embedded
    private CuboidDimension dimension;
    
    @NotNull
    private StructureType type;
    
    private Double value;
    
//    @OneToMany(fetch = FetchType.LAZY)
//    @Cascade(CascadeType.ALL)
//    private Set<StructureOwnerEntity> owners;
//    
//    @OneToMany(fetch = FetchType.LAZY)
//    @Cascade(CascadeType.ALL)
//    private Set<StructureMemberEntity> members;
    
    @Column(name = "structure_state")
    private StructureState state;

    /**
     * JPA Constructor.
     */
    protected StructureEntity() {
    }
    
    public StructureEntity(World world, CuboidDimension dimension, StructureType type) {
        this.state = StructureState.INITIALIZING;
        this.world = world.getName();
        this.dimension = dimension;
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setParent(Long id) {
        this.parent = parent;
    }

    public Long getParent() {
        return parent;
    }

    public StructureType getType() {
        return type;
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

    public String getWorld() {
        return world;
    }

    public CuboidDimension getDimension() {
        return dimension;
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
