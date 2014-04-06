/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.settlercraft.util.material.MaterialUtil;
import com.settlercraft.util.schematic.BlockData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.Basic;
import org.bukkit.Material;

/**
 * All requirements for this structure will be initialized once for each building at server start
 * @author Chingo
 */
public final class StructureRequirement {

    
    private final Set<StructureResource> resources;

    StructureRequirement(StructurePlan plan) {
        this.resources = new HashSet<>();
        setRequirements(plan);
//        System.out.println(resources.size());
    }

    /**
     * All materials will checked if they could get any simpler. e.g. When a building needs a wooden
     * stairs it will be more likely pay the wood value of the stairs (see Woods.class)
     *
     * @param plan The StructurePlan
     */
    private void setRequirements(StructurePlan plan) {
        HashMap<BlockData, Integer> m = plan.getSchematic().getBlockData();
        System.out.println(m.size());
        HashMap<BlockData, Float> approxReq = new HashMap<>();

        for (Map.Entry<BlockData, Integer> e : m.entrySet()) {
            BlockData b = e.getKey();
            Material supported = MaterialUtil.getSupported(b);
            
            if(supported != null) {
                setRequirement(supported, b.getData(), e.getValue() * MaterialUtil.getValue(b), approxReq);
            } else {
                setRequirement(b.getMaterial(), b.getData(), e.getValue() * 1.0f, approxReq); // if not supported default value = 1.0
            }
        }
        setResources(approxReq);
    }
    
    private void setRequirement(Material material, byte data, float value, HashMap<BlockData, Float> approxReq) {
        BlockData block = new BlockData(material.getId(), data);
        
        if(approxReq.get(block) == null) {
            approxReq.put(block, value);
        } else {
            approxReq.put(block, approxReq.get(block) + value);
        }
    }

    private void setResources(HashMap<BlockData, Float> approxReq) {
        for (Entry<BlockData, Float> e : approxReq.entrySet()) {
            BlockData b = e.getKey();
            if(MaterialUtil.isSupported(b)) {
            resources.add(new StructureResource(b.getData(), b.getMaterial(),Math.round(e.getValue()), false));
            } else {
            resources.add(new StructureResource(b.getData(), b.getMaterial(),Math.round(e.getValue()), true));    
            }
        }
    }

    public final Set<StructureResource> getResources() {
        return new HashSet<>(resources);
    }

}
