/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureLogEntry implements Serializable {

    @Column(updatable = false)
    private Timestamp createdAt;
    private Timestamp completedAt;
    private Timestamp removedAt;
    private Boolean autoremoved = false;

    protected StructureLogEntry() {
        this.createdAt = new Timestamp(new Date().getTime());
    }

    public void setRemovedAt(Date removedAt) {
        if (removedAt != null) {
            this.removedAt = new Timestamp(removedAt.getTime());
        } else {
            this.removedAt = null;
        }
    }


    public void setCompletedAt(Date completedAt) {
        if (completedAt != null) {
            this.completedAt = new Timestamp(completedAt.getTime());
        } else {
            this.completedAt = null;
        }
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setAutoremoved(Boolean autoremoved) {
        this.autoremoved = autoremoved;
    }

    public Boolean isAutoremoved() {
        return autoremoved;
    }

}