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
package com.chingo247.settlercraft.structure.data.holograms;

import com.chingo247.settlercraft.bukkit.events.StructureCreateEvent;
import com.chingo247.settlercraft.bukkit.events.StructureStateChangeEvent;
import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.entities.structure.QStructure;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.Vector;
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
import org.bukkit.plugin.Plugin;
import org.dom4j.DocumentException;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureHologramManager implements Listener {

    private static StructureHologramManager instance;
    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());
    public boolean initialized = false;

    private StructureHologramManager() {
    }

    public static StructureHologramManager getInstance() {
        if (instance == null) {
            instance = new StructureHologramManager();
        }
        return instance;
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
        final Plugin plugin = SettlerCraft.getInstance();
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (holograms.get(structure.getId()) == null) {
                    holograms.put(structure.getId(), new ArrayList<Hologram>());
                }

                try {
                    StructurePlan plan = StructurePlanManager.getInstance().getPlan(structure);
                    List<StructureHologram> holos = plan.getHolograms();
                    for (StructureHologram sh : holos) {
                        Location location = structure.translateRelativeLocation(new Vector(sh.x, sh.y, sh.z));

                        Hologram hologram = HolographicDisplaysAPI.createHologram(
                                plugin,
                                location,
                                sh.text
                        );
                        holograms.get(structure.getId()).add(hologram);
                    }

                } catch (DocumentException | StructureDataException ex) {
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
        if (pde.getPlugin().getName().equals(SettlerCraft.getInstance().getName())) {
            for(List<Hologram> hologramList : holograms.values()) {
                for(Hologram h : hologramList) {
                    h.delete();
                }
            }
        }
    }

}
