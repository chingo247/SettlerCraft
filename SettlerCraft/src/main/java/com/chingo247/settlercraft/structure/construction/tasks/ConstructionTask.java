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
package com.chingo247.settlercraft.structure.construction.tasks;

import com.chingo247.settlercraft.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCAsyncClipboard;
import com.chingo247.settlercraft.structure.world.Direction;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class ConstructionTask extends StructureAPITask {

    private final SchematicTask schematicTask;
    protected final World world;
    protected final Vector position;
    protected final Direction direction;
    protected final Player tasker;
    protected final UUID uuid;
    
    

    protected ConstructionTask(ConstructionHandler constructionHandler, long taskId, File schematic, Player tasker, UUID uuid, World world, Vector pos, Direction direction) {
        super(constructionHandler, taskId);
        this.schematicTask = new SchematicTask(constructionHandler, schematic, taskId);
        this.world = world;
        this.position = pos;
        this.direction = direction;
        this.tasker = tasker;
        this.uuid = uuid;
    }

    protected void doTask(CuboidClipboard clipboard, Vector pos, boolean noAir, boolean demolishing) {
        AsyncEditSession editSession;
        if (tasker == null) {
            editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1);
        } else {
            editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1, tasker);
        }
        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
        SCAsyncClipboard asyncStructureClipboard = new SCAsyncClipboard(plyEntry, clipboard, taskID);
        try {
            asyncStructureClipboard.place(editSession, position, noAir, demolishing);
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(DemolitionTask.class.getName()).log(Level.SEVERE, null, ex); // Should never happen...
        }
    }

    @Override
    public void run() {
        schematicTask.run();
    }

    protected CuboidClipboard getClipboard() {
        return schematicTask.getSchematic();
    }

    protected Vector getPosition() {
        return position;
    }

    protected Direction getDirection() {
        return direction;
    }

    protected World getWorld() {
        return world;
    }

}
