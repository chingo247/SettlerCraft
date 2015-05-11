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
package com.chingo247.settlercraft.structureapi.structure.session;

import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class PlayerSessionImpl implements PlayerSession{
    
    private final Player player;
    private final IStructureDAO structureDAO;
    private final GraphDatabaseService graph;
    private final APlatform platform;
    private final IColors colors;

    private Structure currentStructure;
    private PlaceOptions currentPlaceOptions;
    private DemolishingOptions currentDemolishingOptions;
    
    
    PlayerSessionImpl(Player player,  APlatform platform, GraphDatabaseService graph, IStructureDAO structureDAO) {
        Preconditions.checkNotNull(player, "Player was null");
        Preconditions.checkNotNull(graph, "GraphDatabaseService was null");
        Preconditions.checkNotNull(structureDAO, "StructureDAO was null");
        Preconditions.checkNotNull(platform, "APlatform was null");
        
        this.platform = platform;
        this.player = player;
        this.structureDAO = structureDAO;
        this.graph = graph;
        this.colors = platform.getChatColors();
    }
    
    

    @Override
    public Structure getSelectedStructure() {
       return currentStructure;
    }

    @Override
    public void selectStructure(World w, long id) {
        Structure structure = null;
        try(Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(w, id);
            if(structureNode != null) {
                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            }
            tx.success();
        }
        if(structure == null) {
            player.printError("Couldn't find structure with id #" + id);
        } else {
            player.print("You've selected #" + colors.gold() + id + " " + colors.blue() + structure.getName());
            currentStructure = structure;
        }
    }
    
    @Override
    public void selectStructure(Structure structure) {
        Preconditions.checkNotNull(structure, "structure was null");
        player.print("You've selected #" + colors.gold() + structure.getId() + " " + colors.blue() + structure.getName());
        currentStructure = structure;
    }

    @Override
    public PlaceOptions getPlaceOptions() {
        return currentPlaceOptions;
    }

    @Override
    public DemolishingOptions getDemolishOptions() {
        return currentDemolishingOptions;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void reset() {
        deselectStructure();
        currentPlaceOptions = null;
        currentDemolishingOptions = null;
        player.print("Session has been reset");
    }

    @Override
    public void deselectStructure() {
        if(currentStructure != null) {
            player.print("Cleared #" + colors.gold() + " " + colors.reset() + " from selection");
        }
        currentStructure = null;
    }

    

   
    
}
