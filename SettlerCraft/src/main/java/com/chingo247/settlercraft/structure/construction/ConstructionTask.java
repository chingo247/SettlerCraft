
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
package com.chingo247.settlercraft.structure.construction;

import com.chingo247.settlercraft.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCAsyncClipboard;
import com.chingo247.settlercraft.world.Direction;
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
public class ConstructionTask extends SettlerCraftTask {

    private final SchematicTask schematicTask;
    protected final World world;
    protected final Vector position;
    protected final Direction direction;
    protected final Player tasker;
    protected final UUID uuid;
    
    

    protected ConstructionTask(ConstructionTaskManager constructionHandler, long taskId, File schematic, Player tasker, UUID uuid, World world, Vector pos, Direction direction) {
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
