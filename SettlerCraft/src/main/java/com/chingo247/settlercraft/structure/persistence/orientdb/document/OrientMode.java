/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.persistence.orientdb.document;

/**
 *
 * @author Chingo
 */
public enum OrientMode {

    LOCAL("plocal"),
    MEMORY("memory"),
    SERVER("remote");

    final String mode;

    private OrientMode(String mode) {
        this.mode = mode;
    }

    /**
     * Gets the mode as String
     * @return The mode
     */
    public String getMode() {
        return mode;
    }
    
}
