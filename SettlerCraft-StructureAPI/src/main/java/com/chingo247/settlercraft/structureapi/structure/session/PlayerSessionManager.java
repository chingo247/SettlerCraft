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

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.PlayerLoginEvent;
import com.chingo247.settlercraft.core.event.PlayerLogoutEvent;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.entity.Player;
import java.util.Map;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Chingo
 */
public class PlayerSessionManager {
    
    private final Map<UUID, PlayerSessionImpl> sessions;
    private final GraphDatabaseService graph;
    private final IStructureDAO structureDAO;
    private final APlatform platform;
    private static PlayerSessionManager instance;

    private PlayerSessionManager() {
        this.sessions = Maps.newHashMap();
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.structureDAO = new StructureDAO(graph);
    }
    
    public static PlayerSessionManager getInstance() {
        if(instance == null) {
            instance = new PlayerSessionManager();
        }
        return instance;
    }
    
    
    public PlayerSession getSession(UUID player) {
        synchronized(sessions) {
            return sessions.get(player);
        }
    }
    
    
    @Subscribe
    public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
        IPlayer player = playerLoginEvent.getPlayer();
        Player ply = SettlerCraft.getInstance().getPlayer(player.getUniqueId());
        synchronized(sessions) {
            sessions.put(player.getUniqueId(), new PlayerSessionImpl(ply, platform, graph, structureDAO));
        }
    }
    
    @Subscribe
    public void onPlayerLogout(PlayerLogoutEvent playerLogoutEvent) {
        IPlayer player = playerLogoutEvent.getPlayer();
        synchronized(sessions) {
            sessions.remove(player.getUniqueId());
        }
    }
    
}
