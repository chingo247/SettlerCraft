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
import java.util.List;
import java.util.Map.Entry;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureLayer implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private int layer;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MaterialResource> resources;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SpecialResource> specialResources;

    /**
     * JPA Constructor.
     */
    protected StructureLayer() {
    }

    StructureLayer(int layer, Collection<SchematicBlockData> blocks) {
        this.layer = layer;
        this.resources = new ArrayList<>();
        this.specialResources = new ArrayList<>();
        setRequirements(blocks);
    }

    private void setRequirements(Collection<SchematicBlockData> blocks) {
        HashMap<Material, Float> mts = Maps.newHashMap();
        for (SchematicBlockData sbd : blocks) {
            Material m;
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
            resources.add(new MaterialResource(this, e.getKey(), Math.round(e.getValue())));
        }
    }
    
    
    
    public int getNeed(Material material, Byte data) {
        if(!contains(material, data)) {
            return 0; // NO NEED
        } else if(getSpecialResource(material, data) != null) {
            return getSpecialResource(material, data).getValue();
        } else {
            return getResource(material).getValue();
        }
    }

    public int getLayer() {
        return layer;
    }

    public List<MaterialResource> getResources() {
        return resources;
    }

    public List<SpecialResource> getSpecialResources() {
        return specialResources;
    }

    public boolean contains(Material material, Byte data) {
        if (specialResources.contains(new SpecialResource(this, material, data, 0))) {
            return true;
        } else {
            return resources.contains(new MaterialResource(this, material, 0));
        }
    }

    public MaterialResource getResource(Material material) {
        for (MaterialResource r : resources) {
            if (r.getMaterial() == material) {
                return r;
            }
        }
        return null;
    }

    public SpecialResource getSpecialResource(Material material, Byte data) {
        for (SpecialResource s : specialResources) {
            if (s.getData().equals(data) && s.getMaterial() == material) {
                return s;
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
        sb.append("Special Resources:\n");
        for (SpecialResource r : specialResources) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

}
