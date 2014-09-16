/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.util;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.module.structureapi.persistence.HibernateUtil;
import com.sc.module.structureapi.structure.ConstructionSite;
import com.sc.module.structureapi.structure.QStructure;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.world.Dimension;
import java.util.List;
import org.bukkit.World;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureUtil {
    
        public static List<Structure> getStructuresWithinDimension(World world, Dimension dimension) {
        QStructure qStructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<Structure> result = query.from(qStructure)
                .where(qStructure.location().worldUUID.eq(world.getUID())
                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
                        .and(qStructure.constructionSite().state.ne(ConstructionSite.State.REMOVED))
                ).list(qStructure);
        session.close();
        return result;
    }



    
}
