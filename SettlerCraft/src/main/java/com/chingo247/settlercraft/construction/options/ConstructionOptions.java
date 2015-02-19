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
package com.chingo247.settlercraft.construction.options;

/**
 *
 * @author Chingo
 */
public abstract class ConstructionOptions {
    
    private Pattern buildPattern = Pattern.createCompromisePattern();
    private int xLayer = 16;
    private int yLayer = -1;
    private int zLayer = 16;

    ConstructionOptions() {
    }
    
     public void setxLayer(int xLayer) {
        this.xLayer = xLayer;
    }

    public void setyLayer(int yLayer) {
        this.yLayer = yLayer;
    }

    public void setzLayer(int zLayer) {
        this.zLayer = zLayer;
    }

    public int getXCube() {
        return xLayer;
    }

    public int getYCube() {
        return yLayer;
    }

    public int getZCube() {
        return zLayer;
    }

     /**
     * Sets the pattern in which blocks are placed
     * @param buildPattern The pattern.
     * 
     * Use the Pattern factory methods to retrieve an instance of Pattern.
     * for example {@link Pattern#createPerformancePattern()}
     */
    public void setPattern(Pattern buildPattern) {
        this.buildPattern = buildPattern;
    }
    
    public Pattern getPattern() {
        return buildPattern;
    }
    
    
    
}
