/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionSite implements Serializable {
    
    public enum State {
        WAITING,
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
         * Progress has stopped
         */
        STOPPED
    }
    
    @Id
    @GeneratedValue
    @Column(name = "CSITE_ID")
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(referencedColumnName = "SC_STRUCTURE_ID", name = "CSITE_ID")
    private Structure structure;   
    private Timestamp createdAt;
    private Timestamp completedAt;
    private Timestamp removedAt;
    private Boolean autoRemoved = false;
    private State state = State.WAITING;
    
    /**
     * JPA Constructor.
     */
    protected ConstructionSite() {}
    
    ConstructionSite(Structure structure) {
        this.createdAt = new Timestamp(new Date().getTime());
        this.structure = structure;
    }

    public Long getId() {
        return id;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = new Timestamp(completedAt.getTime());
    }

    public Timestamp getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(Date removedAt) {
        this.removedAt = new Timestamp(removedAt.getTime());
    }

    public Boolean isAutoRemoved() {
        return autoRemoved;
    }

    public void setAutoRemoved(Boolean autoRemoved) {
        this.autoRemoved = autoRemoved;
    }

    public State getState() {
        return state;
    }

    public void setState(State progressStatus) {
        this.state = progressStatus;
    }
    
    
    
}
