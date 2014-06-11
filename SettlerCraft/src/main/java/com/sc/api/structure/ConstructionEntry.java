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

package com.sc.api.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class ConstructionEntry {
    
    private final Map<Integer, ConstructionProcess> progressQueue;
    
    ConstructionEntry() {
        progressQueue = Collections.synchronizedMap(new HashMap<Integer, ConstructionProcess>());
    }
    
    public List<ConstructionProcess> list() {
        return new ArrayList<>(progressQueue.values());
    }
    

    public ConstructionProcess get(Integer key) {
        return progressQueue.get(key);
    }

    public boolean containsKey(Integer key) {
        return progressQueue.containsKey(key);
    }

    public ConstructionProcess put(Integer key, ConstructionProcess process) {
        return progressQueue.put(key, process);
    }

    public ConstructionProcess remove(Integer key) {
        return progressQueue.remove(key);
    }

}
