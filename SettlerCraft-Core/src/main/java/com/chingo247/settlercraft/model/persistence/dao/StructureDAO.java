package com.chingo247.settlercraft.model.persistence.dao;


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

import com.chingo247.settlercraft.model.persistence.entities.structure.QStructureEntity;
import com.chingo247.settlercraft.model.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.model.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.model.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.model.persistence.hibernate.HibernateUtil;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;

/**
 * Structure Data Access Object for performing actions on the database
 * @author Chingo
 */
public class StructureDAO extends AbstractDAOImpl<StructureEntity, Long>  {

    public boolean overlaps(UUID world, CuboidDimension dimension) {
        QStructureEntity qStructure = QStructureEntity.structureEntity;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        boolean result = query.from(qStructure)
                .where(qStructure.worldUUID.eq(world)
                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
                        .and(qStructure.state.ne(StructureState.REMOVED))
                ).exists();
        session.close();
        return result;
    }
    
    public List<StructureEntity> getStructureForWorld(UUID world) {
        QStructureEntity qStructureEntity = QStructureEntity.structureEntity;
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        List<StructureEntity> ses = query.from(qStructureEntity).where(qStructureEntity.worldUUID.eq(world)).list(qStructureEntity);
        session.close();
        return ses;
    }
    
    public List<StructureEntity> findChildren(long parent) {
        QStructureEntity qStructureEntity = QStructureEntity.structureEntity;
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        List<StructureEntity> ses = query.from(qStructureEntity).where(qStructureEntity.parent.eq(parent)).list(qStructureEntity);
        session.close();
        return ses;
    }
    
    
   

}
