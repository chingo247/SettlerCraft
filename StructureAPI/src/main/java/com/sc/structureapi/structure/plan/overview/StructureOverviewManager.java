package com.sc.structureapi.structure.plan.overview;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.gmail.filoghost.holograms.api.Hologram;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.structureapi.persistence.HibernateUtil;
import com.sc.structureapi.structure.QStructure;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.entities.structure.Structure.State;
import com.sc.structureapi.structure.StructureAPIModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureOverviewManager implements Listener {

    private static final int STRUCTURE_ID_INDEX = 0;
    private static final int STRUCTURE_PLAN_INDEX = 1;
    private static final int STRUCTURE_STATUS_INDEX = 2;
    private static StructureOverviewManager instance;
    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());

    private synchronized void createHolograms(final Plugin plugin, final Structure structure) {
        // In case being executed asynchronously
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (holograms.get(structure.getId()) == null) {
                    holograms.put(structure.getId(), new ArrayList<Hologram>());
                }
//                try {
//
//                    Document d = new SAXReader().read(structure.getConfig());
//                    List<StructureOverview> holograms = new StructureOverviewLoader().load(d);
//
//                    for (StructureOverview so : holograms) {
//                        Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, structure.translateRelativeLocation(sh., y, z),
//                                ChatColor.GOLD + String.valueOf(structure.getId()),
//                                ChatColor.BLUE + structure.getName(),
//                                getStatusString(structure)
//                        );
//                    }
//
////                        List<Node> nodes = d.selectNodes(Nodes.STRUCTURE_OVERVIEW_NODE);
////                        for (Node n : nodes) {
////
////                            int x = Integer.parseInt(n.selectSingleNode(Elements.X).getText());
////                            int y = Integer.parseInt(n.selectSingleNode(Elements.Y).getText());
////                            int z = Integer.parseInt(n.selectSingleNode(Elements.Z).getText());
////
////                            Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, structure.translateRelativeLocation(x, y, z),
////                                    ChatColor.GOLD + String.valueOf(structure.getId()),
////                                    ChatColor.BLUE + structure.getName(),
////                                    getStatusString(structure)
////                            );
////                            holograms.get(structure.getId()).add(hologram);
////                        }
//                } catch (DocumentException ex) {
//                    Logger.getLogger(StructureOverviewManager.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (StructureDataException ex) {
//                    Logger.getLogger(StructureOverviewManager.class.getName()).log(Level.SEVERE, null, ex);
//                }

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

    private synchronized void updateHolos(final Structure structure) {
        //FIX For out of sync
        Bukkit.getScheduler().scheduleSyncDelayedTask(StructureAPIModule.getInstance().getMainPlugin(), new Runnable() {

            @Override
            public void run() {
                if (structure.getState() == State.REMOVED) {
                    removeHolos(structure);
                }

                for (Hologram holo : holograms.get(structure.getId())) {
                    holo.setLine(STRUCTURE_STATUS_INDEX, getStatusString(structure));
                    holo.update();
                }
            }
        });
    }

    public void init() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        final List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        for (Structure s : structures) {
            onCreate(s);
        }
    }

    public void onStateChanged(Structure structure, State newState) {
        if (newState == State.REMOVED) {
            removeHolos(structure);
        } else if (structure.getState() != newState) {
            updateHolos(structure);
        }
    }

    
    public void onCreate(Structure structure) {
        createHolograms(StructureAPIModule.getInstance().getMainPlugin(), structure);
    }

}
