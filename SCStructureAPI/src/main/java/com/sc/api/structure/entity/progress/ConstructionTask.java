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
package com.sc.api.structure.entity.progress;

import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.entity.Structure;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Version;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionTask implements Serializable {

    @Id
    @Column(name = "TASK_ID")
    private Long id;

    private int jobSize;
    
    private final String placer;

    @Embedded
    private ConstructionTaskData constructionTaskData;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "TASK_ID", referencedColumnName = "STRUCTURE_ID")
    private Structure structure;
    
    @ManyToOne
    @Cascade(CascadeType.ALL)
    private final ConstructionEntry constructionEntry;

    private Timestamp completeAt;

    private Timestamp removeDate;
    
    @Column(insertable = false, updatable = false)
    private final Timestamp createdAt;

    @Version
    private Timestamp lastModified;

    public enum ConstructionType {
        BUILDING_AUTO,
        DEMOLISHING_AUTO,
        MANUAL
    }

    private State constructionState;

    private final ConstructionType constructionType;

    private final ConstructionStrategyType strategyType;

    

    protected ConstructionTask() {
        this.constructionType = null;
        this.strategyType = null;
        this.constructionEntry = null;
        this.createdAt = null;
        this.placer = null;
    }

    public ConstructionTask(String placer, ConstructionEntry entry, Structure structure, ConstructionType constructionType, ConstructionStrategyType strategyType) {
        this.constructionType = constructionType;
        this.strategyType = strategyType;
        this.constructionEntry = entry;
        int count = 0;
        for (Countable<BaseBlock> b : structure.getPlan().getSchematic().getBlockDistributionWithData()) {
            count += b.getAmount();
        }
        this.createdAt = new Timestamp(new Date().getTime());
        this.jobSize = count;
        this.constructionState = State.QUEUED;
        this.structure = structure;
        this.id = structure.getId();
        this.constructionTaskData = new ConstructionTaskData(structure.getPlan().getPrice(), structure.getDimension(), structure.getStructureRegion());
        this.placer = placer;
    }

    public String getPlacer() {
        return placer;
    }

    
    
    public ConstructionEntry getConstructionEntry() {
        return constructionEntry;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    

    public void setState(State newState) {
        if (newState == State.COMPLETE) {
            this.completeAt = new Timestamp(new Date().getTime());
        } else if (newState == State.REMOVED) {
            this.removeDate = new Timestamp(new Date().getTime());
        }
        this.constructionState = newState;
    }

    public Timestamp getCompleteAt() {
        return completeAt;
    }

    public ConstructionTaskData getData() {
        return constructionTaskData;
    }

    public Location getSignLocation() {
        if (structure == null) {
            return null;
        }
        Vector pos = structure.getLocation().getPosition().add(new BlockVector(0, 1, 0));
//        System.out.println("signLoc: " + structure.getLocation());
        Location l = new Location(structure.getLocation().getWorld(), pos);
        return l;
    }

    public final State getState() {
//        Sign sign = WorldUtil.getSign(getSignLocation()) == null ? WorldUtil.createSign(structure.getLocation(), structure.getCardinal()) : WorldUtil.getSign(getSignLocation());
//        sign.setLine(2, getState().name());
        return constructionState;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public Long getId() {
        return id;
    }

    public Structure getStructure() {
        return structure;
    }

    public ConstructionType getConstructionType() {
        return constructionType;
    }

    public ConstructionStrategyType getStrategyType() {
        return strategyType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConstructionTask other = (ConstructionTask) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @author Chingo
     */
    public enum State {
        /**
         * Task has been issued
         */
        QUEUED,
        /**
         * Task has been completed
         */
        COMPLETE,
        /**
         * Task has been canceled and will continue as DEMOLISION task
         */
        CANCELED,
        /**
         * Task has been marked for removal (structure still exists)
         */
        REMOVED,
    }

}
