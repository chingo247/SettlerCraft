/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.construction.backup.event;

import com.chingo247.structureapi.construction.backup.IBackupEntry;

/**
 *
 * @author Chingo
 */
public class BackupEntryEvent {
    
    private IBackupEntry backupEntry;

    public BackupEntryEvent(IBackupEntry backupEntry) {
        this.backupEntry = backupEntry;
    }

    public IBackupEntry getBackupEntry() {
        return backupEntry;
    }
    
}
