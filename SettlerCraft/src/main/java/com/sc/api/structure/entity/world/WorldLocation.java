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
package com.sc.api.structure.entity.world;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.sc.util.SCWorldEditUtil;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
@Embeddable
public class WorldLocation implements Serializable {

    @NotNull
    @Column(name = "x")
    protected int x;

    @NotNull
    @Column(name = "y")
    protected int y;

    @NotNull
    @Column(name = "z")
    protected int z;

    @NotEmpty
    @NotNull
    @Column(name = "loc_world")
    protected String world;

    public WorldLocation() {
    }

    public WorldLocation(Location location) {
        this.x = location.getPosition().getBlockX();
        this.y = location.getPosition().getBlockY();
        this.z = location.getPosition().getBlockZ();
        this.world = location.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorldName() {
        return world;
    }
    
    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    
    public LocalWorld getLocalWorld() {
        return getLocation().getWorld();
    }
    

    public void setWorld(String world) {
        this.world = world;
    }

    public Location getLocation() {
        return new Location(SCWorldEditUtil.getLocalWorld(world), new Vector(x, y, z));
    }

}
