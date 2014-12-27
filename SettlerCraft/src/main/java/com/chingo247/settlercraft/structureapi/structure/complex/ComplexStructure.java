/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.structureapi.structure.complex;

import com.chingo247.settlercraft.bukkit.WorldEditUtil;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.plan.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.Structure.State;
import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.world.World;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
@Entity(name = "StructureComplex")
public class ComplexStructure implements Serializable, SchematicStructure {

    @Id
    @GeneratedValue
    private Long id;
    
    private String name;

    @Embedded
    private com.chingo247.settlercraft.structureapi.world.World world;
    
    @Embedded
    private Dimension dimension;
    @Column(name = "m_state")
    private State state;

    
    
    /**
     * JPA Constructor
     */
    protected ComplexStructure() {}
    
    ComplexStructure(StructurePlan plan, World world, Vector position, Direction direction) {
        this.state = State.INITIALIZING;
        this.dimension = SchematicUtil.calculateDimension(getSchematicData(), position, direction);
        this.name = plan.getName();
    }

    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
     @Override
    public World getWorld() {
         return WorldEditUtil.getWorld(world.getName());
    }

    @Override
    public final SchematicData getSchematicData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Schematic getSchematic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
   

   
    
    
    @Override
    public State getState() {
        return state;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    

    public void setName(String name) {
        this.name = name;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    /**
     * Checks whether this structure has substructures by executing a query
     * @return True if this structure has substructures
     */
    public boolean hasSubstructures() {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructureComplexTree qsct = QStructureComplexTree.structureComplexTree;
        boolean hasSubstructure = query.from(qsct).where(qsct.parent.eq(this.id)).exists();
        session.close();
        return hasSubstructure;
    }
    
    /**
     * Gets the Substructures of this structure by executing a query
     * @return The Substructures of this structure or an empty list
     */
    public List<ComplexStructure> getSubstructures() {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QComplexStructure qStructure = QComplexStructure.complexStructure;
        QStructureComplexTree qTree = QStructureComplexTree.structureComplexTree;
        List<ComplexStructure> result = query.from(qStructure, qTree).where(qTree.parent.eq(this.id).and(qStructure.id.eq(qTree.child))).list(qStructure);
        session.close();
        return result;
    }
    
    
    
    @Override
    public boolean isWithin(World world, Dimension dimension) {
        return this.world.getName().equals(world.getName())
                && dimension.getMinX() > this.dimension.getMinX()
                && dimension.getMinY() > this.dimension.getMinY()
                && dimension.getMinZ() > this.dimension.getMinZ()
                && dimension.getMaxX() < this.dimension.getMaxX()
                && dimension.getMaxY() < this.dimension.getMaxY()
                && dimension.getMaxZ() < this.dimension.getMaxZ();
    }

    @Override
    public boolean isWithin(World world, Vector pos) {
        return this.world.getName().equals(world.getName())
                && pos.getBlockX() < dimension.getMaxX() && pos.getBlockX() > dimension.getMinX()
                && pos.getBlockY() < dimension.getMaxY() && pos.getBlockY() > dimension.getMinY()
                && pos.getBlockZ() < dimension.getMaxZ() && pos.getBlockZ() > dimension.getMinZ();
    }

    
    
    
    @Override
    public List<StructureMembership> getMembers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StructureOwnership> getOwners() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComplexStructure other = (ComplexStructure) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    
    
    
    
   
    
    
}
