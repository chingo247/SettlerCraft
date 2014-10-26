/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.rollback;

import com.chingo247.structureapi.Dimension;
import com.chingo247.structureapi.Structure;
import com.chingo247.structureapi.StructureAPI;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.actions.Handler;
import me.botsko.prism.appliers.PrismProcessType;
import org.bukkit.util.Vector;

/**
 *
 * @author Chingo
 */
public class PrismRollbackService implements RollbackService {
    
    private final Prism prism;

    public PrismRollbackService(Prism prism) {
        if(prism == null) throw new AssertionError("Prism was null");
        this.prism = prism;
    }
    
    

    /**
     * 
     * @param structure 
     */
    @Override
    public void rollback(Structure structure) {
        QueryParameters parameters = new QueryParameters();
        String world = structure.getWorldName();
        Dimension d = structure.getDimension();
        Vector min = new Vector(d.getMinX(), d.getMinY(), d.getMaxX());
        Vector max = new Vector(d.getMaxX(), d.getMaxY(), d.getMaxZ());
        long since = structure.getLog().getCreatedAt().getTime();
        long before = structure.getLog().getCompletedAt().getTime();
        
        parameters.setWorld(world);
        parameters.setMinLocation(min);
        parameters.setMaxLocation(max);
        parameters.setSinceTime(since);
        parameters.setBeforeTime(before);
        parameters.setProcessType(PrismProcessType.LOOKUP);
        
        ActionsQuery aq = new ActionsQuery(prism);
        QueryResult lResult = aq.lookup(parameters);
        if(!lResult.getActionResults().isEmpty()) {
            StructureAPI.print("Printing results");
           
            for(int i = 0; i < 5; i++) {
                Handler h = lResult.getActionResults().get(i);
                StructureAPI.print("Name: " + h.getType().getName());
                StructureAPI.print("Family: " + h.getType().getFamilyName());
                StructureAPI.print("Data: " + h.getData());
                StructureAPI.print("Nice Name: " + h.getNiceName());
                StructureAPI.print("Handler: " + h.getType().getHandler());
            }
            
            
        }
        
    }
    
}
