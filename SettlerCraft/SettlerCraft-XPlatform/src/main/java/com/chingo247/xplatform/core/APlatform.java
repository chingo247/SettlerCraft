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
package com.chingo247.xplatform.core;

import java.io.File;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class APlatform {
    
    public abstract IServer getServer();
    
    public abstract ICommandSender getConsole();
    
    public abstract File getPluginsFolder();
    
    public abstract AItemStack createItemStack(int material);
    
    public abstract IColors getChatColors();
    
    public IPlayer getPlayer(UUID player) {
        return getServer().getPlayer(player);
    }
    
    public IPlayer getPlayer(String player) {
        return getServer().getPlayer(player);
    }
    
    public abstract AInventory createInventory(String title, int slots);
    
}
