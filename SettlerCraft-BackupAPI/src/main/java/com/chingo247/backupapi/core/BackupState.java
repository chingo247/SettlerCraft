/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.core;

/**
 *
 * @author Chingo
 */
public enum BackupState {
    /*
     * Waiting for processing
     */
    WAITING,
    /*
     * Writing chunks from memory to disk
     */
    SAVING_CHUNKS,
    /*
     * Copying data (Chunks or ChunkSections)
     */
    COPYING_DATA,
    /*
     * Finished
     */
    COMPLETE,
    /*
     * Failed
     */
    FAILED
}
