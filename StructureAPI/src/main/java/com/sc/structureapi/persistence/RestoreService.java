/*
 * Copyright (C) 2014 Chingo
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
package com.sc.structureapi.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.structureapi.structure.entities.structure.PlayerOwnership;
import com.sc.structureapi.structure.entities.structure.QStructure;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.entities.structure.Structure.State;
import com.sc.structureapi.util.WorldGuardUtil;
import com.sc.structureapi.structure.entities.world.Dimension;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class RestoreService {

    public void restore() {
        HashMap<String, Timestamp> worlddata = getWorldData();
        for (Entry<String, Timestamp> entry : worlddata.entrySet()) {
            removeCreatedBefore(entry.getKey(), entry.getValue());
            setRemovedAfter(Bukkit.getWorld(entry.getKey()), entry.getValue());
        }
    }

    /**
     * Gets the world data for all worlds that have/had structure tasks assigned within them
     *
     * @return Map of all structure-tasked worlds with worldname as key and timestamp as value
     */
    private HashMap<String, Timestamp> getWorldData() {
        HashMap<String, Timestamp> worldData = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            if (isSettlerCraftWorld(world)) {
                Timestamp t = new Timestamp(world.getWorldFolder().lastModified());
                worldData.put(world.getName(), t);
            }
        }
        return worldData;
    }

    /**
     * Gets the level.dat file from a worldfolder
     *
     * @param worldDataFolder The worldFolder
     * @return The level.data file
     */
    private File getDatFile(File worldDataFolder) {
        for (File file : worldDataFolder.listFiles()) {
            if (file.getName().equals("level.dat")) {
                return file;
            }
        }
        throw new RuntimeException("World doesnt have a level.dat!");
    }
    /**
     * Checks wheter this world has structures
     *
     * @param world The world
     * @return True if this world has structures
     */
    private boolean isSettlerCraftWorld(World world) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qStructure = QStructure.structure;
        boolean exists = query.from(qStructure).where(qStructure.location().worldUUID.eq(world.getUID())).exists();
        session.close();
        return exists;
    }

    private void setRemovedAfter(World world, Timestamp timestamp) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            QStructure qct = QStructure.structure;
            JPQLQuery query = new HibernateQuery(session);
            List<Structure> structures = query.from(qct).where(qct.logEntry().autoremoved.eq(Boolean.FALSE).and(qct.location().worldUUID.eq(world.getUID())).and(qct.logEntry().completedAt.after(timestamp).or(qct.logEntry().removedAt.after(timestamp)))).list(qct);
            
            if(!structures.isEmpty()) {
            System.out.println("[SettlerCraft]: World " + world + " contains " + structures.size() + " that have an invalid status");
            Iterator<Structure> it = structures.iterator();
            RegionManager manager = WorldGuardUtil.getGlobalRegionManager(Bukkit.getWorld(world.getName()));
            while (it.hasNext()) {
                Structure structure = it.next();
                
                
                if(!manager.hasRegion(structure.getStructureRegion())) {
                    System.out.println("Structure #" + structure.getId() + " was removed after last save");
                    reclaim(structure);
                    System.out.println("Reclaimed region: " + structure.getStructureRegion());
                }
                
                structure.setState(State.STOPPED);
                structure.getLog().setRemovedAt(null);
                session.merge(structure);
            }
            try {
                manager.save();
            } catch (ProtectionDatabaseException ex) {
                Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            tx.commit();
            }
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    private synchronized ProtectedRegion reclaim(Structure structure) {
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        Dimension dim = structure.getDimension();
        Vector p1 = dim.getMinPosition();
        Vector p2 = dim.getMaxPosition();
        String id = structure.getStructureRegion();

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        PlayerOwnershipService pos = new PlayerOwnershipService();
        
        // Set Owners
        for(PlayerOwnership owner : pos.getOwners(structure)) {
            region.getOwners().addPlayer(owner.getName());
        }
        mgr.addRegion(region);

        return region;

    }

    private void removeCreatedBefore(String world, Timestamp timestamp) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            QStructure qct = QStructure.structure;
            JPQLQuery query = new HibernateQuery(session);
            List<Structure> structures = query.from(qct).where(qct.logEntry().createdAt.after(timestamp)
                    .and(qct.location().world.eq(world))
                    .and(qct.state.ne(State.REMOVED))).list(qct);
            if (!structures.isEmpty()) {
                System.out.println("[SettlerCraft]: World '" + world + "' has " + structures.size() + " structures that were placed after the last world save");
            }
            Iterator<Structure> it = structures.iterator();
            World w = Bukkit.getWorld(world);
            RegionManager rmgr = WorldGuardUtil.getGlobalRegionManager(w);
            Date removeDate = new Date();
            while (it.hasNext()) {
                Structure structure = it.next();
                rmgr.removeRegion(structure.getStructureRegion());
                System.out.println("[SettlerCraft]: " + structure.getStructureRegion() + " has been removed");
                structure.setState(State.REMOVED);
                structure.getLog().setRemovedAt(new Timestamp(removeDate.getTime()));
                structure.getLog().setAutoremoved(true);
                session.merge(structure);
            }

            try {
                rmgr.save();
                tx.commit();
            } catch (ProtectionDatabaseException ex) {
                Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

   

    
}
