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

package com.chingo247.settlercraft.structure.entities.structure;

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
public class StructureLog implements Serializable {

    @Column(updatable = false)
    private Timestamp createdAt;
    private Timestamp completedAt;
    private Timestamp removedAt;
    private Boolean autoremoved = false;

    protected StructureLog() {
        this.createdAt = new Timestamp(new Date().getTime());
    }

    public void setRemovedAt(Date removedAt) {
        if (removedAt != null) {
            this.removedAt = new Timestamp(removedAt.getTime());
        } else {
            this.removedAt = null;
        }
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public Timestamp getRemovedAt() {
        return removedAt;
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
