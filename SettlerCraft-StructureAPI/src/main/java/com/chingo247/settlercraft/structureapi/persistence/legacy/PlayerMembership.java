
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
package com.chingo247.settlercraft.structureapi.persistence.legacy;

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
     * @param name The name of the owner
     * @param player Whether the owner is a isPlayer or not
     */
    @Deprecated
    PlayerMembership(Player player, Structure structure) {
        this.structure = structure;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.playerMembershipId = new PlayerMembershipId(structure.getId(), player.getUniqueId());
    }

    public String getName() {
        return name;
    }


    public UUID getUUID() {
        return uuid;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 29 * hash + (this.structure != null ? this.structure.hashCode() : 0);
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
        final PlayerMembership other = (PlayerMembership) obj;
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
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
