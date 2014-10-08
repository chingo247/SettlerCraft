/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.entities.structure;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class PlayerOwnershipId implements Serializable {
    
    private Long structure;
    private UUID player;

    public UUID getPlayer() {
        return player;
    }

   

    protected PlayerOwnershipId() {
    }

    PlayerOwnershipId(Long structureId, UUID player) {
        this.structure = structureId;
        this.player = player;
    }
    
    
    
}
