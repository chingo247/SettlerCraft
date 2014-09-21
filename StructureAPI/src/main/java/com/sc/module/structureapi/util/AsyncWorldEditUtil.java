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
package com.sc.module.structureapi.util;

import com.sk89q.worldedit.WorldEdit;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;


/**
 *
 * @author Chingo
 */
public class AsyncWorldEditUtil {
    
    private AsyncWorldEditUtil(){}

    public static AsyncWorldEditMain getAsyncWorldEditPlugin() {
        return AsyncWorldEditMain.getInstance();
    }

    public static BlockPlacer getBlockPlacer() {
        return getAsyncWorldEditPlugin().getBlockPlacer();
    }

    public static AsyncEditSessionFactory getAsyncSessionFactory() {
        return new AsyncEditSessionFactory(AsyncWorldEditMain.getInstance(), WorldEdit.getInstance().getEventBus());
    }


    
   


}
