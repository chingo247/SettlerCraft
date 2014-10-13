/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerOwnership other = (PlayerOwnership) obj;

        if (!Objects.equals(this.player, other.player)) {
            return false;
        }
        if (!Objects.equals(this.structure, other.structure)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.player);
        hash = 29 * hash + Objects.hashCode(this.structure);
        return hash;
    }

    public Structure getStructure() {
        return structure;
    }

   
    
    
    
}
