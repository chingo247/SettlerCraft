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

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class SCOrientDatabase {

    private final Logger LOG = Logger.getLogger(OrientDocumentDatabase.class);
    private final ODatabaseDocumentTx database;
    protected final String name;
    protected final String path;
    

    public SCOrientDatabase() {
        this.path = "databases/";
        this.name = "settlercraft";

        database = new ODatabaseDocumentTx(OrientMode.LOCAL.mode + ":" + path + name);

        if (!database.exists()) {
            database.create();
        }
    }
    
    public static void main(String[] args) {
        SCOrientDatabase db = new SCOrientDatabase();
    }

}
