/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan.requirement.material;

import com.google.common.collect.Maps;
import com.settlercraft.core.model.plan.schematic.ResourceMaterial;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.util.SettlerCraftMaterials;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class StructureLayerRequirement implements Serializable {

    private Long id;

    private ArrayList<MaterialResource> resources;

    private int layer;
    /**
     * JPA Constructor.
     */
    protected StructureLayerRequirement() {
    }

    StructureLayerRequirement(int layer, Collection<SchematicBlockData> blocks) {
        this.layer = layer;
        this.resources = new ArrayList<>();
        setRequirements(blocks);
    }

    private void setRequirements(Collection<SchematicBlockData> blocks) {
        HashMap<Material, Float> mts = Maps.newHashMap();
        for (SchematicBlockData sbd : blocks) {
            Material m;
            if(SettlerCraftMaterials.isUncraftable(sbd.getMaterial())) {
                continue;
            }
            
            if (SettlerCraftMaterials.canSimplify(sbd)) { 
                m = SettlerCraftMaterials.getSimplifiedMaterial(sbd);
            } else {
                 m = sbd.getMaterial();
            }
            
            if (mts.get(m) == null) {
                mts.put(m, SettlerCraftMaterials.getValue(new ResourceMaterial(m, sbd.getData())));
            } else {
                mts.put(m, mts.get(m) + SettlerCraftMaterials.getValue(new ResourceMaterial(m, sbd.getData())));
            }
        }
        setResources(mts);
    }

    private void setResources(HashMap<Material, Float> mts) {
        for (Entry<Material, Float> e : mts.entrySet()) {
            resources.add(new MaterialResource(e.getKey(), Math.round(e.getValue())));
        }
    }
    
    public int getLayer() {
        return layer;
    }

    public List<MaterialResource> getResources() {
        return resources;
    }
    
    public boolean removeResource(MaterialResource resource) {
        Iterator<MaterialResource> it = resources.iterator();
        while (it.hasNext()) {
            if(it.next().material.equals(resource.material)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public MaterialResource getResource(Material material) {
        for (MaterialResource r : resources) {
            if (r.getMaterial() == material) {
                return r;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic Resources:\n");
        for (MaterialResource r : resources) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    
    

}
