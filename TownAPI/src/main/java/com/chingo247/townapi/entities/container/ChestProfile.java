/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.townapi.entities.container;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class ChestProfile implements Serializable {
    
    @Id
    private String id;
    
    private Set<Material> materials;

    /**
     * JPA Constructor
     */
    protected ChestProfile() {
    }

    public ChestProfile(String id, Set<Material> accepts) {
        this.id = id;
        this.materials = accepts;
    }

    public String getId() {
        return id;
    }

    public Set<Material> getMaterials() {
        return materials;
    }
    
    
    
}
