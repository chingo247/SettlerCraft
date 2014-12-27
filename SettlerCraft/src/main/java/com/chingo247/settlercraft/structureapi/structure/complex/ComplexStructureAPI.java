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

import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.SchematicDataDAO;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.world.Dimension;
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
        Dimension dimension = SchematicUtil.calculateDimension(schematicData, pos, direction);
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
