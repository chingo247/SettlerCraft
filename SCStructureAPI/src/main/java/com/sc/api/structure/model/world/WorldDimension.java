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
package com.sc.api.structure.model.world;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.api.structure.util.plugins.WorldEditUtil;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import javax.persistence.Column;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class WorldDimension {
    
    @NotNull
    @Column(name = "world")
    protected String world;

    @NotNull
    @Column(name = "startX")
    private int startX;

    @NotNull
    @Column(name = "startY")
    private int startY;

    @NotNull
    @Column(name = "startZ")
    private int startZ;

    @NotNull
    @Column(name = "endX")
    private int endX;

    @NotNull
    @Column(name = "endY")
    private int endY;

    @NotNull
    @Column(name = "endZ")
    private int endZ;

    
    /**
     * JPA Constructor.
     */
    protected WorldDimension() {}

    public WorldDimension(Location start, Location end) {
        Preconditions.checkArgument(start.getWorld().getName().equals(end.getWorld().getName()));
        this.startX = Math.min(start.getPosition().getBlockX(), end.getPosition().getBlockX());
        this.startY = Math.min(start.getPosition().getBlockY(), end.getPosition().getBlockY());
        this.startZ = Math.min(start.getPosition().getBlockZ(), end.getPosition().getBlockZ());
        this.endX = Math.max(start.getPosition().getBlockX(), end.getPosition().getBlockX());
        this.endY = Math.max(start.getPosition().getBlockY(), end.getPosition().getBlockY());
        this.endZ = Math.max(start.getPosition().getBlockZ(), end.getPosition().getBlockZ());
        this.world = start.getWorld().getName();
    }
    
    public World getWorld() {
        return Bukkit.getWorld(world);
    } 
    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartZ() {
        return startZ;
    }

    public void setStartZ(int startZ) {
        this.startZ = startZ;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getEndZ() {
        return endZ;
    }

    public void setEndZ(int endZ) {
        this.endZ = endZ;
    }

    public Location getStart() {
        return new Location(WorldEditUtil.getLocalWorld(world), new Vector(startX, startY, startZ));
    }

    public Location getEnd() {
        return new Location(WorldEditUtil.getLocalWorld(world), new Vector(endX, endY, endZ));
    }
    
}
