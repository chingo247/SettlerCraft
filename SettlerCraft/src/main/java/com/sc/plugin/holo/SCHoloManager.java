/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.holo;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.common.base.Preconditions;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.plugin.SettlerCraft;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 *
 * @author Chingo
 */
public class SCHoloManager {
    
    public static final int STRUCTURE_ID_INDEX = 0;
    public static final int STRUCTURE_PLAN_INDEX = 1;
    public static final int STRUCTURE_OWNER_INDEX = 2;
    public static final int STRUCTURE_STATUS_INDEX = 3;
    
    private static SCHoloManager instance;
    
    private final Map<Long, Hologram> structureInfoHolos;
    
    private SCHoloManager () {
        this.structureInfoHolos = Collections.synchronizedMap(new HashMap<Long, Hologram>());
    }
    
    public static SCHoloManager getInstance() {
        if(instance == null) {
            instance = new SCHoloManager();
        }
        return instance;
    }
    
    public void removeHolo(Long structurId) {
        Preconditions.checkNotNull(structurId);
        Hologram holo = structureInfoHolos.remove(structurId);
        holo.delete();
    }
    
    public Hologram putStructureHolo(Structure structure) {
        Preconditions.checkNotNull(structure.getId());
        return structureInfoHolos.put(structure.getId(), createStructureHolo(structure));
    }
    
    /**
     * Gets the hologram by structure id. Note: Structures have the same id as the ConstructionTask they are representing
     * @param structureId THe structureId
     * @return The hologram
     */
    public Hologram getStructureHolo(Long structureId) {
        Preconditions.checkNotNull(structureId);
        return structureInfoHolos.get(structureId);
    }
    
    private Hologram createStructureHolo(Structure structure) {
        Preconditions.checkNotNull(structure.getTask());
        Location location = new Location(
                Bukkit.getWorld(structure.getLocation().getWorld().getName()), 
                structure.getLocation().getPosition().getBlockX(), 
                structure.getLocation().getPosition().getBlockY() + 3,
                structure.getLocation().getPosition().getBlockZ());
        String status = getStatusString(structure.getTask());
        
        Hologram hologram = HolographicDisplaysAPI.createHologram(SettlerCraft.getSettlerCraft(), location, 
                "Id: " + ChatColor.GOLD + structure.getId(),
                "Plan: " + ChatColor.BLUE + structure.getPlan().getDisplayName(),
                "Owner: " + ChatColor.GREEN + structure.getOwner(),
                "Status: " + status
                );
       
        return hologram;
    }
    
    public String getStatusString(ConstructionTask task) {
    Preconditions.checkNotNull(task);
    String statusString = "Status: ";
            ConstructionTask.State state = task.getState();
            switch (state) {
                case CANCELED: statusString += ChatColor.RED + state.name(); break;
                case PROGRESSING: statusString += ChatColor.YELLOW + state.name(); break;
                case COMPLETE: statusString += ChatColor.GREEN + state.name(); break;
                case STOPPED: statusString += ChatColor.RED + state.name(); break;
                default: statusString += ChatColor.WHITE + state.name(); break;
            }
            return statusString;
    }
}
