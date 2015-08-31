/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.construction.backup;

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
    
    IBackupEntry createBackup(World world, CuboidRegion region, File destinationFile) throws Exception;
    
    IBackupEntry createBackup(UUID uuid, World world, CuboidRegion region, File destinationFile) throws Exception;
    
    IWorldPartSnapshot readBackup(File backupFile)  throws IOException;
    
    void cancel(IBackupEntry entry);
    
}
