/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.entities.field;

import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.sk89q.worldedit.Vector;
import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author Chingo
 */
public class CropField {
    
    @Transient
    private static final int CROP_HEIGHT = 10;
    
    @Id
    @GeneratedValue
    private Long id;
    private CropType type;
    private Dimension region;
    private String world;
    private UUID worldUUID;
    
    /**
     * JPA Constructor
     */
    protected CropField() {
    }

    public CropField(CropType type, Field field) {
        this.type = type;
        this.region = new Dimension(new Vector(field.minX, field.height, field.minZ) , new Vector(field.maxX, field.height + CROP_HEIGHT, field.maxZ));
        this.world = field.world;
        this.worldUUID = field.worldUUID;
    }
    
    public Long getId() {
        return id;
    }

    public CropType getType() {
        return type;
    }

    public void setType(CropType type) {
        this.type = type;
    }

    
}
