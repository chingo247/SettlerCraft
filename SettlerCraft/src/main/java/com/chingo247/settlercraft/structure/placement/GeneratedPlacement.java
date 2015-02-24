
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structure.placement;

import com.chingo247.settlercraft.structure.plan.generators.Generator;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 * @param <T>
 * 
 */
public abstract class GeneratedPlacement<T extends Generator> extends Placement {
    
    protected final T generator;
    protected final Vector position;

    public GeneratedPlacement(T generator, Vector relativePosition) {
        Preconditions.checkNotNull(generator);
        this.generator =  generator;
        this.position = relativePosition;
    }

    public T getGenerator() {
        return generator;
    }
    
    @Override
    public Vector getRelativePosition() {
        return position;
    }

    @Override
    public void move(Vector offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
