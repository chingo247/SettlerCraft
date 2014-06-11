package com.sc.entity.progress;

///*
// * Copyright (C) 2014 Chingo
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.sc.api.structure.entity.progress;
//
//import com.sc.api.structure.entity.schematic.SchematicMaterialResource;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import org.hibernate.annotations.Cascade;
//
///**
// *
// * @author Chingo
// */
//@Entity
//public class MaterialLayerProgress implements Serializable {
//
//    protected final int layer;
//
//    @OneToMany(fetch = FetchType.LAZY)
//    @Cascade(org.hibernate.annotations.CascadeType.ALL)
//    private List<MaterialResourceProgress> resources = new ArrayList<>();
//
//    @Id
//    @GeneratedValue
//    private Long id;
//
//    @ManyToOne
//    @Cascade(org.hibernate.annotations.CascadeType.ALL)
//    private MaterialProgress progress;
//
//    public MaterialLayerProgress() {
//        this.layer = -1;
//    }
//
//    public MaterialLayerProgress(MaterialProgress progress, int layer) {
//        this.progress = progress;
//        this.resources = new ArrayList<>();
//        this.layer = layer;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public MaterialProgress getProgress() {
//        return progress;
//    }
//
//    public List<MaterialResourceProgress> getResources() {
//        return new ArrayList<>(resources);
//    }
//
//    public int size() {
//        return resources.size();
//    }
//
//    public boolean isEmpty() {
//        return resources.isEmpty();
//    }
//
//    public void addResource(MaterialResourceProgress resource) {
//        Iterator<MaterialResourceProgress> it = resources.iterator();
//        while (it.hasNext()) {
//            MaterialResourceProgress mr = it.next();
//            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
//                mr.setAmount(mr.getAmount() + resource.getAmount());
//                return;
//            }
//        }
//        resources.add(resource);
//    }
//
//    public boolean hasResource(MaterialResourceProgress resource) {
//        Iterator<MaterialResourceProgress> it = resources.iterator();
//        while (it.hasNext()) {
//            MaterialResourceProgress mr = it.next();
//            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public MaterialResourceProgress getResource(MaterialResourceProgress resource) {
//        Iterator<MaterialResourceProgress> it = resources.iterator();
//        while (it.hasNext()) {
//            MaterialResourceProgress mr = it.next();
//            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
//                return mr;
//            }
//        }
//        return null;
//    }
//
//    public void removeResource(SchematicMaterialResource resource) {
//        Iterator<MaterialResourceProgress> it = resources.iterator();
//        while (it.hasNext()) {
//            MaterialResourceProgress mr = it.next();
//            if (mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
//                it.remove();
//                return;
//            }
//        }
//    }
//
//}
