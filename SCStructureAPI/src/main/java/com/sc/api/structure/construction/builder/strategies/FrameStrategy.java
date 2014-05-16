/*
 * Copyright (C) 2014 Chingo
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

package com.sc.api.structure.construction.builder.strategies;

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
     *//**
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
    ADVANCED
}
