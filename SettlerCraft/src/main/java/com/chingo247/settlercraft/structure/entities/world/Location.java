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
package com.chingo247.settlercraft.structure.entities.world;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
@Embeddable
public class Location implements Serializable {

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
    protected String world;
    
    @Column(updatable = false)
    private UUID worldUUID;

    public Location() {
    }

    public Location(World world, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world.getName();
        this.worldUUID = world.getUID();
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

    public UUID getWorldUUID() {
        return worldUUID;
    }

    


    

}
