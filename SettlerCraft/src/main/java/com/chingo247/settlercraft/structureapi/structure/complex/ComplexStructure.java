
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
package com.chingo247.settlercraft.structureapi.structure.complex;

import com.chingo247.settlercraft.bukkit.WorldEditUtil;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.plan.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.old.Structure.State;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
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
import javax.persistence.Transient;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
@Entity(name = "StructureComplex")
public class ComplexStructure  {

    @Id
    @GeneratedValue
    private Long id;
    
    private String name;

    @Embedded
    private com.chingo247.settlercraft.structureapi.world.World world;
    
    @Embedded
    private CuboidDimension dimension;
    @Column(name = "m_state")
    private State state;

    @Transient
    private ComplexStructureDAO complexStructureDAO;
    
    
    /**
     * JPA Constructor
     */
    protected ComplexStructure() {}
    
    ComplexStructure(StructurePlan plan, World world, Vector position, Direction direction) {
        this.state = State.INITIALIZING;
        this.dimension = SchematicUtil.calculateDimension(getSchematicData(), position, direction);
        this.name = plan.getName();
    }

    public Long getId() {
        return id;
    }
    
    private ComplexStructureDAO getDAO() {
        if(complexStructureDAO == null) {
            complexStructureDAO = new ComplexStructureDAO();
        }
        return complexStructureDAO;
    }
    
    
    
    public String getName() {
        return name;
    }
    
    public World getWorld() {
         return WorldEditUtil.getWorld(world.getName());
    }

    public final SchematicData getSchematicData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Schematic getSchematic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
   

   
    
    
    public State getState() {
        return state;
    }

    public CuboidDimension getDimension() {
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
    
    
    
    public boolean isWithin(World world, CuboidDimension dimension) {
        return this.world.getName().equals(world.getName())
                && dimension.getMinX() > this.dimension.getMinX()
                && dimension.getMinY() > this.dimension.getMinY()
                && dimension.getMinZ() > this.dimension.getMinZ()
                && dimension.getMaxX() < this.dimension.getMaxX()
                && dimension.getMaxY() < this.dimension.getMaxY()
                && dimension.getMaxZ() < this.dimension.getMaxZ();
    }

    public boolean isWithin(World world, Vector pos) {
        return this.world.getName().equals(world.getName())
                && pos.getBlockX() < dimension.getMaxX() && pos.getBlockX() > dimension.getMinX()
                && pos.getBlockY() < dimension.getMaxY() && pos.getBlockY() > dimension.getMinY()
                && pos.getBlockZ() < dimension.getMaxZ() && pos.getBlockZ() > dimension.getMinZ();
    }
    public List<StructureMembership> getMembers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
