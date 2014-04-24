/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.entity.structure;

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
