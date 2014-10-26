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
package com.chingo247.structureapi.plan.holograms;

import com.chingo247.structureapi.QStructure;
import com.chingo247.structureapi.Structure;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.event.StructureCreateEvent;
import com.chingo247.structureapi.event.StructureStateChangeEvent;
import com.chingo247.structureapi.exception.StructureDataException;
import com.chingo247.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.structureapi.plan.StructurePlan;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.Vector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureHologramManager implements Listener {

    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());
    private boolean initialized = false;
    private final StructureAPI structureAPI;

    public StructureHologramManager(StructureAPI structureAPI) {
        this.structureAPI = structureAPI;
    }
    

    public synchronized void init() {
        if (initialized) {
            return;
        }
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        final List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        for (Structure s : structures) {
            createHolograms(s);
        }
        initialized = true;
    }

    private void createHolograms(final Structure structure) {
        // FIX: Crashed the server when not performed sync delayed as this method could be called from a different thread
        // 
        Bukkit.getScheduler().scheduleSyncDelayedTask(structureAPI.getPlugin(), new Runnable() {

            @Override
            public void run() {
                if (holograms.get(structure.getId()) == null) {
                    holograms.put(structure.getId(), new ArrayList<Hologram>());
                }

                try {
                    StructurePlan plan = structureAPI.getStructurePlanManager().getPlan(structure);
                    List<StructureHologram> holos = plan.getHolograms();
                    for (StructureHologram sh : holos) {
                        Location location = structure.translateRelativeLocation(new Vector(sh.x, sh.y, sh.z));

                        Hologram hologram = HolographicDisplaysAPI.createHologram(structureAPI.getPlugin(),
                                location,
                                sh.lines
                        );
                        holograms.get(structure.getId()).add(hologram);
                    }

                } catch (StructureDataException | IOException ex) {
                    Logger.getLogger(StructureHologramManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    public void removeHolos(Structure structure) {
        for (Hologram h : holograms.get(structure.getId())) {

            h.delete();
            holograms.remove(structure.getId());
        }
    }

    @EventHandler
    protected void onStateChanged(StructureStateChangeEvent changeEvent) {
        if (changeEvent.getStructure().getState() == Structure.State.REMOVED) {
            removeHolos(changeEvent.getStructure());
        }
    }

    @EventHandler
    protected void onCreate(StructureCreateEvent createEvent) {
        createHolograms(createEvent.getStructure());
    }
    
    @EventHandler
    public void shutdown(PluginDisableEvent pde) {
        if (pde.getPlugin().getName().equals(structureAPI.getPlugin().getName())) {
            for(List<Hologram> hologramList : holograms.values()) {
                for(Hologram h : hologramList) {
                    h.delete();
                }
            }
        }
    }

}
