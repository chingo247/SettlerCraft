package com.chingo247.settlercraft.model.legacy;

//
///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.structureapi.persistence.hibernate;
//
//import com.chingo247.settlercraft.structureapi.structure.old.PlayerOwnership;
//import com.chingo247.settlercraft.structureapi.structure.QPlayerOwnership;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.mysema.query.jpa.JPQLQuery;
//import com.mysema.query.jpa.hibernate.HibernateQuery;
//import com.sk89q.worldedit.entity.Player;
//import java.util.List;
//import org.hibernate.Session;
//
///**
// *
// * @author Chingo
// */
//public class PlayerOwnershipDAO {
//    
//    
//    public boolean isOwner(Player player, Structure structure) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
//        boolean isOwner = query.from(qpo).where(qpo.structure().id.eq(structure.getId()).and(qpo.player.eq(player.getUniqueId()))).exists();
//        session.close();
//        return isOwner;
//    }
//
//    public List<Structure> getOwnedStructures(Player player) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
//        List<Structure> structures = query.from(qpo).where(qpo.player.eq(player.getUniqueId())).list(qpo.structure());
//        session.close();
//        return structures;
//    }
//
//    public List<PlayerOwnership> getOwners(Structure structure) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
//        List<PlayerOwnership> owners = query.from(qpo).where(qpo.structure().id.eq(structure.getId())).list(qpo);
//        session.close();
//        return owners;
//    }
//    
//}
