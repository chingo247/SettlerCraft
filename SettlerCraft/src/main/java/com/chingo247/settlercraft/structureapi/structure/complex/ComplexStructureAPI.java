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

import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.MemDBUtil;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class ComplexStructureAPI implements StructureAPI{
    
    

    @Override
    public StructureComplex create(Player player, StructurePlan plan, World world, Vector position, Direction direction) {
        
    }

    @Override
    public boolean overlaps(World world, Dimension dimension) {
        Session memSession = MemDBUtil.getSession();
        JPQLQuery memQuery = new HibernateQuery(memSession);
        QPlot qp = QPlot.plot;
        boolean result = memQuery.from(qp)
                .where(qp.world.eq(world.getName())
                        .and(qp.dimension().maxX.goe(dimension.getMinX()).and(qp.dimension().minX.loe(dimension.getMaxX())))
                        .and(qp.dimension().maxY.goe(dimension.getMinY()).and(qp.dimension().minY.loe(dimension.getMaxY())))
                        .and(qp.dimension().maxZ.goe(dimension.getMinZ()).and(qp.dimension().minZ.loe(dimension.getMaxZ())))
                ).exists();
        memSession.close();
        if(result) {
           return true; 
        } else {
            Session session = HibernateUtil.getSession();
            JPQLQuery query = new HibernateQuery(session);
            QStructureComplex qsc = QStructureComplex.structureComplex;
            List<StructureComplex> structures = query.from(eps)
        }
        
    }

    @Override
    public void build(Player player, StructureComplex structure, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void demolish(Player player, StructureComplex structure, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop(Player player, StructureComplex structure, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    void setState(StructureComplex complex) {
        
    }
    
}
