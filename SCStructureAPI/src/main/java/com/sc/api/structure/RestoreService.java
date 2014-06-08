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
package com.sc.api.structure;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.persistence.HibernateUtil;
import com.sc.persistence.service.AbstractService;
import com.sc.util.SCWorldGuardUtil;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
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
            setRemovedAfter(entry.getKey(), entry.getValue());
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
        boolean exists = query.from(qStructure).where(qStructure.worldUUID.eq(world.getUID())).exists();
        session.close();
        return exists;
    }

    private void setRemovedAfter(String world, Timestamp timestamp) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            QStructure qct = QStructure.structure;
            JPQLQuery query = new HibernateQuery(session);
            List<Structure> structures = query.from(qct).where(qct.progress().autoRemoved.eq(Boolean.FALSE).and(qct.progress().completedAt.after(timestamp).or(qct.progress().removedAt.after(timestamp)))).list(qct);
            Iterator<Structure> it = structures.iterator();
            while (it.hasNext()) {
                Structure structure = it.next();
                structure.getProgress().setProgressStatus(ConstructionProcess.State.STOPPED);
                session.merge(structure);
            }
            tx.commit();
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

    private void removeCreatedBefore(String world, Timestamp timestamp) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            QStructure qct = QStructure.structure;
            JPQLQuery query = new HibernateQuery(session);
            List<Structure> structures = query.from(qct).where(qct.progress().createdAt.after(timestamp)).list(qct);
            if (!structures.isEmpty()) {
                System.out.println("[SCStructureAPI]:" + world + " has " + structures.size() + " structures that were placed after the last save");
            }
            Iterator<Structure> it = structures.iterator();
            World w = Bukkit.getWorld(world);
            RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(w);
            Date removeDate = new Date();
            while (it.hasNext()) {
                Structure structure = it.next();
                rmgr.removeRegion(structure.getStructureRegion());
                System.out.println("[SCStructureAPI]: " + structure.getStructureRegion() + " has been removed");
                structure.getProgress().setProgressStatus(ConstructionProcess.State.REMOVED);
                structure.getProgress().setRemovedAt(new Timestamp(removeDate.getTime()));
                structure.getProgress().setAutoRemoved(true);
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
