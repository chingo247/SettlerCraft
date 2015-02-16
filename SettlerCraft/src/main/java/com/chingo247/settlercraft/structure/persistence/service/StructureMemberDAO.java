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
package com.chingo247.settlercraft.structure.persistence.service;

import com.chingo247.settlercraft.structure.persistence.entities.QStructurePlayerMemberEntity;
import com.chingo247.settlercraft.structure.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structure.persistence.entities.StructureEntity;
import com.chingo247.settlercraft.structure.persistence.entities.StructurePlayerMemberEntity;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureMemberDAO extends AbstractDAOImpl<StructurePlayerMemberEntity, Long> {

    public List<StructurePlayerMemberEntity> getOwnersForStructure(long structureId) {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructurePlayerMemberEntity qspoe = QStructurePlayerMemberEntity.structurePlayerMemberEntity;
        List<StructurePlayerMemberEntity> owners = query.from(qspoe).where(qspoe.playerMembershipId().structure.eq(structureId)).list(qspoe);
        session.close();
        return owners;
    }

    public List<StructureEntity> getStructureForMember(UUID player) {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructurePlayerMemberEntity qspoe = QStructurePlayerMemberEntity.structurePlayerMemberEntity;
        List<StructureEntity> owners = query.from(qspoe).where(qspoe.uuid.eq(player)).list(qspoe.structure());
        session.close();
        return owners;
    }

    public boolean isMember(UUID player, long structure) {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QStructurePlayerMemberEntity qspoe = QStructurePlayerMemberEntity.structurePlayerMemberEntity;
        boolean owners = query.from(qspoe)
                .where(qspoe.playerMembershipId().structure.eq(structure)
                        .and(qspoe.playerMembershipId().player.eq(player)))
                .exists();
        session.close();
        return owners;
    }

}
