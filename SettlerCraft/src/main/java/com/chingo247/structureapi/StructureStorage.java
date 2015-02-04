/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi;

import com.chingo247.structureapi.entities.QStructureEntity;
import com.chingo247.structureapi.entities.QStructureTreeEntity;
import com.chingo247.structureapi.entities.StructureEntity;
import com.chingo247.structureapi.persistence.HibernateUtil;
import com.chingo247.structureapi.persistence.StructureDAO;
import com.chingo247.structureapi.regions.CuboidDimension;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;

/**
 * In memory storage for StructureData
 * @author Chingo
 */
public class StructureStorage {
    
    private static final int PARTITION_SIZE = 512;
    private final Map<String, StructureWorldPartition> storage;
    private final StructureDAO sdao;
    
    StructureStorage() {
        this.storage = Collections.synchronizedMap(new HashMap<String, StructureWorldPartition>());
        this.sdao = new StructureDAO();
    }
    
    public StructureComplex find(long id) {
        StructureEntity entity = sdao.find(id);
        if(entity == null) {
            return null;
        }
        
        return find(entity);
    }
    
    private StructureComplex find(StructureEntity entity) {
        if(entity == null) return null;
        
        StructureWorldPartition worldPartition = storage.get(entity.getWorld().getName());
        if(worldPartition == null) {
            return null;
        }
        
        StructurePartition partition = worldPartition.worldStorage.get(getPartition(entity.getDimension()));
        if(partition == null) {
            return null;
        }
        return partition.structures.get(entity.getId());
    }
    
    public Structure getParent(Long id) {
        Session session = HibernateUtil.getSession();
        QStructureTreeEntity qste = QStructureTreeEntity.structureTreeEntity;
        QStructureEntity qse = QStructureEntity.structureEntity;
        HibernateQuery query = new HibernateQuery(session);
        StructureEntity parent = query.from(qste).where(qste.child.eq(id).and(qse.id.eq(qste.parent))).singleResult(qse);
        session.close();
        return find(parent);
    }

    public List<Structure> getSubStructures(Long id) {
        Session session = HibernateUtil.getSession();
        QStructureTreeEntity qste = QStructureTreeEntity.structureTreeEntity;
        QStructureEntity qse = QStructureEntity.structureEntity;
        HibernateQuery query = new HibernateQuery(session);
        List<StructureEntity> children = query.from(qste, qse).where(qste.parent.eq(id).and(qse.id.eq(qste.child))).list(qse);
        session.close();
        
        List<Structure> structures = new ArrayList<>(children.size());
        for(StructureEntity se : children) {
            StructureComplex complex = find(se);
            if(complex != null) {
                structures.add(complex);
            }
        }
        return structures;
    }
    
    public void store(StructureComplex complex) {
        String world = complex.getWorld().getName();
        StructureWorldPartition worldPartition = storage.get(world);
        synchronized(storage) {
            if(worldPartition == null) {
                worldPartition = new StructureWorldPartition();
                storage.put(world, worldPartition);
            }
        }
        worldPartition.store(complex);
    }
    
    private class StructureWorldPartition {
        final Map<Integer, StructurePartition> worldStorage = Collections.synchronizedMap(new HashMap<Integer, StructurePartition>());
        
        public void store(StructureComplex complex) {
            int partition = getPartition(complex.getCuboidRegion());
            StructurePartition structurePartition = worldStorage.get(partition);
            
            synchronized(worldStorage) {
                if(structurePartition == null) {
                    structurePartition = new StructurePartition();
                    worldStorage.put(partition, structurePartition);
                }
            }
        }
    }
    
    private class StructurePartition {
        Map<Long, StructureComplex> structures = Collections.synchronizedMap(new HashMap<Long, StructureComplex>());
        
        public void store(StructureComplex complex) {
            structures.put(complex.getId(), complex);
        }
    }
    
    private static int getPartition(CuboidRegion region) {
        return getPartition(new CuboidDimension(region.getMinimumPoint(), region.getMaximumPoint()));
    }
    
    private static int getPartition(CuboidDimension dimension) {
        CuboidRegion region = new CuboidRegion(dimension.getMinPosition(), dimension.getMaxPosition());
        Vector v = region.getCenter();
        
        System.out.println(v);
        
        int xP = compute(v.getBlockX());
        int zP = compute(v.getBlockZ());
        
        return 0;
    }
    
    private static int compute(int number) {
        int x = number % PARTITION_SIZE;
        number -= x;
        number /= PARTITION_SIZE;
        System.out.println(number);
        return number;
    }
    
    
    
}
