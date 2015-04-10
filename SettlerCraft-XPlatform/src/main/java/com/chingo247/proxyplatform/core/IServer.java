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
package com.chingo247.proxyplatform.core;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IServer {
    
    public List<IPlayer> getPlayers();
    
    public List<IWorld> getWorlds();
    
    public IPlayer getPlayer(UUID playerUUID);
    
    public IWorld getWorld(String world);
    
    public IWorld getWorld(UUID worldUUID);
    
}
