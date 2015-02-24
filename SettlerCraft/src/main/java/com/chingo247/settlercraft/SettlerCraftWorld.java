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
package com.chingo247.settlercraft;

import com.chingo247.settlercraft.persistence.entities.structure.QStructureEntity;
import com.chingo247.settlercraft.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.persistence.service.StructureDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureType;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.structure.restriction.StructureHeightRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureOverlapRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureRestriction;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.xcore.core.IWorld;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sk89q.worldedit.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public abstract class SettlerCraftWorld {

    private final Logger LOG = Logger.getLogger(SettlerCraftWorld.class);
    protected final ExecutorService executor;
    private final Map<Long, SettlerCraftStructure> structures;
    private final StructureDAO structureDao = new StructureDAO();
    private final SettlerCraft settlerCraft;
    private WorldConfig worldConfig;
    private List<StructureRestriction> restrictions;
    
    protected final String worldName;
    protected final UUID worldUuid;
    
    protected SettlerCraftWorld(ExecutorService executor, SettlerCraft settlerCraft, IWorld iWorld) {
        this.executor = executor;
        this.structures = new HashMap<>();
        this.restrictions = new ArrayList<>();
//        this.restrictions.add(new StructureWorldRestriction());
        this.restrictions.add(new StructureHeightRestriction());
        this.restrictions.add(new StructureOverlapRestriction());
        this.settlerCraft = settlerCraft;
        this.worldName = iWorld.getName();
        this.worldUuid = iWorld.getUUID();
        this.restrictions = new ArrayList<>();
        this.restrictions.add(new StructureHeightRestriction());
    }

     /**
     * Gets the world UUID
     * @return The worldUUID
     */
    public UUID getUniqueId() {
        return worldUuid;
    }
    
    /**
     * Gets the name of the world
     * @return The name of the world
     */
    public String getName() {
        return worldName;
    }

    
    public WorldConfig getConfig() {
        if(worldConfig == null) {
            
        }
        return worldConfig;
    }

    public SettlerCraftStructure createStructure(StructurePlan plan, Vector position, Direction direction) {
        System.out.println("Creating structure...");
        
        CuboidDimension planDimension = plan.getPlacement().getCuboidDimension();
        CuboidDimension dimension = new CuboidDimension(planDimension.getMinPosition().add(position), planDimension.getMaxPosition().add(position));
        StructureEntity entity = new StructureEntity(worldName, worldUuid, dimension, StructureType.OTHER, direction);
        entity = structureDao.save(entity);
        SettlerCraftStructure structure = new SettlerCraftStructure(settlerCraft, executor, entity, this, plan);
        return structure;
    }

    public SettlerCraftStructure createStructure(Placement placement, Vector postion, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<SettlerCraftStructure> getStructures() {
        return new ArrayList<>(structures.values());
    }

    public SettlerCraftStructure getStructure(long id) {
        return structures.get(id);
    }

    public boolean overlapsStructures(CuboidDimension dimension) {
        return structureDao.overlaps(worldUuid, dimension);
    }
    
    

    List<SettlerCraftStructure> _getSubstructures(long id) {
        List<Long> ids = getSubstructuresIds(id);
        List<SettlerCraftStructure> ss = new ArrayList<>(ids.size());
        for (Long i : ids) {
            ss.add(getStructure(i));
        }
        return ss;
    }

    private SettlerCraftStructure _prepare(StructureEntity structureEntity, ForkJoinPool pool) {
        SettlerCraftStructure structure = null;
        synchronized (structures) {
            structure = structures.get(structureEntity.getId());
            if (structure == null) {
                SettlerCraftStructure ss = handleStructure(structureEntity);
                ss._load(pool);
                structures.put(ss.getId(), structure);
                structure = ss;
            }
        }
        return structure;
    }

    void _load() {
//        worldConfig = getConfig();
        List<StructureEntity> entities = structureDao.getStructureForWorld(getUniqueId());
        ForkJoinPool pool = new ForkJoinPool();
        for (StructureEntity structureEntity : entities) {
                System.out.println("Prepare structure is off! in SCWorld#Load" );
//            _prepare(structureEntity, pool);
        }
        pool.shutdown();
    }

    protected abstract SettlerCraftStructure handleStructure(StructureEntity entity);
    
    protected StructurePlan getStructurePlan(StructureEntity entity) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    private List<Long> getSubstructuresIds(long id) {
        QStructureEntity qse = QStructureEntity.structureEntity;
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        List<Long> entities = query.from(qse).where(qse.parent.eq(id)).list(qse.id);
        session.close();
        return entities;
    }

}
