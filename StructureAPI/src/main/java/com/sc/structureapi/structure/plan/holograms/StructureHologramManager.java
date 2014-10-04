/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan.holograms;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.persistence.HibernateUtil;
import com.sc.structureapi.structure.QStructure;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.StructureAPIModule;
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
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureHologramManager implements Listener {

    private static StructureHologramManager instance;
    private final Map<Long, List<Hologram>> holograms = Collections.synchronizedMap(new HashMap<Long, List<Hologram>>());
    public boolean initialized = false;

    
    public synchronized void init() {
        if(initialized) {
            return;
        }
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        final List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        for(Structure s : structures) {
            onCreate(s);
        }    
        initialized = true;
    }

    private void createHolograms(final Plugin plugin, final Structure structure) {
                       Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                @Override
                public void run() {
                    if (holograms.get(structure.getId()) == null) {
                        holograms.put(structure.getId(), new ArrayList<Hologram>());
                    }
                    
                    try {
                        Document d = new SAXReader().read(structure.getConfig());
                        List<StructureHologram> holos = new StructureHologramLoader().load(d);
                        for (StructureHologram sh : holos) {
                            Location l = structure.translateRelativeLocation(new Vector(sh.x, sh.y, sh.z));
                            
                            Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, l,
                                    sh.text
                            );
                            holograms.get(structure.getId()).add(hologram);
                        }

                    } catch (DocumentException ex) {
                        Logger.getLogger(StructureHologramManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (StructureDataException ex) {
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

    public void onStateChanged(Structure structure, Structure.State newState) {
        if(structure.getState() != newState && newState == Structure.State.REMOVED) {
            removeHolos(structure);
        }
    }

    public void onCreate(final Structure structure) {
         // In case being executed asynchronously
        createHolograms(StructureAPIModule.getInstance().getMainPlugin(), structure);
    }

 

   
    
}
