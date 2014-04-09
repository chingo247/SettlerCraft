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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Embeddable
public class LayerRequirement implements Serializable {

    private int layer;

    @OneToMany(cascade = CascadeType.ALL)
    private ArrayList<ResourceRequirement> basicResources;

    @OneToMany(cascade = CascadeType.ALL)
    private ArrayList<SpecialResourceRequirement> specialResources;

    /**
     * JPA Constructor.
     */
    protected LayerRequirement() {
    }

    LayerRequirement(int layer, Collection<SchematicBlockData> blocks) {
        this.layer = layer;
        this.basicResources = new ArrayList<>();
        this.specialResources = new ArrayList<>();
        setRequirements(blocks);
    }

    private void setRequirements(Collection<SchematicBlockData> blocks) {
        HashMap<Material, Float> mts = Maps.newHashMap();
        for (SchematicBlockData sbd : blocks) {
            Material m = SettlerCraftMaterials.getSimplifiedMaterial(sbd);

            // Check canSimplify?
            if (m == null) { // This material
                m = sbd.getMaterial();
            }
            // exists?
            if (mts.get(m) == null) {
                mts.put(m, SettlerCraftMaterials.getValue(new ResourceMaterial(m, sbd.getData())));
            } else {
                mts.put(m, mts.get(m) + SettlerCraftMaterials.getValue(new ResourceMaterial(m, sbd.getData())));
            }
        }
        // Set Special Resources here?

        // Set Basice Resources
        setResources(mts);
    }

    private void setResources(HashMap<Material, Float> mts) {
        for (Entry<Material, Float> e : mts.entrySet()) {
            basicResources.add(new ResourceRequirement(this, e.getKey(), Math.round(e.getValue())));
        }
    }

    public int getLayer() {
        return layer;
    }

    public ArrayList<ResourceRequirement> getBasicResources() {
        return basicResources;
    }

    public ArrayList<SpecialResourceRequirement> getSpecialResources() {
        return specialResources;
    }
    
    public boolean contains(Material material, Byte data) {
        if(specialResources.contains(new SpecialResourceRequirement(this, material, data, 0))) {
            return true;
        } else {
            return basicResources.contains(new ResourceRequirement(this, material, 0));
        }
    }
    
 
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic Resources:\n");
        for (ResourceRequirement r : basicResources) {
            sb.append(r).append("\n");
        }
        sb.append("Special Resources:\n");
        for (SpecialResourceRequirement r : specialResources) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }

}
