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
package com.chingo247.settlercraft.core;

import com.chingo247.settlercraft.core.event.PlayerLoginEvent;
import com.chingo247.settlercraft.core.model.BaseSettlerRepository;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Meant to register players as Settler for the very first time
 * @author Chingo
 */
public class SettlerRegister {

    private final BaseSettlerRepository settlerRepository;
    private final ExecutorService service;
    private final Set<UUID> cachedPlayers;
    private final GraphDatabaseService graph;
    private boolean checked = false;

    SettlerRegister(BaseSettlerRepository settlerDAO, ExecutorService service, GraphDatabaseService graph) {
        Preconditions.checkNotNull(settlerDAO, "settlerDAO was null!");
        Preconditions.checkNotNull(service, "Executor was null!");
        this.settlerRepository = settlerDAO;
        this.service = service;
        this.cachedPlayers = Collections.synchronizedSet(new HashSet<UUID>());
        this.graph = graph;
    }

    @Subscribe
    public void onPlayerLogin(PlayerLoginEvent ple) {
        final IPlayer player = ple.getPlayer();
        if (!cachedPlayers.contains(player.getUniqueId())) { // Only check if not in cache
            service.submit(new Runnable() {
                @Override
                public void run() {
                    try(Transaction tx = graph.beginTx()) {
                        if (settlerRepository.findByUUID(player.getUniqueId()) == null) {
                            settlerRepository.addSettler(new PlayerSettler(player));
                        }
                        cachedPlayers.add(player.getUniqueId());
                        tx.success();
                    }
                }
            });
        }
    }
    
    private class PlayerSettler implements IBaseSettler {

        private final IPlayer player;
        
        public PlayerSettler(IPlayer player) {
            this.player = player;
        }
        
        
        

        @Override
        public Long getId() {
            return null;
        }

        @Override
        public UUID getUUID() {
            return player.getUniqueId();
        }

        @Override
        public String getName() {
            return player.getName();
        }

        @Override
        public Node getNode() {
            return null;
        }
        
    }
    
    

}
