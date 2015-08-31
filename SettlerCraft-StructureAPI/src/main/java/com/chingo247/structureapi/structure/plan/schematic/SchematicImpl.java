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
package com.chingo247.structureapi.structure.plan.schematic;

import com.chingo247.settlercraft.core.util.XXHasher;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import com.chingo247.structureapi.structure.plan.exception.SchematicException;
import com.chingo247.structureapi.structure.plan.placement.SchematicPlacement;
import com.google.common.base.Preconditions;


/**
 *
 * @author Chingo
 */
public class SchematicImpl implements Schematic {

    private final File schematicFile;
    private final long xxhash;
    private final int width;
    private final int height;
    private final int length;

    protected SchematicImpl (File schematicFile, int width, int height, int length) {
        Preconditions.checkNotNull(schematicFile);
        Preconditions.checkArgument(schematicFile.exists());
        this.schematicFile = schematicFile;
        
        XXHasher hasher = new XXHasher();
        
        try {
            this.xxhash = hasher.hash64(schematicFile);
        } catch (IOException ex) {
            throw new SchematicException(ex);
        }
        
        this.width = width;
        this.height = height;
        this.length = length;
    }

    protected SchematicImpl(File schematicFile, FastClipboard clipboard) {
        this(schematicFile, clipboard.getWidth(), clipboard.getHeight(), clipboard.getLength());
    }

    @Override
    public File getFile() {
        return schematicFile;
    }

    @Override
    public long getHash() {
        return this.xxhash;
    }

    @Override
    public final FastClipboard getClipboard() {
        if (!schematicFile.exists()) {
            throw new RuntimeException("File: " + schematicFile.getAbsolutePath() + " doesn't exist");
        }
        try {
            return FastClipboard.read(schematicFile);
        } catch (IOException ex) {
            throw new SchematicException(ex);
        }
    }

    @Override
    public Vector getSize() {
        return new Vector(width, height, length);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public SchematicPlacement createPlacement() {
        return new SchematicPlacement(this, 0, Vector.ZERO); // Default settlercraft thinks schematics are stored in natural position
    }

}
