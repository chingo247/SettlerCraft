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
package com.chingo247.settlercraft.structure.rollback;

import com.chingo247.settlercraft.structure.persistence.orientdb.document.BlockDatabase;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import org.PrimeSoft.blocksHub.BlocksHub;
import org.PrimeSoft.blocksHub.Logic;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BlockLogger {
    
    private final Timer timer;
    private BlockLoggerTask task;
    private final BlockDatabase database = new BlockDatabase();

    public BlockLogger() {
        BlocksHub b = (BlocksHub) Bukkit.getPluginManager().getPlugin("BlocksHub");
        this.timer = new Timer("SettlerCraft-BlockLogger");
        
        try {
            task = new BlockLoggerTask(database);
            
            Field mLogicField = b.getClass().getDeclaredField("m_logic");
            mLogicField.setAccessible(true);
            Logic logic = (Logic)mLogicField.get(b);
          
            Field mLoggersField = logic.getClass().getDeclaredField("m_loggers");
            mLoggersField.setAccessible(true);
            List list = (List) mLoggersField.get(logic);
            list.add(task);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(this.getClass()).error(ex);
        } 
    }
    
    public void start() {
        timer.scheduleAtFixedRate(task, 0, 10000);
    }
    
    
    
}
