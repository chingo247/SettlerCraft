/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

/**
 *
 * @author Chingo
 */
public enum FRAME_STRATEGY {

    /**
     * Just place the frame instantly with using edge of this structure
     * (width,length,height)
     */
    DEFAULT,
    /**
     * Will try to build the frame using a more complex algorithm
     */
    FANCY
}
