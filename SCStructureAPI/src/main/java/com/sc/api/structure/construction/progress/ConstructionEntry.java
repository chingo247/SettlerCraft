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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionEntry implements Serializable {
    
    
    @Id
    private final String player; 
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<ConstructionTask> constructionQueue;

    protected ConstructionEntry() {
        this.player = null;
    }

    /**
     * Constructor
     * @param issuer The player or issuer
     */
    public ConstructionEntry(String issuer) {
        this.player = issuer;
        this.constructionQueue = new ArrayList<>();
    }

    public boolean add(ConstructionTask constructionProgress) {
        return constructionQueue.add(constructionProgress);
    }

    public ConstructionTask remove(ConstructionTask constructionProgress) {
        Iterator<ConstructionTask> it = constructionQueue.iterator();
        while(it.hasNext()) {
            ConstructionTask cp = it.next();
            if(cp.getId().equals(constructionProgress.getId())) {
                it.remove();
                return cp;
            }
        }
        return null;
    }

    public String getEntryName() {
        return player;
    }



    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.player);
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
        if (!Objects.equals(this.player, other.player)) {
            return false;
        }
        return true;
    }
    
    


   
    
}
