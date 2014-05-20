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

package com.sc.api.structure.construction.builder.async;

import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.model.Structure;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * All actions executed by this builder class will be saved
 * @author Chingo
 */
public class SCAsyncStructureBuilder {
    
    public static void place(Structure structure, ConstructionTask task, AsyncEditSession asyncEditSession, SCJobCallback callback) {
        StructureClipboard structureClipboard = new StructureClipboard(structure, task,  ConstructionStrategyType.LAYERED);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncEditSession.getPlayer(), structureClipboard);
        try {
            asyncCuboidClipboard.place(asyncEditSession, structure.getLocation().getPosition(), true, callback);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCAsyncStructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
