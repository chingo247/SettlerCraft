/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.module.structureapi.structure.PlayerOwnership;
import com.sc.module.structureapi.structure.QPlayerOwnership;
import com.sc.module.structureapi.structure.Structure;
import java.util.List;
import org.bukkit.entity.Player;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class PlayerOwnershipService extends AbstractService<PlayerOwnership> {

    @Override
    public PlayerOwnership save(PlayerOwnership playerOwnership) {
        return super.save(playerOwnership); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(PlayerOwnership playerOwnership) {
        super.delete(playerOwnership); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isOwner(Player player, Structure structure) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
        boolean isOwner = query.from(qpo).where(qpo.structure().id.eq(structure.getId()).and(qpo.uuid.eq(player.getUniqueId()))).exists();
        session.close();
        return isOwner;
    }

    public List<Structure> getOwnedStructures(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
        List<Structure> structures = query.from(qpo).where(qpo.uuid.eq(player.getUniqueId())).list(qpo.structure());
        session.close();
        return structures;
    }

    public List<PlayerOwnership> getOwners(Structure structure) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
        List<PlayerOwnership> owners = query.from(qpo).where(qpo.structure().id.eq(structure.getId())).list(qpo);
        session.close();
        return owners;
    }

}
