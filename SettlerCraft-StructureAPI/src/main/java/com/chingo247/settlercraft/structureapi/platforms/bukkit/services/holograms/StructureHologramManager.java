/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.holograms;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.event.StructureStateChangeEvent;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.Hologram;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.HologramsProvider;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IScheduler;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class StructureHologramManager {

    private static StructureHologramManager instance;
    private static final String PLUGIN = "SettlerCraft-StructureAPI";
    private static final int STATUS_LINE = 2;

    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());
    private final APlatform platform;
    private final IColors color;
    private final IScheduler scheduler;

    private HologramsProvider hologramsProvider;

    private StructureHologramManager() {
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.color = platform.getChatColors();
        this.scheduler = platform.getServer().getScheduler(platform.getServer().getPlugin(PLUGIN));
    }

    public static StructureHologramManager getInstance() {
        if (instance == null) {
            instance = new StructureHologramManager();
        }
        return instance;
    }

    public void setHologramProvider(HologramsProvider hologramsProvider) {
        Preconditions.checkNotNull(hologramsProvider);
        this.hologramsProvider = hologramsProvider;
    }

    public void registerStructureHologram(Structure structure, Hologram hologram) {
        if (holograms.get(structure.getId()) == null) {
            holograms.put(structure.getId(), new ArrayList<Hologram>());
        }
        
        hologram.addLine("#" + color.gold() + String.valueOf(structure.getId()));
        hologram.addLine(color.blue() + structure.getName());
        hologram.addLine(getStatusString(structure));
        synchronized (holograms) {
            List<Hologram> holos = holograms.get(structure.getId());
            synchronized (holos) {
                holos.add(hologram);
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onStructureCreate(StructureCreateEvent structureCreateEvent) {
        if (hologramsProvider == null) {
            return;
        }

        final Structure structure = structureCreateEvent.getStructure();

        

        // Assures non-async behavior - Fixes concurrent exceptions that could be thrown
        scheduler.runSync(new Runnable() {

            @Override
            public void run() {
                World w = SettlerCraft.getInstance().getWorld(structure.getWorld());
                Hologram hologram = hologramsProvider.createHologram(PLUGIN, w, structure.translateRelativeLocation(new Vector(0, 2, 0)), structure);
                registerStructureHologram(structure, hologram);
            }
        });

    }

    @Subscribe
    @AllowConcurrentEvents
    public void onStructureStateChange(StructureStateChangeEvent changeEvent) {
        if (hologramsProvider == null) {
            return;
        }

        final Structure structure = changeEvent.getStructure();
        scheduler.runSync(new Runnable() {

            @Override
            public void run() {

                List<Hologram> holos = holograms.get(structure.getId());
                if (holos != null && !holos.isEmpty()) {
                    synchronized (holos) {
                        ConstructionStatus status = structure.getConstructionStatus();
                        if (status == ConstructionStatus.REMOVED) {
                            for (Hologram holo : holos) {
                                holo.delete();
                            }
                            synchronized (holograms) {
                                holograms.remove(structure.getId());
                            }
                        } else {
                            String statusString = getStatusString(structure);
                            for (Hologram holo : holos) {
                                holo.removeLine(STATUS_LINE);
                                holo.addLine(statusString);
                            }

                        }
                    }
                }
            }
        });

    }

    private String getStatusString(Structure structure) {
        ConstructionStatus state = structure.getConstructionStatus();
        String statusString;
        switch (state) {
            case DEMOLISHING:
                statusString = color.yellow();
                statusString += state.name();
                break;
            case BUILDING:
                statusString = color.yellow();
                statusString += state.name();
                break;
            case COMPLETED:
                statusString = "";
                break;
            case STOPPED:
                statusString = color.red();
                statusString += state.name();
                break;
            default:
                statusString = color.white();
                statusString += state.name();
                break;
        }
        return statusString;
    }

}
