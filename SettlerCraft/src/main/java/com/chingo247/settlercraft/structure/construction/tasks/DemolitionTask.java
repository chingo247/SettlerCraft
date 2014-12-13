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
package com.chingo247.settlercraft.structure.construction.tasks;

import com.chingo247.settlercraft.structure.construction.DemolitionOptions;
import com.chingo247.settlercraft.structure.construction.worldedit.DemolitionClipboard;
import com.chingo247.settlercraft.structure.util.SchematicUtil;
import com.chingo247.settlercraft.structure.world.Direction;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class DemolitionTask extends ConstructionTask {

    private final DemolitionOptions options;

    /**
     *
     * @param constructionHandler The constructionHandler to report to when things go wrong...
     * @param taskId The taskId
     * @param player The player, may be null
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The location
     * @param direction The direction
     * @param options The DemolitionOption
     */
    DemolitionTask(ConstructionHandler constructionHandler, long taskId, File schematic, Player player, UUID uuid, World world, Vector pos, Direction direction, DemolitionOptions options) {
        super(constructionHandler, taskId, schematic, player, uuid , world, pos, direction);
        this.options = options;
    }

    @Override
    public void run() {
        super.run(); // Loads schematic
        CuboidClipboard clipboard = getClipboard();
        if (clipboard != null) {
            SchematicUtil.align(clipboard, direction);
            DemolitionClipboard demolitionClipboard = new DemolitionClipboard(clipboard, options);
            doTask(demolitionClipboard, position, false, true);
        }
    }

}
