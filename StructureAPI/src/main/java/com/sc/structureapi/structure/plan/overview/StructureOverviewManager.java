package com.sc.structureapi.structure.plan.overview;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.structureapi.bukkit.ConfigProvider;
import com.sc.structureapi.bukkit.events.StructureCreateEvent;
import com.sc.structureapi.bukkit.events.StructureStateChangeEvent;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.persistence.HibernateUtil;
import com.sc.structureapi.structure.StructureAPIModule;
import com.sc.structureapi.structure.entities.structure.QStructure;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.entities.structure.Structure.State;
import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.plan.StructurePlanManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.dom4j.DocumentException;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureOverviewManager implements Listener {

//    private static final int STRUCTURE_ID_INDEX = 0;
//    private static final int STRUCTURE_NAME_INDEX = 1;
    private static final int STRUCTURE_STATUS_INDEX = 2;
    private static StructureOverviewManager instance;
    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());

    private StructureOverviewManager() {
    }

    public static StructureOverviewManager getInstance() {
        if (instance == null) {
            instance = new StructureOverviewManager();
        }
        return instance;
    }

    private synchronized void createHolograms(final Plugin plugin, final Structure structure) {
        // In case being executed asynchronously
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (holograms.get(structure.getId()) == null) {
                    holograms.put(structure.getId(), new ArrayList<Hologram>());
                }
                try {
                    StructurePlan plan = StructurePlanManager.getInstance().getPlan(structure);
                    List<StructureOverview> holos = plan.getOverviews();
                    
                    if (holos.isEmpty() && plan.hasDefaultHologramEnabled() && ConfigProvider.getInstance().hasDefaultHologramEnabled()) {
                        Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, structure.translateRelativeLocation(0, 2, 0),
                                ChatColor.GOLD + String.valueOf(structure.getId()),
                                ChatColor.BLUE + structure.getName(),
                                getStatusString(structure)
                        );
                        holograms.get(structure.getId()).add(hologram);

                    } else {
                        for (StructureOverview so : holos) {
                            Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, structure.translateRelativeLocation(so.getX(), so.getY(), so.getZ()),
                                    ChatColor.GOLD + String.valueOf(structure.getId()),
                                    ChatColor.BLUE + structure.getName(),
                                    getStatusString(structure)
                            );
                            holograms.get(structure.getId()).add(hologram);
                        }
                    }
                } catch (DocumentException | StructureDataException ex) {
                    Logger.getLogger(StructureOverviewManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }

    private synchronized void removeHolos(Structure structure) {
        for (Hologram hologram : holograms.get(structure.getId())) {
            if (!hologram.isDeleted()) {
                hologram.delete();
                holograms.remove(structure.getId());
            }
        }
    }

    private String getStatusString(Structure structure) {
        State state = structure.getState();
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
        return statusString;
    }

    protected synchronized void updateHolos(final Structure structure) {
        if (structure.getState() == State.REMOVED) {
            removeHolos(structure);
        }

        for (Hologram holo : holograms.get(structure.getId())) {
            holo.setLine(STRUCTURE_STATUS_INDEX, getStatusString(structure));
            holo.update();
        }
    }

    public void init() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        final List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        session.close();
        for (Structure s : structures) {
            createHolograms(StructureAPIModule.getInstance().getMainPlugin(), s);
        }

    }

    @EventHandler
    public void onStateChanged(StructureStateChangeEvent stateChangeEvent) {
        updateHolos(stateChangeEvent.getStructure());
    }

    @EventHandler
    public void onCreate(StructureCreateEvent createEvent) {
        createHolograms(StructureAPIModule.getInstance().getMainPlugin(), createEvent.getStructure());
    }

}
