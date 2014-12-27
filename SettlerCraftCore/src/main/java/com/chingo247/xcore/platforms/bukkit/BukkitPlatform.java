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

import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IConsole;
import com.chingo247.xcore.core.IServer;
import java.io.File;
import org.bukkit.Server;

/**
 *
 * @author Chingo
 */
public class BukkitPlatform extends APlatform {
    
    private final BukkitServer bukkitServer;
    
    public BukkitPlatform(Server server) {
        this(new BukkitServer(server));
    }

    public BukkitPlatform(BukkitServer bukkitServer) {
        this.bukkitServer = bukkitServer;
    }

    @Override
    public IServer getServer() {
        return bukkitServer;
    }

    @Override
    public IConsole getConsole() {
        return bukkitServer.getConsole();
    }

    @Override
    public File getPluginsFolder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}