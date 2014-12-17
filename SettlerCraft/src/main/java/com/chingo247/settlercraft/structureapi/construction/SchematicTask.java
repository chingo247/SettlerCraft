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
package com.chingo247.settlercraft.structureapi.construction;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class SchematicTask extends SettlerCraftTask {
    
    private final File schematic;
    private static final String SCHEMATIC_LOAD_FAIL = "Failed to load schematic";
    private CuboidClipboard clipboard = null;

    public SchematicTask(ConstructionTaskManager handler, File schematic, long taskid) {
        super(handler, taskid);
        this.schematic = schematic;
    }

    @Override
    public void run() {
        try {
            clipboard = SchematicFormat.MCEDIT.load(schematic);
        } catch (IOException | DataException ex) {
            constructionHandler.fail(this, SCHEMATIC_LOAD_FAIL);
            Logger.getLogger(SchematicTask.class.getName()).log(Level.SEVERE, SCHEMATIC_LOAD_FAIL + " for: " + schematic.getAbsolutePath(), ex); // Warn Console!
        }
    }

   
    protected CuboidClipboard getSchematic() {
        return clipboard;
    }
    
    
    
    
    
}
