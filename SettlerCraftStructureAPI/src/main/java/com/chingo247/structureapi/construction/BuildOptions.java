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
public class BuildOptions extends ConstructionOptions {

    
    private boolean noAir;
    private boolean placeFence ;

    
    public BuildOptions(boolean noAir) {
        this(noAir, true);
        
    }
    
    public BuildOptions(boolean noAir, boolean placeFence) {
        this.noAir = noAir;
        this.placeFence = placeFence;
    }

    public boolean isNoAir() {
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
