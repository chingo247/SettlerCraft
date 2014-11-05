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
package com.chingo247.structureapi.bukkit;

import com.chingo247.structureapi.main.IConfigProvider;
import com.sk89q.worldguard.protection.flags.Flag;
import java.util.HashMap;

/**
 *
 * @author Chingo
 */
public interface IBukkitConfigProvider extends IConfigProvider {
    
    public boolean useDefaultHolograms();
    
    public boolean useHolgrams();
    
    public HashMap<Flag, Object> getDefaultRegionFlags();
}
