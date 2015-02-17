
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
package com.chingo247.settlercraft.entities;

import com.chingo247.settlercraft.structure.persistence.legacy.Structure;
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
public class StructurePlayerMemberEntity implements Serializable {

    @EmbeddedId
    private StructurePlayerMemberId playerMembershipId;
    
    @Column(name = "PLAYER_ID")
    private final UUID uuid;
    
    private String name;
    
//    @MapsId(value = "playerMembershipId")
    @ManyToOne(cascade = CascadeType.ALL)
    private StructureEntity structure;

    /**
     * JPA Constructor.
     */
    protected StructurePlayerMemberEntity() {
        this.uuid = null;
    }

    /**
     * Constructor.
     * @param name The name of the owner
     * @param player Whether the owner is a isPlayer or not
     */
    StructurePlayerMemberEntity(Player player, StructureEntity structure) {
        this.structure = structure;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.playerMembershipId = new StructurePlayerMemberId(structure.getId(), player.getUniqueId());
    }

   

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
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
        final StructurePlayerMemberEntity other = (StructurePlayerMemberEntity) obj;
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if (this.structure != other.structure && (this.structure == null || !this.structure.equals(other.structure))) {
            return false;
        }
        return true;
    }

}
