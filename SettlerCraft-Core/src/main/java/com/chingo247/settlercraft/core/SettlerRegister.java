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
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerRegister {

    private final SettlerDAO ownerRepository;
    private final ExecutorService service;
    private final Set<UUID> cached;
    private final GraphDatabaseService graph;

    SettlerRegister(SettlerDAO settlerDAO, ExecutorService service, GraphDatabaseService graph) {
        Preconditions.checkNotNull(settlerDAO, "settlerDAO was null!");
        Preconditions.checkNotNull(service, "Executor was null!");
        this.ownerRepository = settlerDAO;
        this.service = service;
        this.cached = Collections.synchronizedSet(new HashSet<UUID>());
        this.graph = graph;
    }

    @Subscribe
    public void onPlayerLogin(PlayerLoginEvent ple) {
        final IPlayer player = ple.getPlayer();
        if (!cached.contains(player.getUniqueId())) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    try(Transaction tx = graph.beginTx()) {
                        if (ownerRepository.find(player.getUniqueId()) == null) {
                            ownerRepository.addSettler(player.getName(), player.getUniqueId());
                        }
                        cached.add(player.getUniqueId());
                        tx.success();
                    }
                }
            });
        }
    }

}
