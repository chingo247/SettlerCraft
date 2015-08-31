/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.core;

import com.chingo247.backupapi.core.snapshot.SnapshotReader;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.backup.IBackupAPI;
import com.chingo247.structureapi.construction.backup.IBackupEntry;
import com.chingo247.structureapi.construction.backup.IChunkManager;
import com.chingo247.structureapi.construction.backup.IWorldPartSnapshot;
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
    
    private BackupManager backupManager;
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
    
    public void registerBackupManager(BackupManager backupManager) {
        this.backupManager = backupManager;
        AsyncEventManager.getInstance().register(backupManager);
    }

    @Override
    public IBackupEntry createBackup(World world, CuboidRegion region, File destinationFile) {
        return backupManager.createBackup(world, region, destinationFile);
    }

    @Override
    public IWorldPartSnapshot readBackup(File backupFile) throws IOException {
        return reader.read(backupFile);
    }

    @Override
    public IBackupEntry createBackup(UUID uuid, World world, CuboidRegion region, File destinationFile) throws Exception {
        return backupManager.createBackup(uuid, world, region, destinationFile);
    }

    @Override
    public void cancel(IBackupEntry entry) {
        entry.cancel();
    }
    
}
