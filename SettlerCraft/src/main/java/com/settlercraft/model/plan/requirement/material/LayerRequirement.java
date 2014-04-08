/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.requirement.material;

import com.google.common.collect.Maps;
import com.settlercraft.model.plan.schematic.BlockMaterial;
import com.settlercraft.model.plan.schematic.SchematicBlockData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import org.bukkit.Material;
import org.hibernate.annotations.CollectionOfElements;

/**
 *
 * @author Chingo
 */
@Embeddable
public class LayerRequirement implements Serializable {

    private int layer;
    
    @CollectionOfElements(fetch = FetchType.EAGER)
    private Set<StructureResource> resources;

    /**
     * JPA Constructor.
     */
    protected LayerRequirement() {}
    
    
    
    LayerRequirement(int layer, Collection<SchematicBlockData> blocks) {
        this.layer = layer;
        this.resources = new HashSet<>();
        setRequirements(blocks);
    }
    
    private void setRequirements(Collection<SchematicBlockData> blocks) {
        HashMap<SettlerCraftResource, Float> mts = Maps.newHashMap();
        for(SchematicBlockData sbd : blocks) {
            Material m = SettlerCraftMaterials.getSimplifiedMaterial(sbd);
            SettlerCraftResource bm;
            // Check canSimplify?
            if(m != null) { // This material
                bm = new SettlerCraftResource(m,sbd.data);
            } else {
                bm = new SettlerCraftResource(sbd.material, sbd.data);
            }
            // exists?
            if(mts.get(bm) == null) {
                mts.put(bm, SettlerCraftMaterials.getValue(new BlockMaterial(bm.material, bm.data)));
            } else {
                mts.put(bm, mts.get(bm) + SettlerCraftMaterials.getValue(new BlockMaterial(bm.material, bm.data)));
            }
        }
        setResources(mts);
    }

    public int getLayer() {
        return layer;
    }

    private void setResources(HashMap<SettlerCraftResource, Float> mts) {
        for(Entry<SettlerCraftResource, Float> e : mts.entrySet()) {
            if(SettlerCraftMaterials.isSpecial(e.getKey())) {
            resources.add(new StructureResource(e.getKey().material, Math.round(e.getValue()), e.getKey().data));     
            } else {
            resources.add(new StructureResource(e.getKey().material, Math.round(e.getValue())));
            }
        }
    }

    
    

    
    
    
    
}
