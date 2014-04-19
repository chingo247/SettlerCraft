/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.listeners;

import com.sc.api.structure.SCStructureAPI;
import com.sc.api.structure.event.LayerCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chingo
 */
public class StructureListener implements Listener {
    
    
    @EventHandler
    public void onLayerCompleteEvent(LayerCompleteEvent lce) {
        System.out.println("Layer Complete");
        
    }
}
