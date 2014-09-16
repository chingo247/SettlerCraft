/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Chingo
 */
@Entity
public class PlayerOwnership implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    private final UUID uuid;
    @ManyToOne
    private Structure structure;
    
    

    /**
     * JPA Constructor.
     */
    protected PlayerOwnership() {
        this.uuid = null;
    }
    
    /**
     * Constructor.
     * @param name The name of the owner
     * @param uuid The uuid of the owner
     * @param player Wheter the owner is a isPlayer or not
     */
    PlayerOwnership(UUID player, Structure structure) {
        this.structure = structure;
        this.uuid = player;
    }

    
    public Long getId() {
        return id;
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
        final PlayerOwnership other = (PlayerOwnership) obj;

        if (!Objects.equals(this.uuid, other.uuid)) {
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
        hash = 29 * hash + Objects.hashCode(this.uuid);
        hash = 29 * hash + Objects.hashCode(this.structure);
        return hash;
    }

    public Structure getStructure() {
        return structure;
    }

   
    
    
    
}
