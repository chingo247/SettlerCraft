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
package com.sc.api.structure.construction;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionProgress implements Serializable {

    public enum State {
        INITIALIZED,
        /**
         * Structure has been issued
         */
        QUEUED,
        /**
         * Structure is being build
         */
        BUILDING,
        /**
         * Structure is being demolished
         */
        DEMOLISHING,
        /**
         * Progress has been completed
         */
        COMPLETE,
        /**
         * Structure has been removed
         */
        REMOVED,
        /**
         * Progress har stopped
         */
        STOPPED
    }

    @Id
    private Long id;
    private double refundValue;
    private Timestamp createdAt;
    private Timestamp completedAt;
    private Timestamp removedAt;
    private boolean isDemolishing;
    private State progressStatus;
    @Transient
    private int jobId = -1;
    
    @OneToOne
    @NotNull
    @Cascade(CascadeType.ALL)
    private Structure structure;

    protected ConstructionProgress() {}

    public ConstructionProgress(Structure structure) {
        Preconditions.checkNotNull(structure);
        this.id = structure.getId();
        this.refundValue = structure.getPlan().getPrice();
        this.createdAt = new Timestamp(new Date().getTime());
        this.isDemolishing = false;
        this.progressStatus = State.INITIALIZED;
        this.structure = structure;
    }

    public Long getId() {
        return id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
    

    public double getRefundValue() {
        return refundValue;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public Timestamp getRemovedAt() {
        return removedAt;
    }

    public boolean isDemolishing() {
        return isDemolishing;
    }

    public State getProgressStatus() {
        return progressStatus;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setRefundValue(double refundValue) {
        this.refundValue = refundValue;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public void setRemovedAt(Timestamp removedAt) {
        this.removedAt = removedAt;
    }

    public void setIsDemolishing(boolean isDemolishing) {
        this.isDemolishing = isDemolishing;
    }

    public void setProgressStatus(State progressStatus) {
        this.progressStatus = progressStatus;
    }
    
    
    
    

}
