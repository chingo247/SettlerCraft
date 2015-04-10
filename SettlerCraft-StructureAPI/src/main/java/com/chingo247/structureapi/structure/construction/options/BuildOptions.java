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
package com.chingo247.structureapi.structure.construction.options;

/**
 *
 * @author Chingo
 */
public final class BuildOptions extends ConstructionOptions {

    private boolean noAir;
    private boolean placeFence ;
    private int xLayer = 16;
    private int yLayer = 16;
    private int zLayer = 16;

    
    public BuildOptions(boolean noAir) {
        this(noAir, true);
        
    }

   
    
    public BuildOptions(boolean noAir, boolean placeFence) {
        this.noAir = noAir;
        this.placeFence = placeFence;
    }

    public boolean noAir() {
        return noAir;
    }

    public void setNoAir(boolean noAir) {
        this.noAir = noAir;
    }

    public boolean isPlaceFence() {
        return placeFence;
    }

    public void setPlaceFence(boolean placeFence) {
        this.placeFence = placeFence;
    }
    
}
