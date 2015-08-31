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
package com.chingo247.structureapi.model.structure;

/**
 *
 * @author Chingo
 */
public enum ConstructionStatus {
    
    ON_HOLD(0),
    BUILDING(1),
    STOPPED(2),
    DEMOLISHING(3),
    COMPLETED(4),
    QUEUED(5),
    REMOVED(6),
    PLACING_FENCE(7),
    ROLLING_BACK(8),
    PLACING(9),
    CREATING_BACKUP(10),
    PERFORMING_OPERATION(11);
    


    private final int statusId;
    
    private ConstructionStatus(int statusId) {
        this.statusId = statusId;
    }

    
    
    public int getStatusId() {
        return statusId;
    }
    
    public static ConstructionStatus getStatus(String status) {
        switch(status.replaceAll(" ", "_")) {
            case "CREATING_BACKUP":
                return CREATING_BACKUP;
            case "ON_HOLD":
                return ON_HOLD;
            case "BUILDING":
                return BUILDING;
            case "STOPPED":
                return STOPPED;
            case "DEMOLISHING":
                return COMPLETED;
            case "COMPLETED":
                return COMPLETED;
            case "QUEUED": 
                return QUEUED;
            case "REMOVED":
                return REMOVED;
            case "PLACING_FENCE":
                return PLACING_FENCE;
            case "ROLLING_BACK":
                return ROLLING_BACK;
            case "PLACING":
                return PLACING;
            default:
                return PERFORMING_OPERATION;
        }
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
            case 7: return PLACING_FENCE;
            case 8: return ROLLING_BACK;
            case 9: return PLACING;
            case 10: return CREATING_BACKUP;
            case 11: return PERFORMING_OPERATION;
            default: throw new AssertionError("Unreachable");
        }
    }
    
    
}
