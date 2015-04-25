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
package com.chingo247.structureapi;

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
    REMOVED(6);

    private final int statusId;
    
    private ConstructionStatus(int statusId) {
        this.statusId = statusId;
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
