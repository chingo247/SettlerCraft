/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.structureapi.construction;

/**
 *
 * @author Chingo
 */
class ConstructionOptions {
    
    private Pattern buildPattern = Pattern.createCompromisePattern();

    public ConstructionOptions() {
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
    
     public Pattern getBuildPattern() {
        return buildPattern;
    }
    
}
