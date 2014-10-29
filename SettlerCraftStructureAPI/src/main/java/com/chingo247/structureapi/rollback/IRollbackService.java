/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.rollback;

import com.chingo247.structureapi.Structure;

/**
 *
 * @author Chingo
 */
public interface IRollbackService {
    
    public static String WORLD_EDIT_ACTION = "worldedit";
    
    public void rollback(Structure structure);
    
}
