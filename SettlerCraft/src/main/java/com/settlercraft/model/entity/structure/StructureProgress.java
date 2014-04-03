/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.entity.structure;

import com.settlercraft.util.schematic.model.BlockData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureProgress implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;

    @Basic
    private HashMap<Material, Integer> requirements;

    public StructureProgress(List<BlockData> data) {
        for(BlockData d : data) {
            
        }
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    

    
}
