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

package com.chingo247.settlercraft.structure.entities.structure;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
@Entity
public class PlayerMembership implements Serializable {

    @EmbeddedId
    private PlayerMembershipId playerMembershipId;
    
    @Column(name = "PLAYER_ID")
    private final UUID uuid;
    
    private String name;
    
//    @MapsId(value = "playerMembershipId")
    @ManyToOne(cascade = CascadeType.ALL)
    private Structure structure;

    /**
     * JPA Constructor.
     */
    protected PlayerMembership() {
        this.uuid = null;
    }

    /**
     * Constructor.
     *
     * @param name The name of the owner
     * @param uuid The uuid of the owner
     * @param player Whether the owner is a isPlayer or not
     */
    PlayerMembership(Player player, Structure structure) {
        this.structure = structure;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.playerMembershipId = new PlayerMembershipId(structure.getId(), player.getUniqueId());
    }

   

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerMembership other = (PlayerMembership) obj;

        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.uuid);
        return hash;
    }

    public Structure getStructure() {
        return structure;
    }

}
