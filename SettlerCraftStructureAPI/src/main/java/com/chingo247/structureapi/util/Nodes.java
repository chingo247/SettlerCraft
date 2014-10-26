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
package com.chingo247.structureapi.util;

/**
 * Utility Class
 * @author Chingo
 */
public class Nodes {
    
    private Nodes() {}
    
    // Main
    public static final String ROOT_NODE = "StructurePlan";
    
    // StructureAPI Related
//    public static final String SETTLERCRAFT_NODE = Elements.SETTLERCRAFT;
    public static final String NAME_NODE = Elements.NAME;
    public static final String SCHEMATIC_NODE = Elements.SCHEMATIC;
    public static final String DESCRIPTION_NODE = Elements.DESCRIPTION;
    public static final String CATEGORY_NODE = Elements.CATEGORY;
    public static final String FACTION_NODE = Elements.FACTION;
    public static final String PRICE_NODE = Elements.PRICE;

    // Entities
    public static final String STRUCTURE_OVERVIEWS_NODE = Elements.STRUCTURE_OVERVIEWS;
    public static final String STRUCTURE_OVERVIEW_NODE = STRUCTURE_OVERVIEWS_NODE + "/" + Elements.STRUCTURE_OVERVIEW;
    
    //Holographic Displays
    public static final String HOLOGRAMS_NODE = Elements.STRUCTURE_HOLOGRAMS;
    public static final String HOLOGRAM_NODE = HOLOGRAMS_NODE + "/" + Elements.STRUCTURE_HOLOGRAM;
    
    
    // WorldGuard Related
    public static final String WORLDGUARD_NODE = Elements.WORLDGUARD;
    public static final String WORLDGUARD_FLAGS_NODE = WORLDGUARD_NODE + "/" + Elements.REGIONFLAGS;
    public static final String WORLDGUARD_FLAG_NODE = WORLDGUARD_FLAGS_NODE + "/" + Elements.REGIONFLAG;
    
    
}
