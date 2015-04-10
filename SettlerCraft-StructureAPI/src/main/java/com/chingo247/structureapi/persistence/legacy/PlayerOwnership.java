
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

package com.chingo247.structureapi.persistence.legacy;

import com.sk89q.worldedit.entity.Player;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author Chingo
 */
@Entity
@Deprecated
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
