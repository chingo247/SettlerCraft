/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure;

import com.sc.module.structureapi.structure.Structure.State;

/**
 *
 * @author Chingo
 */
public interface StructureListener {
    
    abstract void onStateChanged(Structure structure, State newState);
    
    abstract void onCreate(Structure structure);
    
}
