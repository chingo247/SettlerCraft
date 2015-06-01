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
package com.chingo247.settlercraft.core.platforms.bukkit;

import com.chingo247.settlercraft.core.platforms.services.IPlayerProvider;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BKPlayerProvider implements IPlayerProvider{
    
    private final WorldEditPlugin worldEdit;

    public BKPlayerProvider() {
        this.worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null) {
            throw new RuntimeException("WorldEdit was not found!");
        }
    }
    
    @Override
    public Player getPlayer(UUID playerId) {
        org.bukkit.entity.Player player = Bukkit.getPlayer(playerId);
        return player != null ? worldEdit.wrapPlayer(Bukkit.getPlayer(playerId)) : null;
    }
    
}
