/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.commons.platforms.bukkit;

import com.chingo247.settlercraft.commons.core.ILocation;
import com.chingo247.settlercraft.commons.core.IWorld;

/**
 *
 * @author Chingo
 */
public class BukkitLocation implements ILocation{
    
    private final BukkitWorld world;
    
    private final int x;
    private final int y;
    private final int z;

    public BukkitLocation(BukkitWorld world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int getBlockX() {
        return x;
    }

    @Override
    public int getBlockY() {
        return y;
    }

    @Override
    public int getBlockZ() {
        return z;
    }

    @Override
    public IWorld getWorld() {
        return world;
    }
    
    
    
}
