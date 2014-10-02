/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure.dataplans;

/**
 * Utility Class
 * @author Chingo
 */
public class Nodes {
    
    private Nodes() {}
    
    // Main
    public static final String ROOT_NODE = "StructurePlan";
    
    // StructureAPI Related
    public static final String STRUCTUREAPI_NODE = ROOT_NODE + "/" + Elements.STRUCTUREAPI;
    public static final String NAME_NODE = STRUCTUREAPI_NODE + "/" + Elements.NAME;
    public static final String SCHEMATIC_NODE = STRUCTUREAPI_NODE + "/" + Elements.SCHEMATIC;
    public static final String STRUCTURE_SCHEMATIC_NODE = SCHEMATIC_NODE + "/" + Elements.STRUCTURE;
    public static final String DESCRIPTION_NODE = STRUCTUREAPI_NODE + "/" + Elements.DESCRIPTION;
    public static final String CATEGORY_NODE = STRUCTUREAPI_NODE + "/" + Elements.CATEGORY;
    public static final String FACTION_NODE = STRUCTUREAPI_NODE + "/" + Elements.FACTION;
    public static final String PRICE_NODE = STRUCTUREAPI_NODE + "/" + Elements.PRICE;

    // Entities
    public static final String STRUCTURE_OVERVIEWS_NODE = STRUCTUREAPI_NODE + "/" + Elements.STRUCTURE_OVERVIEWS;
    public static final String STRUCTURE_OVERVIEW_NODE = STRUCTURE_OVERVIEWS_NODE + "/" + Elements.STRUCTURE_OVERVIEW;
    
    //Holographic Displays
    public static final String HOLOGRAMS_NODE = STRUCTUREAPI_NODE + "/" + Elements.STRUCTURE_HOLOGRAMS;
    public static final String HOLOGRAM_NODE = HOLOGRAMS_NODE + "/" + Elements.STRUCTURE_HOLOGRAM;
    
    
    // WorldGuard Related
    public static final String WORLDGUARD_NODE = STRUCTUREAPI_NODE + "/" + Elements.WORLDGUARD;
    public static final String WORLDGUARD_FLAGS_NODE = WORLDGUARD_NODE + "/" + Elements.REGIONFLAGS;
    public static final String WORLDGUARD_FLAG_NODE = WORLDGUARD_FLAGS_NODE + "/" + Elements.REGIONFLAG;
    
    
}
