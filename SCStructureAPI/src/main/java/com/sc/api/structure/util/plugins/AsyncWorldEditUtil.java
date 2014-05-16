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

package com.sc.api.structure.util.plugins;

import static com.sc.api.structure.util.plugins.WorldEditUtil.getLocalWorld;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

/**
 *
 * @author Chingo
 */
public class AsyncWorldEditUtil {
    
    public static PluginMain getAsyncWorldEditPlugin() {
        return PluginMain.getInstance();
    }
    
    public static BlockPlacer getBlockPlacer() {
        return getAsyncWorldEditPlugin().getBlockPlacer();
    }
    
    public static AsyncEditSessionFactory getAsyncSessionFactory() {
        return new AsyncEditSessionFactory(getAsyncWorldEditPlugin());
    }

    public static AsyncEditSession createAsyncEditSession(Player issuer, int maxblocks) {
        return new AsyncEditSession(getAsyncSessionFactory(), getAsyncWorldEditPlugin(), issuer.getName(), getLocalWorld(issuer), maxblocks);
    }
    
 
    
}
