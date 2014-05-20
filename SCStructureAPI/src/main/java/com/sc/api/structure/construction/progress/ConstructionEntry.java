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
    private final String entryName; 
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<ConstructionTask> constructionQueue;

    protected ConstructionEntry() {
        this.entryName = null;
    }

    /**
     * Constructor
     * @param entryName The entryName originally was designed to be restricted to playerNames, however
     * due to future support with factions players might be able to register structures (and their construction progress) under the name of a faction.
     * This way the bps (blocks-per-second) for structure placement can be increased without overloading the server
     */
    public ConstructionEntry(String entryName) {
        this.entryName = entryName;
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
        return entryName;
    }
    
    


   
    
}
