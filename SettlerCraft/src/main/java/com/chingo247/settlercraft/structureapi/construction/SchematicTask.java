
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
