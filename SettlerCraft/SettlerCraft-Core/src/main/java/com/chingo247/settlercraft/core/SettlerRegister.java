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
import com.chingo247.settlercraft.core.exception.SettlerException;
import com.chingo247.settlercraft.core.model.settler.SettlerRepository;
import com.chingo247.settlercraft.core.model.settler.Settler;
import com.chingo247.settlercraft.core.model.settler.SettlerNode;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Meant to register players as Settler for the very first time
 *
 * @author Chingo
 */
public class SettlerRegister {

    private static final Logger LOG = Logger.getLogger(SettlerRegister.class.getName());
    private final SettlerRepository settlerRepository;
    private final ExecutorService service;
    private final Set<UUID> cachedPlayers;
    private final GraphDatabaseService graph;
    private boolean checked = false;

    SettlerRegister(SettlerRepository settlerDAO, ExecutorService service, GraphDatabaseService graph) {
        Preconditions.checkNotNull(settlerDAO, "settlerDAO was null!");
        Preconditions.checkNotNull(service, "Executor was null!");
        this.settlerRepository = settlerDAO;
        this.service = service;
        this.cachedPlayers = Collections.synchronizedSet(new HashSet<UUID>());
        this.graph = graph;
    }

    @Subscribe
    public void onPlayerLogin(PlayerLoginEvent ple) {
        try {
            final IPlayer player = ple.getPlayer();
            if (!cachedPlayers.contains(player.getUniqueId())) { // Only check if not in cache
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        try (Transaction tx = graph.beginTx()) {
                            SettlerNode settlerNode = settlerRepository.findByUUID(player.getUniqueId());
                            if (settlerNode == null) {
                                settlerRepository.addSettler(player.getUniqueId(), player.getName());
                            } else {
                                settlerNode.setName(player.getName()); // update name..
                            }
                            cachedPlayers.add(player.getUniqueId());
                            tx.success();
                        } 
                    }
                });
            }
        } catch (Exception ex) { // Catch all... EventBus doesn't catch it... and will not throw error
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    

}
