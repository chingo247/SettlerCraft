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
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.persistence.service.StructureDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structure.restriction.StructureHeightRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureOverlapRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureWorldRestriction;
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
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public abstract class SCWorld implements World {

    private final Logger LOG = Logger.getLogger(SCWorld.class);
    protected final ExecutorService EXECUTOR;
    private final Map<Long, Structure> STRUCTURES;
    private final StructureDAO STRUCTURE_DAO = new StructureDAO();
    private final IWorld WORLD;
    private WorldConfig worldConfig;
    private List<StructureRestriction> restrictions;

    protected SCWorld(ExecutorService executor, IWorld world) {
        this.EXECUTOR = executor;
        this.WORLD = world;
        this.STRUCTURES = new HashMap<>();
        this.restrictions = new ArrayList<>();
        this.restrictions.add(new StructureWorldRestriction());
        this.restrictions.add(new StructureHeightRestriction());
        this.restrictions.add(new StructureOverlapRestriction());
        // TODO ADD GLOBAL RESTRICTIONS
    }

    @Override
    public UUID getUniqueId() {
        return WORLD.getUUID();
    }

    @Override
    public String getName() {
        return WORLD.getName();
    }

    @Override
    public WorldConfig getConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createStructure(StructurePlan plan, Vector position, Direction direction) {

    }

    @Override
    public void createStructure(Placement placement, Vector postion, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Structure> getStructures() {
        return new ArrayList<>(STRUCTURES.values());
    }

    @Override
    public Structure getStructure(long id) {
        return STRUCTURES.get(id);
    }

    List<Structure> _getSubstructures(long id) {
        List<Long> ids = getSubstructuresIds(id);
        List<Structure> ss = new ArrayList<>(ids.size());
        for (Long i : ids) {
            ss.add(getStructure(i));
        }
        return ss;
    }

    private Structure _prepare(StructureEntity structureEntity) {
        Structure structure = null;
        synchronized (STRUCTURES) {
            structure = STRUCTURES.get(structureEntity.getId());
            if (structure == null) {
                structure = handleStructure(structureEntity);
                STRUCTURES.put(structure.getId(), structure);
            }
        }
        return structure;
    }

    void _load() {
        worldConfig = getConfig();
        List<StructureEntity> entities = STRUCTURE_DAO.getStructureForWorld(getUniqueId());
        for (StructureEntity structureEntity : entities) {
            _prepare(structureEntity);
        }
    }

    protected abstract Structure handleStructure(StructureEntity entity);

    private List<Long> getSubstructuresIds(long id) {
        QStructureEntity qse = QStructureEntity.structureEntity;
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        List<Long> entities = query.from(qse).where(qse.parent.eq(id)).list(qse.id);
        session.close();
        return entities;
    }

}
