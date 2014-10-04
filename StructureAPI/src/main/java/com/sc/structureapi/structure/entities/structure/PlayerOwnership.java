/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.structureapi.structure.entities.structure;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
@Entity
public class PlayerOwnership implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "PLAYER_ID")
    private final UUID player;
    private final String name;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private Structure structure;
    
    

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
    PlayerOwnership(Player player, Structure structure) {
        this.structure = structure;
        this.player = player.getUniqueId();
        this.name = player.getName();
    }

    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
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
