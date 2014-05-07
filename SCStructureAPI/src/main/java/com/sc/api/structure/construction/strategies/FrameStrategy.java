/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.strategies;

/**
 *
 * @author Chingo
 */
public enum FrameStrategy {

    /**
     * Just place the frame instantly with using edge of this structure
     * (width,length,height)
     * @deprecated Uses far more resources/ticks than fancy strategy in most cases
     * which has a huge impact on the performance
     */
    @Deprecated
    SIMPLE,
    /**
     * Will try to build the frame using a more complex algorithm
     */
    FANCY
}
