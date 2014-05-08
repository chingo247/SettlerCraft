/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.model.structure.schematic;

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
        while(it.hasNext()) {
            SchematicMaterialResource mr = it.next();
            if(mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                mr.setAmount(mr.getAmount() + resource.getAmount());
                return;
            }
        }
        resources.add(resource);
    }
    
    public boolean hasResource(SchematicMaterialResource resource) {
       Iterator<SchematicMaterialResource> it = resources.iterator();
        while(it.hasNext()) {
            SchematicMaterialResource mr = it.next();
            if(mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return true;
            }
        } 
        return false;
    }
    
    public SchematicMaterialResource getResource(SchematicMaterialResource resource) {
       Iterator<SchematicMaterialResource> it = resources.iterator();
        while(it.hasNext()) {
            SchematicMaterialResource mr = it.next();
            if(mr.getMaterial() == resource.getMaterial() && mr.getData() == resource.getData()) {
                return mr;
            }
        } 
        return null;
    }
    
}
