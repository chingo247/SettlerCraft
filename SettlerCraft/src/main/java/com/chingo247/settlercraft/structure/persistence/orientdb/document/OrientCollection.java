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
package com.chingo247.settlercraft.structure.persistence.orientdb.document;

import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;

/**
 *
 * @author Chingo
 * @param <T>
 */
public class OrientCollection<T extends OrientDocumentable> {
    
    private final String collection;
    private final OrientDocumentDatabase orientDB;

    public OrientCollection(OrientDocumentDatabase database, String collection) {
        this.collection = collection;
        this.orientDB = database;
    }
    
    public ORecordIteratorClass<ODocument> browse() {
        return orientDB.getDatabase().browseClass(collection);
    }
    
    public void save(T t) {
        if(orientDB.isClosed()) {
            orientDB.open();
        }
        orientDB.getDatabase().begin();
        t.asDocument().save();
        orientDB.getDatabase().commit();
        orientDB.close();
    }
    
    public void saveBulk(List<T> ts, int bulkSize) {
        if(orientDB.isClosed()) {
            orientDB.open();
        }
        
        orientDB.getDatabase().begin();
        int count = 0;
        for(T t : ts) {
            t.asDocument().save();
            count++;
            if(count % bulkSize == 0) {
                orientDB.getDatabase().commit();
                orientDB.getDatabase().begin();
            }
        }
        orientDB.getDatabase().commit();
        orientDB.getDatabase().close();
    }
    
    
}
