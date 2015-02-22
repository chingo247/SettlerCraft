
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.construction;

import com.chingo247.settlercraft.construction.options.DemolitionOptions;
import com.chingo247.settlercraft.construction.worldedit.DemolitionClipboard;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.chingo247.settlercraft.world.Direction;
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
    DemolitionTask(ConstructionTaskManager constructionHandler, long taskId, File schematic, Player player, UUID uuid, World world, Vector pos, Direction direction, DemolitionOptions options) {
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
