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
package com.sc.api.structure.progress;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import java.util.List;

/**
 *
 * @author Chingo
 */
public abstract class ConstructionStrategy {

    public List<Vector> getList(CuboidClipboard cliboard) {
        return getList(cliboard, true);
    }
    

    public abstract List<Vector> getList(CuboidClipboard cliboard, boolean noAir);

    protected boolean isLava(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    protected boolean isWater(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

}
