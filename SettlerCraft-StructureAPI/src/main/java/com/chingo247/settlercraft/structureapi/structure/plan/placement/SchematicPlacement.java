
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
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.FastClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;

/**
 *
 * @author Chingo
 */
public class SchematicPlacement extends AbstractBlockPlacement<BuildOptions> implements FilePlacement<BuildOptions>, BlockPlacement<BuildOptions> {

    private final Schematic schematic;
    private FastClipboard clipboard;
    private CuboidRegion region;

    public SchematicPlacement(Schematic schematic) {
        this(schematic, Direction.EAST, Vector.ZERO);
    }

    public SchematicPlacement(Schematic schematic, Direction direction, Vector position) {
        super(direction, position, schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        this.schematic = schematic;
        this.clipboard = schematic.getClipboard();
        this.region = new CuboidRegion(Vector.ZERO, schematic.getSize());
    }

    public Schematic getSchematic() {
        return schematic;
    }

    @Override
    public String getTypeName() {
        return PlacementTypes.SCHEMATIC;
    }

    @Override
    public File[] getFiles() {
        return new File[]{schematic.getFile()};
    }

    @Override
    public BaseBlock getBlock(Vector position) {
        if(region.contains(position)) {
            BaseBlock b = clipboard.getBlock(position);
            return b;
        }
        return null;
    }

}
