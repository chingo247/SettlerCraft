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
package com.chingo247.structureapi.persistence.service;

import com.chingo247.structureapi.Dimension;
import com.chingo247.structureapi.PlayerMembership;
import com.chingo247.structureapi.PlayerOwnership;
import com.chingo247.structureapi.QStructure;
import com.chingo247.structureapi.Structure;
import com.chingo247.structureapi.Structure.State;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.structureapi.util.WorldGuardUtil;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
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

    private final StructureAPI structureAPI;
    
    public RestoreService(StructureAPI structureAPI) {
        this.structureAPI = structureAPI;
    }

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
            try {
                if (!firstTime(world)) {
                    continue;
                }
            } catch (IOException ex) {
                continue;
            }

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
     * Checks whether this world has structures
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
            List<Structure> structures = query.from(qct).where(
                    qct.logEntry().autoremoved.eq(Boolean.FALSE)
                    .and(qct.location().worldUUID.eq(world.getUID()))
                    .and(qct.logEntry().completedAt.after(timestamp).or(qct.logEntry().removedAt.after(timestamp)))).list(qct);

            if (!structures.isEmpty()) {
                StructureAPI.print("World " + world + " contains " + structures.size() + " that have an invalid status");
                Iterator<Structure> it = structures.iterator();
                RegionManager manager = WorldGuardUtil.getRegionManager(Bukkit.getWorld(world.getName()));
                while (it.hasNext()) {
                    Structure structure = it.next();

                    if (!manager.hasRegion(structure.getStructureRegion())) {
                        StructureAPI.print("Structure #" + structure.getId() + " was removed after last save");
                        reclaim(structure);
                        StructureAPI.print("Reclaimed region: " + structure.getStructureRegion());
                    }

                    // If structure was completed after world save
                    if (timestamp.getTime() < structure.getLog().getCompletedAt().getTime()) {
                        structure.setState(State.STOPPED);
                        structure.getLog().setCompletedAt(null);
                    } else {
                        structure.setState(State.COMPLETE);
                    }
                    structure.getLog().setRemovedAt(null);
                    session.merge(structure);
                }
                manager.save();

                tx.commit();
            }
        } catch (HibernateException e) {
            try {
                if(tx != null) {
                    tx.rollback();
                }
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } catch (StorageException ex) {
            Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private synchronized void reclaim(Structure structure) {
        World world = Bukkit.getWorld(structure.getLocation().getWorld().getUID());
        if(world == null) return;
        RegionManager mgr = WorldGuardUtil.getRegionManager(world);
        if(mgr == null) return;
        
        
        Dimension dim = structure.getDimension();
        Vector p1 = dim.getMinPosition();
        Vector p2 = dim.getMaxPosition();
        String id = structure.getStructureRegion();

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        region.setOwners(new DefaultDomain());

        PlayerOwnershipService pos = new PlayerOwnershipService();

        // Set Owners
        for (PlayerOwnership owner : pos.getOwners(structure)) {
            LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(owner.getPlayerUUID()));

            region.getOwners().addPlayer(lp);
        }

        PlayerMembershipService pms = new PlayerMembershipService();

        // Set Owners
        for (PlayerMembership member : pms.getMembers(structure)) {
            LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(member.getUUID()));
            region.getMembers().addPlayer(lp);
        }

        mgr.addRegion(region);
        
        try {
            mgr.save();
        } catch (StorageException ex) {
            Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
        }

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
                StructureAPI.print("World '" + world + "' has " + structures.size() + " structures that were placed after the last world save");
            }
            Iterator<Structure> it = structures.iterator();
            World w = Bukkit.getWorld(world);
            if(w == null) return;
            
            RegionManager rmgr = WorldGuardUtil.getRegionManager(w);
            if(rmgr == null) return;
            
            Date removeDate = new Date();
            while (it.hasNext()) {
                Structure structure = it.next();
                rmgr.removeRegion(structure.getStructureRegion());
                StructureAPI.print("Region: " + structure.getStructureRegion() + " has been removed");
                structure.setState(State.REMOVED);
                structure.getLog().setRemovedAt(new Timestamp(removeDate.getTime()));
                structure.getLog().setAutoremoved(true);
                session.merge(structure);
            }

            try {
                rmgr.save();
                tx.commit();
            } catch (StorageException ex) {
                Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (HibernateException e) {
            try {
                if(tx != null) {
                    tx.rollback();
                }
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

    private boolean firstTime(World world) throws IOException {
        File worldsFolder = new File(structureAPI.getStructureDataFolder(), "/restoreservice");
        worldsFolder.mkdirs();
        File scWorldFile = new File(worldsFolder, world.getName());
        File lockFile = new File(world.getWorldFolder(), "session.lock");

        if (scWorldFile.exists()) {
            if (FileUtils.contentEquals(scWorldFile, lockFile)) {
                return false;
            } else {
                FileUtils.copyFile(lockFile, scWorldFile);
                return true;
            }

        }
        FileUtils.copyFile(lockFile, scWorldFile);

        return true;

    }

}
