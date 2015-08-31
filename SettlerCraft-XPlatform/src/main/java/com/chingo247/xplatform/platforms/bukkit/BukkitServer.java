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
package com.chingo247.xplatform.platforms.bukkit;

import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IScheduler;
import com.chingo247.xplatform.core.IServer;
import com.chingo247.xplatform.core.IWorld;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class BukkitServer implements IServer {
    
    final Server server;
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
            players.add(new BukkitPlayer(p));
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
        Player player = server.getPlayer(playerUUID);
        if(player == null) {
            return null;
        }
        
        return new BukkitPlayer(player);
    }

    @Override
    public IWorld getWorld(String world) {
        World w = server.getWorld(world);
        if(w == null) {
            return null;
        }
        return new BukkitWorld(w);
    }

    @Override
    public IWorld getWorld(UUID worldUUID) {
        World w = server.getWorld(worldUUID);
        if(w == null) {
            return null;
        }
        return new BukkitWorld(w);
    }

    @Override
    public IPlayer getPlayer(String player) {
        Player ply = Bukkit.getPlayer(player);
        return ply != null ? new BukkitPlayer(ply) : null;
    }

    @Override
    public IScheduler getScheduler(IPlugin plugin) {
        return new BukkitScheduler(server, ((BukkitPlugin) plugin).getPlugin());
    }

    @Override
    public IPlugin getPlugin(String plugin) {
        return new BukkitPlugin(server.getPluginManager().getPlugin(plugin));
    }

    @Override
    public File getWorldFolder(String world) {
        return new File(world);
    }

    @Override
    public File getWorldRegionFolder(String world) {
        return new File(getWorldFolder(world), "region");
    }

    @Override
    public void broadcast(String message) {
        server.broadcastMessage(message);
    }

    
    
}
