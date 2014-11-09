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
package com.chingo247.settlercraft.main.persistence;

import com.chingo247.settlercraft.main.structure.QStructure;
import com.chingo247.settlercraft.main.structure.Structure;
import com.chingo247.settlercraft.main.world.Dimension;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.List;
import org.hibernate.Session;

/**
 * Structure Data Access Object for performing actions on the database
 * @author Chingo
 */
public class StructureDAO extends AbstractDAOImpl<Structure>  {


    public Structure getStructure(String world, int x, int y, int z) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);

        Structure structure = query.from(qStructure)
                .where(qStructure.location().world.eq(world)
                        .and(qStructure.dimension().minX.loe(x))
                        .and(qStructure.dimension().maxX.goe(x))
                        .and(qStructure.dimension().minZ.loe(z))
                        .and(qStructure.dimension().maxZ.goe(z))
                        .and(qStructure.dimension().minY.loe(y))
                        .and(qStructure.dimension().maxY.goe(y))
                        .and(qStructure.state.ne(Structure.State.REMOVED))
                ).singleResult(qStructure);
        session.close();
        return structure;
    }

    public boolean overlaps(String world, Dimension dimension) {
        return !getStructuresWithin(world, dimension).isEmpty();
    }

    public List<Structure> getStructuresWithin(String world, Dimension dimension) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<Structure> result = query.from(qStructure)
                .where(qStructure.location().world.eq(world)
                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
                        .and(qStructure.state.ne(Structure.State.REMOVED))
                ).list(qStructure);
        session.close();
        return result;
    } 

}
