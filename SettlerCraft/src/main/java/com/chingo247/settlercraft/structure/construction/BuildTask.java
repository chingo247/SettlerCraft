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
package com.chingo247.settlercraft.structure.construction;

import com.chingo247.settlercraft.structure.construction.options.BuildOptions;
import com.chingo247.settlercraft.structure.construction.worldedit.ConstructionClipboard;
import com.chingo247.settlercraft.util.SchematicUtil;
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
public class BuildTask extends ConstructionTask {
    
    private final BuildOptions options;

    BuildTask(ConstructionTaskManager constructionHandler, long taskId, File schematic, Player tasker, UUID uuid, World world, Vector pos, Direction direction, BuildOptions options) {
        super(constructionHandler, taskId, schematic, tasker, uuid, world, pos, direction);
        this.options = options;
    }

    @Override
    public void run() {
        super.run();
        CuboidClipboard clipboard = getClipboard();
        if (clipboard != null) {
            SchematicUtil.align(clipboard, direction);
            ConstructionClipboard constructionClipboard = new ConstructionClipboard(clipboard, options);
            doTask(constructionClipboard, position, options.noAir(), false);
        }
    }
    
    
    
}
