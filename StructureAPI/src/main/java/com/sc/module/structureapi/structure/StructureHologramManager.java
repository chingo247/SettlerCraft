/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.sc.module.structureapi.structure.Structure.State;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class StructureHologramManager {

    private static final int STRUCTURE_ID_INDEX = 0;
    private static final int STRUCTURE_PLAN_INDEX = 1;
    private static final int STRUCTURE_STATUS_INDEX = 2;
    private static StructureHologramManager instance;
    private final Map<Long, Hologram> holograms = Collections.synchronizedMap(new HashMap<Long, Hologram>());
    private boolean enabled;

    /**
     * Private Constructor.
     */
    private StructureHologramManager() {
        this.enabled = Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null;
    }

    public void setEnabled(boolean enabled) {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            this.enabled = enabled;
        }
    }

    public static StructureHologramManager getInstance() {
        if (instance == null) {
            instance = new StructureHologramManager();
        }
        return instance;
    }

    public void createHologram(final Plugin plugin, final Structure structure) {
        if (enabled) {
            // In case being executed asynchronously
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                @Override
                public void run() {
                    Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, structure.getLocation().add(0,2,0),
                            "Id: " + ChatColor.GOLD + structure.getId(),
                            "Plan: " + ChatColor.BLUE + structure.getName(),
                            "Status: " + structure.getState().name()
                    );
                    holograms.put(structure.getId(), hologram);
                }
            });

        }
    }

    public void removeHolo(Structure structure) {
        if (enabled) {
            Hologram hologram = holograms.get(structure.getId());
            if (hologram != null) {
                hologram.delete();
                holograms.remove(structure.getId());
            }
        }
    }

    public void updateHolo(final Structure structure) {
        if (enabled) {
            //FIX For out of sync
            Bukkit.getScheduler().scheduleSyncDelayedTask(StructureAPI.getPlugin(), new Runnable() {

                @Override
                public void run() {
                    final Hologram holo = holograms.get(structure.getId());
                    State state = structure.getState();
                    if (holo != null) {
                        if (state == State.COMPLETE) {
                            holo.setLine(STRUCTURE_STATUS_INDEX, "");
                            holo.update();
                            return;
                        } else if (state == State.REMOVED) {
                            removeHolo(structure);
                            return;
                        }
                        String statusString;
                        switch (state) {
                            case DEMOLISHING:
                                statusString = "Status: " + ChatColor.YELLOW;
                                statusString += state.name();
                                break;
                            case BUILDING:
                                statusString = "Status: " + ChatColor.YELLOW;
                                statusString += state.name();
                                break;
                            case COMPLETE:
                                statusString = "";
                                break;
                            case STOPPED:
                                statusString = "Status: " + ChatColor.RED;
                                statusString += state.name();
                                break;
                            default:
                                statusString = "Status: " + ChatColor.WHITE;
                                statusString += state.name();
                                break;
                        }

                        holo.setLine(STRUCTURE_STATUS_INDEX, statusString);
                        holo.update();

                    }
                }
            });
        }
    }

}