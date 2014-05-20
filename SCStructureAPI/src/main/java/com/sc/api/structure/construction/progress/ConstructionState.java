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
         * The state direct after the structure has been placed
         *//**
         * The state direct after the structure has been placed
         */
        PREPARING,
        /**
         * Before structure has been added to the Player's Queue
         */
        PLACING_FOUNDATION,

        /**
         * When 
         */
        IN_QUEUE,
        
        /*
         * Constructing
         */
        CONSTRUCTION_IN_PROGRESS,
        
        CANCELED,
        /**
         * ConstructionSite is complete
         */
        FINISHED
    }
