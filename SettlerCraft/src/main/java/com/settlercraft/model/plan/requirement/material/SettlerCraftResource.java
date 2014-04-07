/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.requirement.material;

import com.settlercraft.model.plan.schematic.BlockMaterial;
import java.util.Objects;
import org.bukkit.Material;

/**
 * Stores material and data but uses an alternative equals()
 * @author Chingo
 */
class SettlerCraftResource extends BlockMaterial {

    public SettlerCraftResource(Material material, Byte data) {
        super(material, data);
    }

    @Override
    public boolean equals(Object obj) {
          if(! (obj instanceof SettlerCraftResource)) {
            return false;
        }
        SettlerCraftResource res = (SettlerCraftResource) obj;
        if(SettlerCraftMaterials.isSpecial(this) && SettlerCraftMaterials.isSpecial(res)) {
            return this.data.equals(res.data) && this.material == res.material;
        } else {
            return this.material == res.material;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.material);
        if(SettlerCraftMaterials.isSpecial(this)) {
        hash = 41 * hash + Objects.hashCode(this.data);
        }
        return hash;
    }
    
    
    
}
