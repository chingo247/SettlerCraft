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

import com.chingo247.backupapi.core.exception.BackupException;
import com.chingo247.backupapi.core.io.IWorldPartSnapshot;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IBackupAPI {
    
    /**
     * Creates a backup of a region in a given world
     * @param uuid The uuid used as id for the {@link IBackupEntry}
     * @param world The world where the backup will be made
     * @param region The region of which a backup will be created
     * @param destinationFile The file to copy the backup to
     * @return The {@link IBackupEntry}
     * @throws com.chingo247.backupapi.core.exception.BackupException When an UUID is used that was already in use
     */
    public IBackupEntry createBackup(UUID uuid, World world, CuboidRegion region, File destinationFile) throws BackupException;
    
    /**
     * Creates a backup of a region in a given world
     * @param world The world where the backup will be made
     * @param region The region of which a backup will be created
     * @param destinationFile The file to copy the backup to
     * @return The {@link IBackupEntry}
     */
    public IBackupEntry createBackup(World world, CuboidRegion region, File destinationFile) throws BackupException;
    
    /**
     * Reads a backup from a file
     * @param backupFile The backup file to read
     * @return The {@link IWorldPartSnapshot}
     * @throws IOException 
     */
    IWorldPartSnapshot readBackup(File backupFile)  throws IOException;
    
    /**
     * Cancels a backup
     * @param entry The backup to cancel
     */
    void cancel(IBackupEntry entry);
    
    /**
     * Gets the chunkManager
     * @return The chunkManager
     */
    IChunkManager getChunkManager();
    
}
