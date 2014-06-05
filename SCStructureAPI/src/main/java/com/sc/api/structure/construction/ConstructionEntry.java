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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class ConstructionEntry {
    
    private final HashMap<Integer, ConstructionProgress> progressQueue;

    ConstructionEntry() {
        progressQueue = new HashMap<>();
    }
    
    public List<ConstructionProgress> list() {
        return new ArrayList<>(progressQueue.values());
    }
    

    public ConstructionProgress get(Integer key) {
        return progressQueue.get(key);
    }

    public boolean containsKey(Integer key) {
        return progressQueue.containsKey(key);
    }

    public ConstructionProgress put(Integer key, ConstructionProgress value) {
        return progressQueue.put(key, value);
    }

    public ConstructionProgress remove(Integer key) {
        return progressQueue.remove(key);
    }
    
    
    
}
