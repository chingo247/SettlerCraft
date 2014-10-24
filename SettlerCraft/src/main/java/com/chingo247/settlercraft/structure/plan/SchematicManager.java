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

package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.persistence.service.AbstractService;
import com.chingo247.settlercraft.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.plan.data.schematic.QSchematicData;
import com.chingo247.settlercraft.structure.plan.data.schematic.Schematic;
import com.chingo247.settlercraft.structure.plan.data.schematic.SchematicData;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicManager {
    
    private final Map<Long, Schematic> schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
    private static SchematicManager instance;
    private final ExecutorService executor = SettlerCraft.getInstance().getExecutorService();
    
    private SchematicManager() {}
    
    public static SchematicManager getInstance() {
        if(instance == null) {
            instance = new SchematicManager();
        }
        return instance;
    }
    
    /**
     * Gets the schematic for this file, if the schematic isn't present in the map of schematics, then the file will be loaded.
     * This method doesn't return null unless an exception was thrown
     * @param schematic The schematic file
     * @return The schematic for this file
     * @throws IOException
     * @throws DataException 
     */
    public Schematic getSmartSchematic(File schematic) throws IOException, DataException {
        long checksum = FileUtils.checksumCRC32(schematic);
        Schematic s = schematics.get(checksum);
        if(s != null) {
            return s;
        }
        s = Schematic.load(schematic);
        schematics.put(checksum, s);
        
        return s;
    }
    
    /**
     * Loads the schematic and stores it in the hashmap, from which it's available by the file's checksum
     * @param schematic
     * @throws IOException
     * @throws DataException 
     */
    public void load(File schematic) throws IOException, DataException {
        long checksum = FileUtils.checksumCRC32(schematic);
        if(schematics.get(checksum) != null) {
            Schematic s = Schematic.load(schematic);
            schematics.put(s.getCheckSum(), s);
        }
    }
    
    public Schematic getSchematic(long checksum) {
        return schematics.get(checksum);
    }
    
    public boolean hasSchematic(long checksum) {
        return schematics.get(checksum) != null;
    }
    
    public boolean hasSchematic(StructurePlan plan) throws IOException {
        long checkskum = FileUtils.checksumCRC32(plan.getSchematic());
        return hasSchematic(checkskum);
    }
    
    /**
     * Loads all schematics
     */
    void loadData() {
        List<File> schms = new LinkedList<>();
        for(SettlerCraftPlan plan : SettlerCraftPlanManager.getInstance().getPlans()) {
            schms.add(plan.getSchematic());
        }
        filter(schms);
        save(schms);
    }
    
    
    private void filter(final List<File> schematics) {
        Set<Long> checksums = new HashSet<>();
        Iterator<File> it = schematics.iterator();
        while(it.hasNext()) {
            File file = it.next();
            try {
                long checksum = FileUtils.checksumCRC32(file);
                QSchematicData qsd = QSchematicData.schematicData;
                if(exists(checksum) || checksums.contains(checksum)){
                    it.remove();
                } else {
                    checksums.add(checksum);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean exists(long checksum) throws IOException {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QSchematicData qsd = QSchematicData.schematicData;
        boolean exists  = query.from(qsd).where(qsd.checksum.eq(checksum)).exists();
        session.close();
        return exists;
    }
    
    
    private void save(final List<File> schematics) {
        
        final List<SchematicData> data = new LinkedList<>();
        List<Future> tasks = new LinkedList<>();
        
        for(final File file : schematics) {
            tasks.add(executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        data.add(SchematicData.load(file));
                    } catch (IOException | DataException ex) {
                        Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }));
        }
        
        for(Future task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
                for(Future f : tasks) {
                    f.cancel(true);
                }
            }
        }
        
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            for(SchematicData schematicData : data) {
                session.merge(schematicData);
            }
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Gets data about the schematic, doesn't contain the CuboidClipboard
     * @param checksum The checksum
     * @return The schematicData
     */
    SchematicData getData(long checksum) {
        Session session = HibernateUtil.getSession();
        QSchematicData qsd = QSchematicData.schematicData;
        SchematicData cachedEntry = (SchematicData) session.get(SchematicData.class, checksum);
        if(cachedEntry != null) {
            session.close();
            return cachedEntry;
        }
        JPQLQuery query = new HibernateQuery(session);
        SchematicData data = query.from(qsd).where(qsd.checksum.eq(checksum)).uniqueResult(qsd);
        session.close();
        return data;
    }
    
    
 
    
    
    


    
}
