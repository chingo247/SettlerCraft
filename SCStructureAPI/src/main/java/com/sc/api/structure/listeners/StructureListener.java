///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.sc.api.structure.listeners;
//
//import com.sc.api.structure.event.structure.StructureCompleteEvent;
//import com.sc.api.structure.event.structure.StructureLayerCompleteEvent;
//import org.bukkit.Bukkit;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//
///**
// *
// * @author Chingo
// */
//public class StructureListener implements Listener {
//    
//    
//    @EventHandler
//    public void onLayerCompleteEvent(StructureLayerCompleteEvent lce) {
//        System.out.println("Layer Complete Event!");
//        if(lce.getLayer() == lce.getStructure().getPlan().getStructureSchematic().layers - 1) {
//            Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(lce.getStructure()));
//        } 
//    }
//    
//    @EventHandler
//    public void onStructureCompleteEvent(StructureCompleteEvent sce) {
//        System.out.println("Structure Complete Event!");
//    }
//}
