/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.google.common.collect.Maps;
import com.settlercraft.util.material.MaterialUtil;
import com.settlercraft.util.schematic.StructureBlock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Material;

/**
 * All requirements for this structure will be initialized once for each building at server start
 * @author Chingo
 */
public final class StructureRequirement {

    
    private final HashMap<Integer,Set<StructureResource>> resources = Maps.newHashMap();

    StructureRequirement(StructurePlan plan) {
        setRequirements(plan);
    }

    /**
     * All materials will checked if they could get any simpler. e.g. When a building needs a wooden
     * stairs it will be more likely pay the wood value of the stairs (see Woods.class)
     *
     * @param plan The StructurePlan
     */
    private void setRequirements(StructurePlan plan) {
        for(int layer = 0; layer < plan.getSchematic().height; layer++) {
        resources.put(layer, new HashSet<StructureResource>());
        HashMap<StructureBlock, Integer> m = plan.getSchematic().getStructureBlocks(layer);
        HashMap<StructureBlock, Float> approxReq = new HashMap<>();

        for (Map.Entry<StructureBlock, Integer> e : m.entrySet()) {
            StructureBlock b = e.getKey();
            Material supported = MaterialUtil.getSupported(b);
            
            if(supported != null) {
                setRequirement(supported, b.getData(), e.getValue() * MaterialUtil.getValue(b), approxReq);
            } else {
                setRequirement(b.getMaterial(), b.getData(), e.getValue() * 1.0f, approxReq); // if not supported default value = 1.0
            }
        }

        setResources(approxReq,layer);
        }
    }
    
    private void setRequirement(Material material, byte data, float value, HashMap<StructureBlock, Float> approxReq) {
        StructureBlock block = new StructureBlock(material.getId(), data);
        if(approxReq.get(block) == null) {
            approxReq.put(block, value);
        } else {
            approxReq.put(block, approxReq.get(block) + value);
        }
    }

    private void setResources(HashMap<StructureBlock, Float> approxReq, int layer) {
        for (Entry<StructureBlock, Float> e : approxReq.entrySet()) {
            StructureBlock b = e.getKey();
            StructureResource res = new StructureResource(b.getData(), b.getMaterial(), Math.round(e.getValue()));  
    
            if(resources.get(layer).contains(res)) {
                Iterator<StructureResource> it = resources.get(layer).iterator();
                
                while(it.hasNext()) {
                    StructureResource i = it.next();
                    if(i.equals(res)) {
                        int amount = i.getAmount();
                        res.setAmount(res.getAmount() + amount);
                        resources.get(layer).remove(res);
                        resources.get(layer).add(res);
                        break;
                    }
                }
                
            } else if (!resources.get(layer).contains(res)) {
                resources.get(layer).add(res);
            } else {
               
            }
           
        }
        
    }

    public final HashMap<Integer,Set<StructureResource>> getResources() {
        return new HashMap<>(resources);
    }



}
