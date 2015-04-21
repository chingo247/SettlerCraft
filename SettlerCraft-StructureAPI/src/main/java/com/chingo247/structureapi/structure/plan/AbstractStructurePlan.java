/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.structure.plan;

import com.chingo247.settlercraft.core.util.XXHasher;
import java.io.File;

/**
 *
 * @author Chingo
 */
public abstract class AbstractStructurePlan implements StructurePlan {

    private final String id;
    private String name, category, description;
    private double price;
    private final String hash;
    private final File file;
    
    protected AbstractStructurePlan(File planFile) {
        this(String.valueOf(new XXHasher().hash32String(planFile.getAbsolutePath())), planFile);
    }

    protected AbstractStructurePlan(String id, File planFile) {
        this.file = planFile;
        this.hash = String.valueOf(new XXHasher().hash32String(planFile.getAbsolutePath()));
        this.price = 0.0d;
        this.category = "Default";
        this.id = id;
    }
    
    @Override
    public final String getId() {
        return id;
    }
    
    @Override
    public String getDescription() {
        if(description == null) {
            return "None";
        }
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public double getPrice() {
        return price;
    }
    
    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public File getFile() {
        return file;
    }
    
   
    protected String getPathHash() {
        return hash;
    }

    protected String hash(File f) {
        return String.valueOf(new XXHasher().hash32String(f.getAbsolutePath()));
    }

}
