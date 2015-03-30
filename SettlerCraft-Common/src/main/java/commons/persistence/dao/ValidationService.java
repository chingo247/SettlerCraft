package commons.persistence.dao;

//
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
//package com.chingo247.settlercraft.structureapi.persistence.hibernate;
//
//import com.chingo247.settlercraft.structureapi.structure.old.AbstractStructureAPI;
//import com.chingo247.settlercraft.structureapi.structure.QStructure;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure.State;
//import com.chingo247.settlercraft.util.WorldGuardUtil;
//import com.mysema.query.jpa.JPQLQuery;
//import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
//import com.mysema.query.jpa.hibernate.HibernateQuery;
//import com.sk89q.worldguard.protection.managers.RegionManager;
//import com.sk89q.worldguard.protection.managers.storage.StorageException;
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.bukkit.Bukkit;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
///**
// *
// * @author Chingo
// */
//public abstract class ValidationService {
//
//    protected final AbstractStructureAPI structureAPI;
//    
//    public ValidationService(AbstractStructureAPI structureAPI) {
//        this.structureAPI = structureAPI;
//    }
//
//    public void validate() {
//        deleteInvalidState();
//        HashMap<String, Timestamp> worlddata = getWorldData();
//
//        for (Entry<String, Timestamp> entry : worlddata.entrySet()) {
//            removeCreatedBefore(entry.getKey(), entry.getValue());
//            setRemovedAfter(entry.getKey(), entry.getValue());
//        }
//    }
//    
//    private void deleteInvalidState() {
//        Session session = HibernateUtil.getSession();
//        QStructure structure = QStructure.structure;
//        new HibernateDeleteClause(session, structure).where(structure.state.eq(State.INITIALIZING)).execute();
//        session.close();
//    }
//    
//
//    /**
//     * Gets the world data for all worlds that have/had structure tasks assigned within them
//     *
//     * @return Map of all structure-tasked worlds with worldname as key and timestamp as value
//     */
//    protected abstract HashMap<String, Timestamp> getWorldData();
//
//
//
//    /**
//     * Checks whether this world has structures
//     *
//     * @param world The world
//     * @return True if this world has structures
//     */
//    protected boolean isSettlerCraftWorld(String world) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QStructure qStructure = QStructure.structure;
//        boolean exists = query.from(qStructure).where(qStructure.location().world.eq(world)).exists();
//        session.close();
//        return exists;
//    }
//
//    private void setRemovedAfter(String world, Timestamp timestamp) {
//        Session session = null;
//        Transaction tx = null;
//        try {
//            session = HibernateUtil.getSession();
//            tx = session.beginTransaction();
//            QStructure qct = QStructure.structure;
//            JPQLQuery query = new HibernateQuery(session);
//            List<Structure> structures = query.from(qct).where(
//                    qct.logEntry().autoremoved.eq(Boolean.FALSE)
//                    .and(qct.location().world.eq(world))
//                    .and(qct.logEntry().completedAt.after(timestamp).or(qct.logEntry().removedAt.after(timestamp)))).list(qct);
//
//            if (!structures.isEmpty()) {
//                structureAPI.print("World " + world + " contains " + structures.size() + " that have an invalid status");
//                Iterator<Structure> it = structures.iterator();
//                RegionManager manager = WorldGuardUtil.getRegionManager(Bukkit.getWorld(world));
//                while (it.hasNext()) {
//                    Structure structure = it.next();
//
//                    if (!manager.hasRegion(structure.getStructureRegion())) {
//                        structureAPI.print("Structure #" + structure.getId() + " was removed after last save");
//                        reclaim(structure);
//                        structureAPI.print("Reclaimed region: " + structure.getStructureRegion());
//                    }
//
//                    // If structure was completed after world save
//                    if (timestamp.getTime() < structure.getLog().getCompletedAt().getTime()) {
//                        structure.setState(State.STOPPED);
//                        structure.getLog().setCompletedAt(null);
//                    } else {
//                        structure.setState(State.COMPLETE);
//                    }
//                    structure.getLog().setRemovedAt(null);
//                    session.merge(structure);
//                }
//                manager.save();
//
//                tx.commit();
//            }
//        } catch (HibernateException e) {
//            try {
//                if(tx != null) {
//                    tx.rollback();
//                }
//            } catch (HibernateException rbe) {
//                java.util.logging.Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
//            }
//            throw e;
//        } catch (StorageException ex) {
//            Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//    }
//
//    protected abstract void reclaim(Structure structure);
//    
//    protected abstract void removeRegion(Structure structure, String world);
//
//    private void removeCreatedBefore(String world, Timestamp timestamp) {
//        Session session = null;
//        Transaction tx = null;
//        try {
//            session = HibernateUtil.getSession();
//            tx = session.beginTransaction();
//            QStructure qct = QStructure.structure;
//            JPQLQuery query = new HibernateQuery(session);
//            List<Structure> structures = query.from(qct).where(qct.logEntry().createdAt.after(timestamp)
//                    .and(qct.location().world.eq(world))
//                    .and(qct.state.ne(State.REMOVED))).list(qct);
//            if (!structures.isEmpty()) {
//                structureAPI.print("World '" + world + "' has " + structures.size() + " structures that were placed after the last world save");
//            }
//            Iterator<Structure> it = structures.iterator();
//            
//            Date removeDate = new Date();
//            while (it.hasNext()) {
//                Structure structure = it.next();
//                removeRegion(structure, world);
//                structureAPI.print("Region: " + structure.getStructureRegion() + " has been removed");
//                structure.setState(State.REMOVED);
//                structure.getLog().setRemovedAt(new Timestamp(removeDate.getTime()));
//                structure.getLog().setAutoremoved(true);
//                session.merge(structure);
//            }
//
//            try {
//                saveChanges(world);
//                tx.commit();
//            } catch (StorageException ex) {
//                Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        } catch (HibernateException e) {
//            try {
//                if(tx != null) {
//                    tx.rollback();
//                }
//            } catch (HibernateException rbe) {
//                java.util.logging.Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
//            }
//            throw e;
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//    }
//    
//    protected abstract boolean firstTime(String world) throws IOException;
//
//    protected abstract void saveChanges(String world) throws StorageException;
//    
//   
//    
//
//}
