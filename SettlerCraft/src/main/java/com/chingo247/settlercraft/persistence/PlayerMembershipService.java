/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.persistence;

import com.chingo247.settlercraft.structure.entities.structure.PlayerMembership;
import com.chingo247.settlercraft.structure.entities.structure.QPlayerMembership;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.List;
import org.bukkit.entity.Player;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class PlayerMembershipService extends AbstractService<PlayerMembership> {

    @Override
    public PlayerMembership save(PlayerMembership playerMembership) {
        return super.save(playerMembership); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(PlayerMembership playerMembership) {
        super.delete(playerMembership); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isMember(Player player, Structure structure) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerMembership qpo = QPlayerMembership.playerMembership;
        boolean isOwner = query.from(qpo).where(qpo.structure().id.eq(structure.getId()).and(qpo.uuid.eq(player.getUniqueId()))).exists();
        session.close();
        return isOwner;
    }

    public List<Structure> getOwnedStructures(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerMembership qpo = QPlayerMembership.playerMembership;
        List<Structure> structures = query.from(qpo).where(qpo.uuid.eq(player.getUniqueId())).list(qpo.structure());
        session.close();
        return structures;
    }
    
    public List<PlayerMembership> getMembers(Structure structure) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerMembership qpo = QPlayerMembership.playerMembership;
        List<PlayerMembership> members = query.from(qpo).where(qpo.structure().id.eq(structure.getId())).list(qpo);
        session.close();
        return members;
    }

}
