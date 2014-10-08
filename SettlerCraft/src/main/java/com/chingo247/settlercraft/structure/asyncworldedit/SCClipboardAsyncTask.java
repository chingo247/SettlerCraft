/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
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
package com.chingo247.settlercraft.structure.asyncworldedit;

import com.chingo247.settlercraft.structure.construction.ConstructionCallback;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.UUID;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;

/**
 *
 * @author Chingo
 */
public abstract class SCClipboardAsyncTask extends SCBaseTask {

    /**
     * Clipboard
     */
    private final CuboidClipboard clipboard;
    
    private final ConstructionCallback callback;

    /**
     *
     * @param clipboard
     * @param editSession
     * @param player
     * @param commandName
     * @param blocksPlacer
     * @param job
     * @param callback
     */
    public SCClipboardAsyncTask(final CuboidClipboard clipboard, final EditSession editSession,
            final UUID player, final String commandName, BlockPlacer blocksPlacer,
            SCJobEntry job, ConstructionCallback callback) {
        super(editSession, player, commandName, blocksPlacer, job, callback);

        this.clipboard = clipboard;
        this.callback = callback;
    }

    @Override
    protected Object doRun() throws MaxChangedBlocksException {
        task(clipboard);

        return null;
    }

    @Override
    protected void doPostRun(Object result) {
//        if (ConfigProvider.isTalkative()) {
//            AsyncWorldEditMain.say(m_player, ChatColor.LIGHT_PURPLE + "Clipboard operation done.");
//        }
    }

    /**
     * Task to run
     *
     * @param clipboard
     * @throws com.sk89q.worldedit.MaxChangedBlocksException
     */
    public abstract void task(CuboidClipboard clipboard)
            throws MaxChangedBlocksException;
}