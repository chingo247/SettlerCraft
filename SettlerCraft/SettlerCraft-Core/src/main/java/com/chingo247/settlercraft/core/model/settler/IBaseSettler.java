/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.core.model.settler;

import com.chingo247.settlercraft.core.model.IdentifiableNode;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IBaseSettler extends IdentifiableNode<UUID>{
    
    
    /**
     * The id assigned to this Settler
     * @return The id
     */
    Long getId();
    
    /**
     * The UUID, if this Settler was a player the UUID is equal to {@link com.sk89q.worldedit.entity.Player#getUniqueId()}
     * @return The UUID
     */
    @Override
    UUID getUniqueId();
    
    /**
     * The name of this Settler
     * @return The name of this Settler
     */
    String getName();
    
}
