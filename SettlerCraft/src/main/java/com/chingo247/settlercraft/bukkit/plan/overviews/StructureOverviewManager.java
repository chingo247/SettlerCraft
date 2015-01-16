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
//
//package com.chingo247.settlercraft.bukkit.plan.overviews;
//
//
//import com.chingo247.settlercraft.bukkit.BukkitStructureAPI;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure.State;
//import static com.chingo247.settlercraft.structureapi.structure.old.Structure.State.BUILDING;
//import static com.chingo247.settlercraft.structureapi.structure.old.Structure.State.COMPLETE;
//import static com.chingo247.settlercraft.structureapi.structure.old.Structure.State.DEMOLISHING;
//import static com.chingo247.settlercraft.structureapi.structure.old.Structure.State.STOPPED;
//import com.chingo247.settlercraft.structureapi.event.SettlerCraftDisableEvent;
//import com.chingo247.settlercraft.structureapi.event.structure.StructureCreateEvent;
//import com.chingo247.settlercraft.structureapi.event.structure.StructureStateChangeEvent;
//import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
//import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
//import com.chingo247.settlercraft.structureapi.structure.QStructure;
//import com.gmail.filoghost.holograms.api.Hologram;
//import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
//import com.google.common.eventbus.Subscribe;
//import com.mysema.query.jpa.JPQLQuery;
//import com.mysema.query.jpa.hibernate.HibernateQuery;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.plugin.Plugin;
//import org.hibernate.Session;
//
///**
// *
// * @author Chingo
// */
//public class StructureOverviewManager {
//
////    private static final int STRUCTURE_ID_INDEX = 0;
////    private static final int STRUCTURE_NAME_INDEX = 1;
//    private static final int STRUCTURE_STATUS_INDEX = 2;
//    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());
//    private final BukkitStructureAPI structureAPI;
//    private final Plugin plugin;
//
//    public StructureOverviewManager(Plugin plugin, BukkitStructureAPI structureAPI) {
//        this.structureAPI = structureAPI;
//        this.plugin = plugin;
//    }
//
//    private synchronized void createHolograms(final Structure structure) {
//
//        // In case being executed asynchronously
//        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//            @Override
//            public void run() {
//                if (holograms.get(structure.getId()) == null) {
//                    holograms.put(structure.getId(), new ArrayList<Hologram>());
//                }
//                try {
//                    StructurePlan plan = structureAPI.getStructurePlanManager().getPlan(structure);
//                    List<StructureOverview> holos = plan.getOverviews();
//
//                    if (holos.isEmpty() && structureAPI.useHolograms()) {
//
//                        com.chingo247.settlercraft.structureapi.world.Location loc = structure.translateRelativeLocation(0, 2, 0); // Above ground and not in block...
//                        World world = Bukkit.getWorld(loc.getWorld());
//
//                        Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, new Location(world, loc.getX(), loc.getY(), loc.getZ()),
//                                ChatColor.GOLD + String.valueOf(structure.getId()),
//                                ChatColor.BLUE + structure.getName(),
//                                getStatusString(structure)
//                        );
//                        holograms.get(structure.getId()).add(hologram);
//
//                    } else {
//                        for (StructureOverview so : holos) {
//
//                            com.chingo247.settlercraft.structureapi.world.Location loc = structure.translateRelativeLocation(so.getX(), so.getY(), so.getZ()); // Above ground and not in block...
//                            World world = Bukkit.getWorld(loc.getWorld());
//
//                            Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, new Location(world, loc.getX(), loc.getY(), loc.getZ()),
//                                    ChatColor.GOLD + String.valueOf(structure.getId()),
//                                    ChatColor.BLUE + structure.getName(),
//                                    getStatusString(structure)
//                            );
//                            holograms.get(structure.getId()).add(hologram);
//                        }
//                    }
//                } catch (StructureDataException | IOException ex) {
//                    Logger.getLogger(StructureOverviewManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//            }
//        });
//
//    }
//
//    private synchronized void removeHolos(Structure structure) {
//        for (Hologram hologram : holograms.get(structure.getId())) {
//            if (!hologram.isDeleted()) {
//                hologram.delete();
//                holograms.remove(structure.getId());
//            }
//        }
//    }
//
//    private String getStatusString(Structure structure) {
//        State state = structure.getState();
//        String statusString;
//        switch (state) {
//            case DEMOLISHING:
//                statusString = "Status: " + ChatColor.YELLOW;
//                statusString += state.name();
//                break;
//            case BUILDING:
//                statusString = "Status: " + ChatColor.YELLOW;
//                statusString += state.name();
//                break;
//            case COMPLETE:
//                statusString = "";
//                break;
//            case STOPPED:
//                statusString = "Status: " + ChatColor.RED;
//                statusString += state.name();
//                break;
//            default:
//                statusString = "Status: " + ChatColor.WHITE;
//                statusString += state.name();
//                break;
//        }
//        return statusString;
//    }
//
//    protected synchronized void updateHolos(final Structure structure) {
//        if (structure.getState() == State.REMOVED) {
//            removeHolos(structure);
//        }
//
//        List<Hologram> holos = holograms.get(structure.getId());
//        if (holos != null && !holos.isEmpty()) {
//            Iterator<Hologram> hit = holos.iterator();
//            while (hit.hasNext()) {
//                Hologram holo = hit.next();
//                holo.setLine(STRUCTURE_STATUS_INDEX, getStatusString(structure));
//                holo.update();
//            }
//        }
//
//    }
//
//    public void init() {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QStructure qs = QStructure.structure;
//        final List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
//        session.close();
//        for (Structure s : structures) {
//            createHolograms(s);
//        }
//
//    }
//
//    @Subscribe
//    public void onStateChanged(StructureStateChangeEvent stateChangeEvent) {
//        updateHolos(stateChangeEvent.getStructure());
//    }
//
//    @Subscribe
//    public void onCreate(StructureCreateEvent createEvent) {
//        createHolograms(createEvent.getStructure());
//    }
//
//    @Subscribe
//    public void clearAll(SettlerCraftDisableEvent disableEvent) {
//        for (List<Hologram> hologramList : holograms.values()) {
//            for (Hologram h : hologramList) {
//                h.delete();
//            }
//        }
//    }
//
//}
