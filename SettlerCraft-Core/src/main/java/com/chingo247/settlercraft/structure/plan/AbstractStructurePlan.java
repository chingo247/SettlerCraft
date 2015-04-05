/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.plan;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.File;
import java.nio.charset.Charset;

/**
 *
 * @author Chingo
 */
public abstract class AbstractStructurePlan implements StructurePlan {

    private static final HashFunction hashFunction = Hashing.md5();

    private final String id;
    private String name, category, description;
    private double price;
    private final String hash;
    private final File file;
    
    protected AbstractStructurePlan(File planFile) {
        this(hashFunction.hashString(planFile.getAbsolutePath(), Charset.defaultCharset()).toString(), planFile);
    }

    protected AbstractStructurePlan(String id, File planFile) {
        this.file = planFile;
        this.hash = hashFunction.hashString(file.getAbsolutePath(), Charset.defaultCharset()).toString();
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
    
    /**
     * Returns the MD5 hash of this StructurePlan, which is based on the File Path of this StructurePlan
     * @return The MD5 hash which corresponds this StructurePlans file's path
     */
    protected String getMD5Hash() {
        return hash;
    }

    protected String hash(File f) {
        return hashFunction.hashString(f.getAbsolutePath()).toString();
    }

}
