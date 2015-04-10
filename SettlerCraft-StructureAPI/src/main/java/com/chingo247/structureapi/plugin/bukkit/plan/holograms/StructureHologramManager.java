package com.chingo247.structureapi.plugin.bukkit.plan.holograms;

///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.bukkit.plan.holograms;
//
//import com.chingo247.settlercraft.bukkit.BukkitStructureAPI;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.structureapi.event.SettlerCraftDisableEvent;
//import com.chingo247.settlercraft.structureapi.event.structure.StructureCreateEvent;
//import com.chingo247.settlercraft.structureapi.event.structure.StructureStateChangeEvent;
//import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
//import com.chingo247.settlercraft.structureapi.structure.QStructure;
//import com.chingo247.settlercraft.structureapi.structure.plan.processor.StructurePlan;
//import com.gmail.filoghost.holograms.api.Hologram;
//import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
//import com.google.common.eventbus.Subscribe;
//import com.mysema.query.jpa.JPQLQuery;
//import com.mysema.query.jpa.hibernate.HibernateQuery;
//import com.sk89q.worldedit.Vector;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.plugin.Plugin;
//import org.hibernate.Session;
//
///**
// *
// * @author Chingo
// */
//public class StructureHologramManager {
//
//    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());
//    private boolean initialized = false;
//    private final BukkitStructureAPI structureAPI;
//    private final Plugin plugin;
//
//    public StructureHologramManager(Plugin plugin, BukkitStructureAPI structureAPI) {
//        this.structureAPI = structureAPI;
//        this.plugin = plugin;
//    }
//    
//
//    public synchronized void init() {
//        if (initialized) {
//            return;
//        }
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QStructure qs = QStructure.structure;
//        final List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
//        for (Structure s : structures) {
//            createHolograms(s);
//        }
//        initialized = true;
//    }
//
//    private void createHolograms(final Structure structure) {
//        // FIX: Crashed the server when not performed sync delayed as this method could be called from a different thread
//        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//            @Override
//            public void run() {
//                if (holograms.get(structure.getId()) == null) {
//                    holograms.put(structure.getId(), new ArrayList<Hologram>());
//                }
//
//                try {
//                    StructurePlan plan = structureAPI.getStructurePlanManager().getPlan(structure);
//                    List<StructureHologram> holos = plan.getHolograms();
//                    for (StructureHologram sh : holos) {
//                        com.chingo247.settlercraft.structureapi.world.Location loc = structure.translateRelativeLocation(new Vector(sh.getX(), sh.getY(), sh.getZ()));
//                        Location location = new Location(Bukkit.getWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ());
//
//                        Hologram hologram = HolographicDisplaysAPI.createHologram(plugin,
//                                location,
//                                sh.getLines()
//                        );
//                        holograms.get(structure.getId()).add(hologram);
//                    }
//
//                } catch (StructureDataException | IOException ex) {
//                    Logger.getLogger(StructureHologramManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//            }
//        });
//    }
//
//    public void removeHolos(Structure structure) {
//        for (Hologram h : holograms.get(structure.getId())) {
//
//            h.delete();
//            holograms.remove(structure.getId());
//        }
//    }
//
//    @Subscribe
//    protected void onStateChanged(StructureStateChangeEvent changeEvent) {
//        if (changeEvent.getStructure().getState() == Structure.State.REMOVED) {
//            removeHolos(changeEvent.getStructure());
//        }
//    }
//
//    @Subscribe
//    protected void onCreate(StructureCreateEvent createEvent) {
//        createHolograms(createEvent.getStructure());
//    }
//    
//    @Subscribe
//    public void clearAll(SettlerCraftDisableEvent event) {
//        for (List<Hologram> hologramList : holograms.values()) {
//            for (Hologram h : hologramList) {
//                h.delete();
//            }
//        }
//    }
//
//}
