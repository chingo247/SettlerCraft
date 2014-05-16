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

package com.sc.api.structure.model.structure;

/**
 *
 * @author Chingo
 */
public enum StructureState {

        /**
         * All blocks on structure location will be removed.
         *//**
         * All blocks on structure location will be removed.
         *//**
         * All blocks on structure location will be removed.
         *//**
         * All blocks on structure location will be removed.
         */
        CLEARING_SITE_OF_BLOCKS,
        /**
         * All blocks on structure location will be removed.
         */
        CLEARING_SITE_OF_ENTITIES,
        /**
         * Placeing Foundation
         */
        PLACING_FOUNDATION,
        /**
         * Frame will be placed, all players will be removed from the foundation
         */
        PLACING_FRAME,
        
        /**
         * When the Complete() was called on this structure
         */
        FINISHING,
        /**
         * A layer is being constructed
         */
        CONSTRUCTING_A_LAYER,
        /**
         * Players/NPC may build now
         */
        READY_TO_BE_BUILD,
        /**
         * ConstructionSite is complete
         */
        COMPLETE
    }
