/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.listener;

import com.gmail.filoghost.holograms.api.Hologram;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.event.structure.ConstructionTaskAddedEvent;
import com.sc.api.structure.event.structure.ConstructionTaskStateChangedEvent;
import com.sc.api.structure.persistence.service.StructureService;
import com.sc.plugin.holo.SCHoloManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chingo
 */
public class ConstructionTaskListener implements Listener {
    
    @EventHandler
    public void onConstructionTaskStateChanged(ConstructionTaskStateChangedEvent stateChangedEvent) {
        ConstructionTask task = stateChangedEvent.getTask();
        Hologram holo = SCHoloManager.getInstance().getStructureHolo(task.getId());
        if(holo != null) {
            if(task.getState() == ConstructionTask.State.COMPLETE) {
                holo.removeLine(SCHoloManager.STRUCTURE_STATUS_INDEX);
                return;
            } else if (task.getState() == ConstructionTask.State.REMOVED) {
                SCHoloManager.getInstance().removeHolo(task.getId());
                return;
            }
            String newStatus = SCHoloManager.getInstance().getStatusString(task);
            holo.setLine(SCHoloManager.STRUCTURE_STATUS_INDEX, newStatus);
            holo.update();
        }
    }
    
    @EventHandler
    public void onConstructionTaskAdded(ConstructionTaskAddedEvent addedEvent) {
        ConstructionTask task = addedEvent.getTask();
        StructureService service = new StructureService();
        Structure structure = service.getStructure(task.getId());
        Hologram hologram = SCHoloManager.getInstance().putStructureHolo(structure);
    }
    
}
