/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.requirement.material;

import com.google.common.collect.Maps;
import com.settlercraft.model.plan.schematic.ResourceMaterial;
import com.settlercraft.model.plan.schematic.SchematicBlockData;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.persistence.Basic;
import javax.persistence.Embeddable;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Embeddable
public class LayerRequirement implements Serializable {

    private int layer;
    
    @Basic
    private HashMap<Material,Integer> basicResources;
    
    
    /**
     * All Resources where the byte value is important
     */
    @Basic
    private HashMap<ResourceMaterial, Integer> specialResources; 

    /**
     * JPA Constructor.
     */
    protected LayerRequirement() {}
    
    
    
    LayerRequirement(int layer, Collection<SchematicBlockData> blocks) {
        this.layer = layer;
        this.basicResources = Maps.newHashMap();
        this.specialResources = Maps.newHashMap();
        setRequirements(blocks);
    }

    public HashMap<Material, Integer> getResources() {
        return basicResources;
    }

    public HashMap<ResourceMaterial, Integer> getSpecialResources() {
        return specialResources;
    }
    
    private void setRequirements(Collection<SchematicBlockData> blocks) {
        HashMap<Material, Float> mts = Maps.newHashMap();
        for(SchematicBlockData sbd : blocks) {
            Material m = SettlerCraftMaterials.getSimplifiedMaterial(sbd);
            
            // Check canSimplify?
            if(m == null) { // This material
                m = sbd.getMaterial();
            } 
            // exists?
            if(mts.get(m) == null) {
                mts.put(m, SettlerCraftMaterials.getValue(new ResourceMaterial(m, sbd.getData())));
            } else {
                mts.put(m, mts.get(m) + SettlerCraftMaterials.getValue(new ResourceMaterial(m, sbd.getData())));
            }
        }
        setResources(mts);
    }

    public int getLayer() {
        return layer;
    }

    private void setResources(HashMap<Material, Float> mts) {
        for(Entry<Material, Float> e : mts.entrySet()) {
            basicResources.put(e.getKey(), Math.round(e.getValue()));     
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic Resources:\n");
        for(Entry<Material,Integer> e : basicResources.entrySet()) {
            sb.append(e.getKey()).append(" : ").append(e.getValue()).append("\n");
        }
        
        sb.append("Special Resources:\n");
        for(Entry<ResourceMaterial,Integer> e : specialResources.entrySet()) {
            sb.append(e.getKey().getMaterial()).append(" : ").append(e.getValue()).append("\n");
        }
        return sb.toString(); 
    }
    
    
    
}
