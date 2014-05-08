/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.model.structure.progress;

import com.sc.api.structure.model.structure.schematic.SchematicMaterialResource;
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
        while(it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if(mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                mr.setAmount(mr.getAmount() + resource.getAmount());
                return;
            }
        }
        resources.add(resource);
    }
    
    public boolean hasResource(StructureProgressMaterialResource resource) {
       Iterator<StructureProgressMaterialResource> it = resources.iterator();
        while(it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if(mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return true;
            }
        } 
        return false;
    }
    
    public StructureProgressMaterialResource getResource(StructureProgressMaterialResource resource) {
       Iterator<StructureProgressMaterialResource> it = resources.iterator();
        while(it.hasNext()) {
            StructureProgressMaterialResource mr = it.next();
            if(mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
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
