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
package com.chingo247.settlercraft.core;

/**
 * Represents the basic directions NORTH, EAST, SOUTH, WEST
 * @author Chingo
 */
public enum Direction {
    NORTH(0,-180),
    EAST(1,-90),
    SOUTH(2,0),
    WEST(3,-270);

    private int id;
    private int rotation;
    
    private Direction(int direction, int rotation) {
        this.id = direction;
        this.rotation = rotation;
    }

    public int getDirectionId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }
    
    
    
    
    public static Direction match(int directionID) {
        switch(directionID) {
            case 0: return NORTH;
            case 1: return EAST;
            case 2: return SOUTH;
            case 3: return WEST;
            default: throw new AssertionError("Unreachable");
        }
    }
    
    
    
    
}
