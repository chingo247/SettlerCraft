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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionEntry implements Serializable {

    @Id
    private final String entryName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "constructionEntry")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ConstructionTask> tasks;

    protected ConstructionEntry() {
        this.entryName = null;
    }

    /**
     * Constructor
     *
     * @param issuer The player or issuer
     */
    public ConstructionEntry(String issuer) {
        this.entryName = issuer;
        this.tasks = new ArrayList<>();
    }

    public boolean add(ConstructionTask constructionProgress) {
        return tasks.add(constructionProgress);
    }

    public List<ConstructionTask> getTasks() {
        return tasks;
    }
    
    public ConstructionTask remove(ConstructionTask constructionProgress) {
        Iterator<ConstructionTask> it = tasks.iterator();
        while (it.hasNext()) {
            ConstructionTask cp = it.next();
            if (cp.getId().equals(constructionProgress.getId())) {
                it.remove();
                return cp;
            }
        }
        return null;
    }

    public String getEntryName() {
        return entryName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.entryName);
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
        final ConstructionEntry other = (ConstructionEntry) obj;
        if (!Objects.equals(this.entryName, other.entryName)) {
            return false;
        }
        return true;
    }

}
