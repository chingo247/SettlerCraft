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
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureType;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.persistence.service.StructureDAO;
import com.chingo247.settlercraft.plan.StructurePlan;
import com.chingo247.settlercraft.plan.placement.Placement;
import com.chingo247.settlercraft.regions.CuboidDimension;
import com.chingo247.settlercraft.restriction.StructureHeightRestriction;
import com.chingo247.settlercraft.restriction.StructureOverlapRestriction;
import com.chingo247.settlercraft.restriction.StructureRestriction;
import com.chingo247.settlercraft.restriction.StructureWorldRestriction;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.world.World;
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
public abstract class SCWorld extends World {

    private final Logger LOG = Logger.getLogger(SCWorld.class);
    protected final ExecutorService executor;
    private final Map<Long, Structure> structures;
    private final StructureDAO structureDao = new StructureDAO();
    private final SettlerCraft settlerCraft;
    private WorldConfig worldConfig;
    private List<StructureRestriction> restrictions;
    

    protected SCWorld(ExecutorService executor, SettlerCraft settlerCraft, IWorld world) {
        super(world);
        this.executor = executor;
        this.structures = new HashMap<>();
        this.restrictions = new ArrayList<>();
//        this.restrictions.add(new StructureWorldRestriction());
        this.restrictions.add(new StructureHeightRestriction());
        this.restrictions.add(new StructureOverlapRestriction());
        this.settlerCraft = settlerCraft;
        // TODO ADD GLOBAL RESTRICTIONS
    }

    

    @Override
    public WorldConfig getConfig() {
        if(worldConfig == null) {
            
        }
        return worldConfig;
    }

    @Override
    public Structure createStructure(StructurePlan plan, Vector position, Direction direction) {
        System.out.println("Creating structure...");
        
        CuboidDimension planDimension = plan.getPlacement().getCuboidDimension();
        CuboidDimension dimension = new CuboidDimension(planDimension.getMinPosition().add(position), planDimension.getMaxPosition().add(position));
        StructureEntity entity = new StructureEntity(worldName, worldUuid, dimension, StructureType.OTHER);
        entity = structureDao.save(entity);
        SCStructure structure = new SCStructure(settlerCraft, executor, entity, this, plan);
        return structure;
    }

    @Override
    public Structure createStructure(Placement placement, Vector postion, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Structure> getStructures() {
        return new ArrayList<>(structures.values());
    }

    @Override
    public Structure getStructure(long id) {
        return structures.get(id);
    }

    @Override
    public boolean overlapsStructures(CuboidDimension dimension) {
        return structureDao.overlaps(worldUuid, dimension);
    }
    
    

    List<Structure> _getSubstructures(long id) {
        List<Long> ids = getSubstructuresIds(id);
        List<Structure> ss = new ArrayList<>(ids.size());
        for (Long i : ids) {
            ss.add(getStructure(i));
        }
        return ss;
    }

    private Structure _prepare(StructureEntity structureEntity, ForkJoinPool pool) {
        Structure structure = null;
        synchronized (structures) {
            structure = structures.get(structureEntity.getId());
            if (structure == null) {
                SCStructure ss = handleStructure(structureEntity);
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
//            _prepare(structureEntity, pool);
        }
        pool.shutdown();
    }

    protected abstract SCStructure handleStructure(StructureEntity entity);

    private List<Long> getSubstructuresIds(long id) {
        QStructureEntity qse = QStructureEntity.structureEntity;
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        List<Long> entities = query.from(qse).where(qse.parent.eq(id)).list(qse.id);
        session.close();
        return entities;
    }

}
