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
package com.chingo247.backupapi.core;

import com.chingo247.backupapi.core.exception.BackupAPIException;
import com.chingo247.backupapi.core.exception.BackupException;
import com.chingo247.backupapi.core.io.IWorldPartSnapshot;
import com.chingo247.backupapi.core.io.SnapshotReader;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class BackupAPI implements IBackupAPI {
    
    private IBackupMaker backupMaker;
    private IChunkManager chunkManager;
    private SnapshotReader reader;
    private static BackupAPI instance;

    private BackupAPI() {
        this.reader = new SnapshotReader();
    }
    
    public static IBackupAPI getInstance() {
        if(instance == null) {
            instance = new BackupAPI();
        }
        return instance;
    }

    @Override
    public IChunkManager getChunkManager() {
        return chunkManager;
    }
    
    public void registerBackupManager(IBackupMaker backupMaker) throws BackupAPIException {
        if(this.backupMaker != null) {
            throw new BackupAPIException("Already registered a backupMaker");
        }
        this.backupMaker = backupMaker;
        AsyncEventManager.getInstance().register(backupMaker);
    }
    
    public void registerChunkManager(IChunkManager chunkManager) throws BackupAPIException {
        if(this.chunkManager != null) {
            throw new BackupAPIException("Already registered a chunkManager");
        }
        this.chunkManager = chunkManager;
    }

    @Override
    public IBackupEntry createBackup(World world, CuboidRegion region, File destinationFile) throws BackupException {
        return backupMaker.createBackup(world, region, destinationFile);
    }

    @Override
    public IWorldPartSnapshot readBackup(File backupFile) throws IOException {
        return reader.read(backupFile);
    }

    @Override
    public IBackupEntry createBackup(UUID uuid, World world, CuboidRegion region, File destinationFile) throws BackupException {
        return backupMaker.createBackup(uuid, world, region, destinationFile);
    }

    @Override
    public void cancel(IBackupEntry entry) {
        entry.cancel();
    }
    
}
