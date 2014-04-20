/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.util.Ticks;

/**
 *
 * @author Chingo
 */
public class AnimatedFrameBuilder {

    private final Structure structure;
    private final int delay;
    private static final int defaultDelay = 5 * Ticks.ONE_SECOND;

    AnimatedFrameBuilder(Structure structure, int delay) {
        this.structure = structure;
        this.delay = delay;
    }

    AnimatedFrameBuilder(Structure structure) {
        this.structure = structure;
        this.delay = defaultDelay;
    }

    /**
     * Build frame for target structure
     */
    public void construct() {
        construct(FrameStrategy.DEFAULT);
    }

    /**
     * Contructs a frame for this structure
     *
     * @param strategy The strategy to place this frame
     */
    public void construct(FrameStrategy strategy) {
        switch (strategy) {
            case DEFAULT:
                placeDefaultAnimatedFrame();
            case FANCY:
                placeFancyAnimatedFrame();
            default:
                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
        }
    }

    private void placeDefaultAnimatedFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void placeFancyAnimatedFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
