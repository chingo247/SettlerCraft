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
package com.chingo247.structureapi.construction;

import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.structureapi.structure.plan.placement.options.DemolitionOptions;
import com.sk89q.worldedit.EditSession;
import java.util.UUID;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public interface IConstructionManager {

    /**
     * Gets the default taskfactory
     * @return The default taskfactory
     */
    public IStructureTaskFactory getTaskFactory();

    /**
     * Gets the {@link ConstructionEntry} of the given structure.
     * @param structure The structure
     * @return The constructionEntry
     */
    public ConstructionEntry getEntry(Structure structure);
    
    /**
     * Removes a {@link ConstructionEntry} from the ConstructionManager
     * @param entry The entry to remove
     */
    public void remove(ConstructionEntry entry);

    /**
     * Stops all tasks scheduled for a specific construction entry
     * @param entry The entry
     * @param force Whether to use force, ignoring some checks
     * @throws ConstructionException 
     */
    public void stop(ConstructionEntry entry, boolean force) throws ConstructionException;

    /**
     * Stops all tasks scheduled for a specific construction entry
     * @param entry The entry
     * @param useForce Whether to use force, ignoring some checks
     * @throws ConstructionException 
     */
    public void stop(Structure entry, boolean useForce) throws ConstructionException;

    /**
     * Builds a structure
     * @param session The editSession to use
     * @param player The playerUUID or any other UUID, if the player UUID is used and BarAPI was enabled this player will see the construction status
     * @param entry The ConstructionEntry
     * @param assigner The TaskAssigner
     * @param options The options to use
     * @throws ConstructionException 
     */
    public void build(AsyncEditSession session, UUID player, ConstructionEntry entry, IBuildTaskAssigner assigner, BuildOptions options) throws ConstructionException;

    /**
     * Builds a structure
     * @param session The editSession to use
     * @param player The playerUUID or any other UUID, if the player UUID is used and BarAPI was enabled this player will see the construction status
     * @param structure The Structure
     * @param assigner The TaskAssigner
     * @param options The options to use
     * @throws ConstructionException 
     */
    public void build(AsyncEditSession session, UUID player, Structure structure, IBuildTaskAssigner assigner, BuildOptions options) throws ConstructionException;

    /**
     * Demolishes a structure
     * @param session The editSession to use
     * @param player The playerUUID or any other UUID, if the player UUID is used and BarAPI was enabled this player will see the construction status
     * @param entry The ConstructionEntry
     * @param assigner The TaskAssigner
     * @param options The options to use
     * @throws ConstructionException 
     */
    public void demolish(AsyncEditSession session, UUID player, ConstructionEntry entry, IDemolitionTaskAssigner assigner, DemolitionOptions options) throws ConstructionException;

    /**
     * Demolishes a structure
     * @param session The editSession to use
     * @param player The playerUUID or any other UUID, if the player UUID is used and BarAPI was enabled this player will see the construction status
     * @param structure The Structure
     * @param assigner The TaskAssigner
     * @param options The options to use
     * @throws ConstructionException 
     */
    public void demolish(AsyncEditSession session, UUID player, Structure structure, IDemolitionTaskAssigner assigner, DemolitionOptions options) throws ConstructionException;

}
