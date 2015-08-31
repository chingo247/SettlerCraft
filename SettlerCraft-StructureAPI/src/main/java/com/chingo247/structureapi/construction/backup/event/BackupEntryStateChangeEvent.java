/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.construction.backup.event;

import com.chingo247.structureapi.construction.backup.BackupState;
import com.chingo247.structureapi.construction.backup.IBackupEntry;

/**
 *
 * @author Chingo
 */
public class BackupEntryStateChangeEvent extends BackupEntryEvent {
    
    private BackupState oldState;

    public BackupEntryStateChangeEvent(IBackupEntry backupEntry, BackupState oldState) {
        super(backupEntry);
        this.oldState = oldState;
    }

    public BackupState getOldState() {
        return oldState;
    }
    
}
