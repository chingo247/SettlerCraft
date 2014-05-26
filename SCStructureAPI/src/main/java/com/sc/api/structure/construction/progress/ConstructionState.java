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
package com.sc.api.structure.construction.progress;

/**
 *
 * @author Chingo
 */
public enum ConstructionState {
    
    /**
     * Task has been created
     */
    PREPARING,
    /**
     * Task is in AsyncWorldEdit's Queue and is waiting to be processed
     */
    IN_QUEUE,
    /*
     * Task is being executed
     */
    IN_PROGRESS,
    /**
     * Task has been marked completed
     */
    COMPLETE,
    /**
     * The completion of the task has been confirmed at startup, the sign has been read and matches the state of this task
     */
    COMPLETION_CONFIRMED,
    /**
     * Task has been canceled and will continue as DEMOLISION task
     */
    CANCELED,
    /**
     * Task has been marked for removal (structure still exists)
     */
     REMOVED,
     /**
      * Task's removal has been confirmed (structure will be deleted)
      */
    IN_RECYCLE_BIN
}
