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
package com.chingo247.settlercraft.structureapi.structure;

/**
 *
 * @author Chingo
 */
public enum ConstructionStatus {
    ON_HOLD(0,0),
    STOPPED(2,0),
    PLACING_FENCE(7,1),
    QUEUED(5,2),
    BUILDING(1,3),
    
    DEMOLISHING(3,3),
    COMPLETED(4,4),
    
    REMOVED(6,-1);

    private final int statusId;
    private final int phase;
    
    private ConstructionStatus(int statusId, int phase) {
        this.statusId = statusId;
        this.phase = phase;
    }

    public int getPhase() {
        return phase;
    }
    
    public int getStatusId() {
        return statusId;
    }
    
    
    public static ConstructionStatus match(int statusId) {
        switch(statusId) {
            case 0: return ON_HOLD;
            case 1: return BUILDING;
            case 2: return STOPPED;
            case 3: return DEMOLISHING;
            case 4: return COMPLETED;
            case 5: return QUEUED;
            case 6: return REMOVED;
            default: throw new AssertionError("Unreachable");
        }
    }
    
    
    
}
