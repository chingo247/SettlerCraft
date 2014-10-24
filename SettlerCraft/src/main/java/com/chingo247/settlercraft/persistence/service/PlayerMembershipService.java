/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.persistence.service;

import com.chingo247.settlercraft.persistence.hibernate.HibernateUtil;
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
