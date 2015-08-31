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
package com.chingo247.backupapi.core.io.nbt;

import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.Vector2D;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class NBTChunk {

    private Vector2D relativePos;
    private Vector2D pos;

    private Tag chunkTag;

    public NBTChunk(Vector2D relativePos, Vector2D pos, Tag chunkTag) {
        this.relativePos = relativePos;
        this.pos = pos;
        this.chunkTag = chunkTag;
    }

    public NBTLevel getLevelTag() throws TagNotFoundException {
        Map<String, Tag> chunk = (Map) chunkTag.getValue();
        Tag levelTag = chunk.get("Level");
        if (levelTag == null) {
            throw new TagNotFoundException("Couldn't find tag 'Level'");
        }

        return new NBTLevel(this, levelTag);
    }

    public Vector2D getPos() {
        return pos;
    }

    public Vector2D getRelativePos() {
        return relativePos;
    }

}
