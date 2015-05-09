/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.structureapi.structure.plan.placement.options;

import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class PlaceOptions {

    private int xAxisCube = 16;
    private int yAxisCube = 16;
    private int zAxisCube = 16;

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

    // To be implemented
    // - Masks
    // - Ignore Air
    // - Shipish? replaces water with air, but only if the block underneath is not water
}
