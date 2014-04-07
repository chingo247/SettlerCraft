/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.requirement.material;

import com.google.common.base.Preconditions;
import com.settlercraft.model.plan.schematic.SchematicBlockData;
import com.settlercraft.model.plan.schematic.SchematicObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;

/**
 *
 * @author Chingo
 */
@Embeddable
public class MaterialRequirement implements Serializable {
    
    
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "resource_req")
    private ArrayList<LayerRequirement> layers; 

    /**
     * JPA Constructor.
     */
    protected MaterialRequirement() {}
    
    private MaterialRequirement(ArrayList<LayerRequirement> layers) {
        this.layers = layers;
    }
    
    public MaterialRequirement(SchematicObject obj) {
        Preconditions.checkNotNull(obj);
        this.layers = new ArrayList<>();
        setLayers(obj);
    }

    private void setLayers(SchematicObject obj) {
        HashMap<Integer, TreeSet<SchematicBlockData>> m = obj.getBlocksLayered();
        for(int i : m.keySet()) {
            layers.add(new LayerRequirement(i, m.get(i)));
        }
    }
    
    public LayerRequirement getLayerRequirement(int layer) {
        for(LayerRequirement l : layers) {
            if(l.getLayer() == layer) return l;
        }
        return null;
    }
    
    public boolean removeLayerRequirement(int layer) {
        for(int i = 0; i < layers.size(); i++) {
            if(layers.get(i).getLayer() == layer) return layers.remove(i) != null;
        }
        return false;
    }



    public MaterialRequirement copy() {
        return new MaterialRequirement(new ArrayList<>(layers));
    }
}
