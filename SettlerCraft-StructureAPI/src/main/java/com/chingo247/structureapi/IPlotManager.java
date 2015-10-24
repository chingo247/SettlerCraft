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
package com.chingo247.structureapi;

import com.chingo247.structureapi.model.owner.OwnerType;
import com.chingo247.structureapi.model.plot.IPlot;
import com.chingo247.structureapi.model.plot.Plot;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IPlotManager<T extends IPlot> {
    
    void checkWorldRestrictions(CuboidRegion region) throws RestrictionException;
    
    /**
     * Creates a plot
     * @param region The region the plot will affect
     */
    T create(CuboidRegion region) throws PlotException, RestrictionException;
    
    /**
     * Creates a plot
     * @param region The region the plot will affect
     * @param owner The first owner of the plot
     */
    T create(CuboidRegion region, UUID owner) throws PlotException, RestrictionException;
    
    /**
     * Updates the ownership of a player to a plot, requires an active transaction
     * @param plot The plot
     * @param player The uuid of the player to update
     * @param type The type to set, if type is null the ownership will be removed
     */
    void updateOwnership(T plot, UUID player, OwnerType type);
    
    /**
     * Updates the ownerships multiple players to a plot, requires an active transaction
     * @param plot The plot
     * @param players The uuids of the players to update
     * @param type The type to set, if type is null the ownership will be removed
     */
    void updateOwnership(T plot, Iterable<UUID> players, OwnerType type);
    
    /**
     * Removes the ownership of a player to a plot, requires an active transaction
     * @param plot The plot
     * @param players The uuid of the player to remove
     */
    void removeOwnership(T plot, UUID player);
    
    /**
     * Removes the ownerships multiple players to a plot, requires an active transaction
     * @param plot The plot
     * @param players The uuids of the players to remove
     */
    void removeOwnership(T plot, Iterable<UUID> players);
    
}
