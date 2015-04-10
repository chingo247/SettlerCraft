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
package com.chingo247.proxyplatform.platforms.bukkit;

import com.chingo247.proxyplatform.core.ILocation;
import com.chingo247.proxyplatform.core.IWorld;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class BukkitWorld implements IWorld {
    
    private final World world;

    public BukkitWorld(World world) {
        this.world = world;
        
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public UUID getUUID() {
        return world.getUID();
    }

    @Override
    public int getMaxHeight() {
        return world.getMaxHeight();
    }
    
    @Override
    public ILocation getSpawn() {
        BukkitWorld w = new BukkitWorld(world);
        Location location = world.getSpawnLocation();
        return new BukkitLocation(w, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
}
