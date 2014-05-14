/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.construction.builder.async;

import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;

/**
 *
 * @author Chingo
 */
public interface SCJobCallback {
    void onJobAdded(BlockPlacerJobEntry entry);
//    void onJobComplete(BlockPlacerJobEntry entry);
    void onJobCanceled(BlockPlacerJobEntry entry);
}
