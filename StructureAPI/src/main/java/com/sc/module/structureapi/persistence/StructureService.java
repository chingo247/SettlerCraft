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
package com.sc.module.structureapi.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.module.structureapi.structure.QStructure;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.structure.Structure.State;
import com.sc.module.structureapi.world.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureService extends AbstractService {

    public Structure getStructure(Long id) {
        QStructure qstructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        Structure cachedStructure = (Structure) session.get(Structure.class, id);
        if (cachedStructure != null) {
            session.close();
            return cachedStructure;
        }
        JPQLQuery query = new HibernateQuery(session);
        Structure structure = query.from(qstructure).where(qstructure.id.eq(id)).uniqueResult(qstructure);
        session.close();
        return structure;
    }

    public List<Structure> getStructures() {
        QStructure structure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<Structure> structures = query.from(structure).list(structure);
        session.close();
        return structures;
    }

//    public List<Structure> getStructures(UUID owner) {
//        QStructure structure = QStructure.structure;
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        List<Structure> structures = query.from(structure).where(structure.owner.eq(owner)).list(structure);
//        session.close();
//        return structures;
//    }
    public void delete(Structure structure) {
        Session session = HibernateUtil.getSession();
        QStructure qstructure = QStructure.structure;
        new HibernateDeleteClause(session, qstructure).where(qstructure.id.eq(structure.getId())).execute();
    }

    public Structure save(Structure structure) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            structure = (Structure) session.merge(structure);
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return structure;
    }

    /**
     * Determines if given location is on a structure.
     *
     * @param location The location
     * @return getStructure() != null
     */
    public boolean isOnStructure(Location location) {
        return getStructure(location) != null;
    }

    public Structure getStructure(com.sk89q.worldedit.Location location) {
        World world = Bukkit.getWorld(location.getWorld().getName());
        return getStructure(new Location(
                world,
                location.getPosition().getBlockX(),
                location.getPosition().getBlockY(),
                location.getPosition().getBlockZ())
        );
    }

    public Structure getStructure(Location location) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);

        Structure structure = query.from(qStructure)
                .where(qStructure.location().worldUUID.eq(location.getWorld().getUID())
                        .and(qStructure.dimension().minX.loe(location.getBlockX()))
                        .and(qStructure.dimension().maxX.goe(location.getBlockX()))
                        .and(qStructure.dimension().minZ.loe(location.getBlockZ()))
                        .and(qStructure.dimension().maxZ.goe(location.getBlockZ()))
                        .and(qStructure.dimension().minY.loe(location.getBlockY()))
                        .and(qStructure.dimension().maxY.goe(location.getBlockY()))
                        .and(qStructure.state.ne(State.REMOVED))
                ).singleResult(qStructure);

        session.close();
        return structure;
    }

    public boolean overlaps(Structure structure) {
        return getOverlappingStructure(structure) != null;
    }

    public Structure getOverlappingStructure(Structure structure) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);

        Structure result = query.from(qStructure)
                .where(qStructure.location().worldUUID.eq(structure.getWorldUUID())
                        .and(qStructure.dimension().maxX.goe(structure.getDimension().getMinX()).and(qStructure.dimension().minX.loe(structure.getDimension().getMaxX())))
                        .and(qStructure.dimension().maxY.goe(structure.getDimension().getMinY()).and(qStructure.dimension().minY.loe(structure.getDimension().getMaxY())))
                        .and(qStructure.dimension().maxZ.goe(structure.getDimension().getMinZ()).and(qStructure.dimension().minZ.loe(structure.getDimension().getMaxZ())))
                        .and(qStructure.state.ne(State.REMOVED))
                ).singleResult(qStructure); // return the first
        session.close();
        return result;
    }

    public List<Structure> getStructuresWithin(World world, Dimension dimension) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<Structure> result = query.from(qStructure)
                .where(qStructure.location().worldUUID.eq(world.getUID())
                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
                        .and(qStructure.state.ne(State.REMOVED))
                ).list(qStructure);
        session.close();
        return result;
    }
    
    public boolean hasStructuresWithin(World world, Dimension dimension) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        boolean result = query.from(qStructure)
                .where(qStructure.location().worldUUID.eq(world.getUID())
                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
                        .and(qStructure.state.ne(State.REMOVED))
                ).exists();
        session.close();
        return result;
    }


}
