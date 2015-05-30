/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
