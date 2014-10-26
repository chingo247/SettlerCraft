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


package com.chingo247.structureapi;

import java.io.Serializable;
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
public class PlayerOwnership implements Serializable {
    
    public enum Type {
        /**
         * May modify parts of the structure (Added to worldguard region)
         */
        BASIC,
        /*
         * May modify parts of the structure
         * Gets a share when the structure is refunded
         * May assign new owners below his rank
         * May dismiss owners, by refunding them their share
         */
        FULL,
    }
    
    
    @EmbeddedId
    private PlayerOwnershipId ownershipId;
    
    @Column(name = "PLAYER_ID")
    private final UUID player;
    private final String name;
    
    
//    @MapsId(value = "ownershipId")
    @ManyToOne(cascade = CascadeType.ALL)
    private Structure structure;
    
    private Type ownerType;
    
    /**
     * JPA Constructor.
     */
    protected PlayerOwnership() {
        this.player = null;
        this.name = null;
    }
    
    /**
     * Constructor.
     * @param structure The structure
     * @param player Whether the owner is a isPlayer or not
     */
    PlayerOwnership(Player player, Structure structure, Type ownerType) {
        this.structure = structure;
        this.player = player.getUniqueId();
        this.name = player.getName();
        this.ownershipId = new PlayerOwnershipId(structure.getId(), player.getUniqueId());
        this.ownerType = ownerType;
    }

    public Type getOwnerType() {
        return ownerType;
    }

    public String getName() {
        return name;
    }

    public UUID getPlayerUUID() {
        return player;
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.player != null ? this.player.hashCode() : 0);
        hash = 71 * hash + (this.structure != null ? this.structure.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerOwnership other = (PlayerOwnership) obj;
        if (this.player != other.player && (this.player == null || !this.player.equals(other.player))) {
            return false;
        }
        if (this.structure != other.structure && (this.structure == null || !this.structure.equals(other.structure))) {
            return false;
        }
        return true;
    }
 

    

    

    public Structure getStructure() {
        return structure;
    }

   
    
    
    
}
