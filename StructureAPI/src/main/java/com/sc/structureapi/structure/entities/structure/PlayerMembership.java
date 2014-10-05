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
