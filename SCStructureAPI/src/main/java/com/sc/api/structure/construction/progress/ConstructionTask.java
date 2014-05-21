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
package com.sc.api.structure.construction.progress;

import com.sc.api.structure.model.Structure;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionTask implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private int jobSize;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Structure structure;
    
    @NotNull
    private Timestamp createdAt;
    
    private Timestamp completeAt;

    @Version
    private Timestamp lastModified;

    public enum ConstructionType {
        BUILDING_AUTO,
        DEMOLISHING_AUTO,
        MANUAL
    }

    private ConstructionState state;

    private ConstructionType constructionType;

    private final ConstructionStrategyType strategyType;

    
    @ManyToOne(cascade = CascadeType.ALL)
    private final ConstructionEntry constructionEntry;

    protected ConstructionTask() {
        this.constructionType = null;
        this.strategyType = null;
        this.constructionEntry = null;
    }

    public ConstructionTask(ConstructionEntry entry, Structure structure, ConstructionType constructionType, ConstructionStrategyType strategyType) {
        this.constructionType = constructionType;
        this.strategyType = strategyType;
        this.constructionEntry = entry;
        int count = 0;
        for (Countable<BaseBlock> b : structure.getPlan().getSchematic().getBlockDistributionWithData()) {
            count += b.getAmount();
        }
        this.jobSize = count;
        this.state = ConstructionState.PREPARING;
        this.structure = structure;
        this.createdAt = new Timestamp(new Date().getTime());
    }

    public ConstructionEntry getConstructionEntry() {
        return constructionEntry;
    }

//    public void setState(ConstructionState state) {
//        if(state == ConstructionState.FINISHED) {
//            this.completeAt = new Timestamp(new Date().getTime());
//        }
//        this.state = state;
//    }

    public Timestamp getCompleteAt() {
        return completeAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    

    public ConstructionState getState() {
        return state;
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


    
    

}
