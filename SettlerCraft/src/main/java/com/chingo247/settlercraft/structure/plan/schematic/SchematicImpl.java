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
package com.chingo247.settlercraft.structure.plan.schematic;

import com.chingo247.settlercraft.model.world.Direction;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import com.chingo247.settlercraft.structure.plan.exception.SchematicException;
import com.chingo247.settlercraft.structure.placement.SchematicPlacement;
import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class SchematicImpl implements Schematic {

    private final File schematicFile;
    private final long id;
    private final int width;
    private final int height;
    private final int length;

    

    protected SchematicImpl(File schematicFile, CuboidClipboard clipboard) {
        Preconditions.checkNotNull(schematicFile);
        Preconditions.checkArgument(schematicFile.exists());
        this.schematicFile = schematicFile;
        try {
            this.id = FileUtils.checksumCRC32(schematicFile);
        } catch (IOException ex) {
            throw new SchematicException(ex);
        }
        this.width = clipboard.getWidth();
        this.height = clipboard.getHeight();
        this.length = clipboard.getLength();
    }

    @Override
    public File getFile() {
        return schematicFile;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public final CuboidClipboard getClipboard() {
        if (!schematicFile.exists()) {
            throw new RuntimeException("File: " + schematicFile.getAbsolutePath() + " doesn't exist");
        }
        try {
            return SchematicFormat.getFormat(schematicFile).load(schematicFile);
        } catch (IOException | DataException ex) {
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
        return new SchematicPlacement(this, Direction.EAST, Vector.ZERO);
    }

}
