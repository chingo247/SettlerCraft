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
package com.chingo247.xcore.platforms.bukkit;

import com.chingo247.xcore.core.IPlayer;
import com.chingo247.xcore.core.IServer;
import com.chingo247.xcore.core.IWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class BukkitServer implements IServer {
    
    private final Server server;
    private final BukkitConsole console;
    

    public BukkitServer(Server server) {
        this.server = server;
        this.console = new BukkitConsole(server.getConsoleSender());
    }

    public BukkitConsole getConsole() {
        return console;
    }
    
    @Override
    public List<IPlayer> getPlayers() {
        final List<IPlayer> players = new ArrayList<>(server.getOnlinePlayers().length);
        final List<Player> bukkitPlayers = Arrays.asList(server.getOnlinePlayers());
        for(Player p : bukkitPlayers) {
            players.add(new BukkitPlayer(p, this));
        }
        return players;
    }

    @Override
    public List<IWorld> getWorlds() {
       final List<IWorld> worlds = new ArrayList<>(server.getOnlinePlayers().length);
        final List<World> bukkitWorlds = server.getWorlds();
        for(World w : bukkitWorlds) {
            worlds.add(new BukkitWorld(w));
        }
        return worlds;
    }

    @Override
    public IPlayer getPlayer(UUID playerUUID) {
        return new BukkitPlayer(server.getPlayer(playerUUID), this);
    }

    @Override
    public IWorld getWorld(String world) {
        return new BukkitWorld(server.getWorld(world));
    }

    @Override
    public IWorld getWorld(UUID worldUUID) {
        return new BukkitWorld(server.getWorld(worldUUID));
    }

    
    
}
