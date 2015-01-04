
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

import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.SchematicDataDAO;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public class ComplexStructureAPI implements StructureAPI{
    
    private SchematicDataDAO schematicDataDAO;

    public ComplexStructureAPI() {
        this.schematicDataDAO = new SchematicDataDAO();
        
        
    }
    
    
    

    @Override
    public ComplexStructure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
        SchematicData schematicData = schematicDataDAO.find(plan.getChecksum());
        
        // Check if it is a valid location
        CuboidDimension dimension = SchematicUtil.calculateDimension(schematicData, pos, direction);
        if (dimension.getMinY() < 0) {
            throw new StructureException("Can't place structures below 0");
        } else if (dimension.getMaxY() > world.getMaxY()) {
            throw new StructureException("Can't place structurs above " + world.getMaxY() + " (World max height)");
        }
        
        // Check overlap
        
        
        
        ComplexStructure complex = new ComplexStructure(plan, world, pos, direction);
        
        return null;
    }

//    @Override
//    public boolean overlaps(World world, Dimension dimension) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QPlot qp = QPlot.plot;
//        boolean result = query.from(qp)
//                .where(qp.world.eq(world.getName())
//                        .and(qp.dimension().maxX.goe(dimension.getMinX()).and(qp.dimension().minX.loe(dimension.getMaxX())))
//                        .and(qp.dimension().maxY.goe(dimension.getMinY()).and(qp.dimension().minY.loe(dimension.getMaxY())))
//                        .and(qp.dimension().maxZ.goe(dimension.getMinZ()).and(qp.dimension().minZ.loe(dimension.getMaxZ())))
//                ).exists();
//        session.close();
//        return result;
//    }

    @Override
    public void build(Player player, ComplexStructure structure, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void demolish(Player player, ComplexStructure structure, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop(Player player, ComplexStructure structure, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    void setState(ComplexStructure complex) {
        
    }
    
}
