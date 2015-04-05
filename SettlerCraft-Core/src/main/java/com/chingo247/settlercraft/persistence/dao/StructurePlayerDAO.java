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
package com.chingo247.settlercraft.persistence.dao;

import com.chingo247.settlercraft.persistence.entities.structure.QStructurePlayerEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.persistence.hibernate.HibernateUtil;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructurePlayerDAO extends AbstractDAOImpl<StructurePlayerEntity, Long>{
    
    public List<StructurePlayerEntity>  getOwnersForStructure(long structureId) {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructurePlayerEntity qspoe = QStructurePlayerEntity.structurePlayerEntity;
        List<StructurePlayerEntity> owners = query.from(qspoe).where(qspoe.structureentity().id.eq(structureId)).list(qspoe);
        session.close();
        return owners;
    }
    
    public List<StructureEntity>  getStructureForOwner(UUID player) {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructurePlayerEntity qspoe = QStructurePlayerEntity.structurePlayerEntity;
        List<StructureEntity> owners = query.from(qspoe).where(qspoe.player.eq(player)).list(qspoe.structureentity());
        session.close();
        return owners;
    }
    
    public boolean isOwner(UUID player, long structure) {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructurePlayerEntity qspoe = QStructurePlayerEntity.structurePlayerEntity;
        boolean owners = query.from(qspoe)
                .where(qspoe.structureentity().id.eq(structure)
                        .and(qspoe.player.eq(player)))
                .exists();
        session.close();
        return owners;
    }

    
}
