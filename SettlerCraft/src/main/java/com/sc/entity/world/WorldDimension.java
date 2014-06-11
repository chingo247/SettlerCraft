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
package com.sc.entity.world;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.util.SCWorldEditUtil;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import java.io.Serializable;
import javax.persistence.Column;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class WorldDimension implements Serializable {

    @NotNull
    @Column(name = "dim_world")
    protected String world;

    @NotNull
    @Column(name = "startX")
    private int minX;

    @NotNull
    @Column(name = "startY")
    private int minY;

    @NotNull
    @Column(name = "startZ")
    private int minZ;

    @NotNull
    @Column(name = "endX")
    private int maxX;

    @NotNull
    @Column(name = "endY")
    private int maxY;

    @NotNull
    @Column(name = "endZ")
    private int maxZ;

    /**
     * JPA Constructor.
     */
    protected WorldDimension() {
    }

    public WorldDimension(Location start, Location end) {
        Preconditions.checkArgument(start.getWorld().getName().equals(end.getWorld().getName()));
        this.minX = Math.min(start.getPosition().getBlockX(), end.getPosition().getBlockX());
        this.minY = Math.min(start.getPosition().getBlockY(), end.getPosition().getBlockY());
        this.minZ = Math.min(start.getPosition().getBlockZ(), end.getPosition().getBlockZ());
        this.maxX = Math.max(start.getPosition().getBlockX(), end.getPosition().getBlockX());
        this.maxY = Math.max(start.getPosition().getBlockY(), end.getPosition().getBlockY());
        this.maxZ = Math.max(start.getPosition().getBlockZ(), end.getPosition().getBlockZ());
        this.world = start.getWorld().getName();
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    
    public LocalWorld getLocalWorld() {
        return getMin().getWorld();
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    

    public Location getMin() {
        return new Location(SCWorldEditUtil.getLocalWorld(world), new Vector(minX, minY, minZ));
    }

    public Location getMax() {
        return new Location(SCWorldEditUtil.getLocalWorld(world), new Vector(maxX, maxY, maxZ));
    }

}
