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
package com.chingo247.settlercraft.persistence;

import com.chingo247.settlercraft.structure.entities.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.QPlayerOwnership;
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
        boolean isOwner = query.from(qpo).where(qpo.structure().id.eq(structure.getId()).and(qpo.player.eq(player.getUniqueId()))).exists();
        session.close();
        return isOwner;
    }

    public List<Structure> getOwnedStructures(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
        List<Structure> structures = query.from(qpo).where(qpo.player.eq(player.getUniqueId())).list(qpo.structure());
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
