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
package com.chingo247.settlercraft.core.model.world;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IWorldRepository<T extends WorldNode> {
    
    /**
     * Finds a BaseWorld by UUID
     * @param worldUUID The worlds UUID
     * @return The BaseWorld
     */
    public T findByUUID(UUID worldUUID);

    /**
     * Adds a world if not already added
     * @param worldName The name of the world
     * @param worldUUID The worldUUID
     * @return The world that has been created or the world that already existed with the same UUID
     */
    public  T addOrGet(String worldName, UUID worldUUID);
    
}
