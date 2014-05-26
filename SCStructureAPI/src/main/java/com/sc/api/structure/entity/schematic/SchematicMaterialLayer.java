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
package com.sc.api.structure.entity.schematic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class SchematicMaterialLayer implements Serializable {

    protected final int layer;

    protected List<SchematicMaterialResource> resources = new ArrayList<>();

    /**
     * JPA Constructor
     */
    protected SchematicMaterialLayer() {
        this.layer = -1;
    }

    public SchematicMaterialLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public ArrayList<SchematicMaterialResource> getResources() {
        return new ArrayList<>(resources);
    }

    public int size() {
        return resources.size();
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    public void addResource(SchematicMaterialResource resource) {
        Iterator<SchematicMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            SchematicMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                mr.setAmount(mr.getAmount() + resource.getAmount());
                return;
            }
        }
        resources.add(resource);
    }

    public boolean hasResource(SchematicMaterialResource resource) {
        Iterator<SchematicMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            SchematicMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return true;
            }
        }
        return false;
    }

    public SchematicMaterialResource getResource(SchematicMaterialResource resource) {
        Iterator<SchematicMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            SchematicMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return mr;
            }
        }
        return null;
    }

}
