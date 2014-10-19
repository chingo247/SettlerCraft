/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.townapi.entities.field;

import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.sk89q.worldedit.Vector;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author Chingo
 */
@Entity
public class TreeField implements Serializable {
    
    @Transient
    private static final int TREE_HEIGHT = 10;
    
    @Id
    @GeneratedValue
    private Long id;
    private TreeType type;
    private Dimension region;
    private String world;
    private UUID worldUUID;
    
    /**
     * JPA Constructor
     */
    protected TreeField() {
    }

    public TreeField(TreeType type, Field field) {
        this.type = type;
        this.region = new Dimension(new Vector(field.minX, field.height, field.minZ) , new Vector(field.maxX, field.height + TREE_HEIGHT, field.maxZ));
        this.world = field.world;
        this.worldUUID = field.worldUUID;
    }
    
    public Long getId() {
        return id;
    }

    public TreeType getType() {
        return type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }

    public String getWorldName() {
        return world;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }
    
    

  
    
}
