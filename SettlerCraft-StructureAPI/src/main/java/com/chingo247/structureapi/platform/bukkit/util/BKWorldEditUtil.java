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
package com.chingo247.structureapi.platform.bukkit.util;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;

/**
 *
 * @author Chingo
 */
public class BKWorldEditUtil {
    
    private BKWorldEditUtil(){}

    public static LocalPlayer wrapPlayer(Player player) {
        return getWorldEditPlugin().wrapPlayer(player);
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return AsyncWorldEditMain.getWorldEdit(AsyncWorldEditMain.getInstance());
    }

}
