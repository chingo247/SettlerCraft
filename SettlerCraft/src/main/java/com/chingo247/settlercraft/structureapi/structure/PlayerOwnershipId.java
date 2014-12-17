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

package com.chingo247.settlercraft.structureapi.structure;

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
