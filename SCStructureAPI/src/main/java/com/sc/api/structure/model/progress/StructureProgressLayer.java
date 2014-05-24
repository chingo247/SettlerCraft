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
package com.sc.api.structure.model.progress;

import com.sc.api.structure.model.schematic.SchematicMaterialResource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureProgressLayer implements Serializable {

    protected final int layer;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StructureProgressMaterialResource> resources = new ArrayList<>();

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private StructureProgress progress;

    public StructureProgressLayer() {
        this.layer = -1;
    }

    public StructureProgressLayer(StructureProgress progress, int layer) {
        this.progress = progress;
        this.resources = new ArrayList<>();
        this.layer = layer;
    }

    public Long getId() {
        return id;
    }

    public StructureProgress getProgress() {
        return progress;
    }

    public List<StructureProgressMaterialResource> getResources() {
        return new ArrayList<>(resources);
    }

    public int size() {
        return resources.size();
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    public void addResource(StructureProgressMaterialResource resource) {
        Iterator<StructureProgressMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                mr.setAmount(mr.getAmount() + resource.getAmount());
                return;
            }
        }
        resources.add(resource);
    }

    public boolean hasResource(StructureProgressMaterialResource resource) {
        Iterator<StructureProgressMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return true;
            }
        }
        return false;
    }

    public StructureProgressMaterialResource getResource(StructureProgressMaterialResource resource) {
        Iterator<StructureProgressMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return mr;
            }
        }
        return null;
    }

    public void removeResource(SchematicMaterialResource resource) {
        Iterator<StructureProgressMaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                it.remove();
                return;
            }
        }
    }

}
