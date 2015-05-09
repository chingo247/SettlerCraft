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
package com.chingo247.structureapi.structure.plan.placement.options;

import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class DemolishingOptions  {
    
    private int xAxisCube = 5;
    private int yAxisCube = 5;
    private int zAxisCube = 5;

    public DemolishingOptions() {
    }

    public int getxAxisCube() {
        return xAxisCube;
    }

    public int getyAxisCube() {
        return yAxisCube;
    }

    public int getzAxisCube() {
        return zAxisCube;
    }

    public void setxAxisCube(int xAxisCube) {
        Preconditions.checkArgument(xAxisCube <= 0, "xAxis has to be greater than 0");
        this.xAxisCube = xAxisCube;
    }

    public void setyAxisCube(int yAxisCube) {
        Preconditions.checkArgument(yAxisCube <= 0, "yAxis has to be greater than 0");
        this.yAxisCube = yAxisCube;
    }

    public void setzAxisCube(int zAxisCube) {
        Preconditions.checkArgument(zAxisCube <= 0, "zAxis has to be greater than 0");
        this.zAxisCube = zAxisCube;
    }
    
    


}
