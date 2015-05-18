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

import com.chingo247.xplatform.core.IPlugin;
import com.google.common.base.Preconditions;
import java.io.File;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class BukkitPlugin implements IPlugin {
    
    private final Plugin plugin;

    public BukkitPlugin(Plugin plugin) {
        Preconditions.checkNotNull(plugin);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
    
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }
    
    
    
}