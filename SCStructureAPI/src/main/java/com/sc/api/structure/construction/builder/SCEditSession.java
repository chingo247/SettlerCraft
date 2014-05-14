/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.construction.builder;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;

/**
 *
 * @author Chingo
 */
public class SCEditSession extends EditSession {
    
    private String sessionName;

    public SCEditSession(LocalWorld world, int maxBlocks, String sessionName) {
        super(world, maxBlocks);
    }
    
}
