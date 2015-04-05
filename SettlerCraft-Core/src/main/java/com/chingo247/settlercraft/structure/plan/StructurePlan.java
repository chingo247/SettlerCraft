/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.structure.placement.Placement;
import java.io.File;

/**
 *
 * @author Chingo
 */
public interface StructurePlan {
    
    /**
     * Gets the id of this StructurePlan. Note that the id of the StructurePlan is based on the relative path of the file which
     * represents this StructurePlan and might differ at the next server startup
     * @return The id of this StructurePlan
     */
    public String getId();
    /**
     * Gets the description
     * @return The description of this StructurePlan
     */
    public String getDescription();
    
    /**
     * Sets the description of this StructurePlan
     * @param description The description to set
     */
    public void setDescription(String description);
    
    /**
     * Sets the name of this StructurePlan
     * @param name The StructurePlan
     */
    public void setName(String name);
    
    /**
     * Gets the name of the StructurePlan
     * @return The name of the StructurePlan
     */
    public String getName();
    
    /**
     * Gets the Category
     * @return The category
     */
    public String getCategory();
    
    /**
     * Set the category of this StructurePlan
     * @param category The category to set
     */
    public void setCategory(String category);
    
    /**
     * Gets the price of the StructurePlan
     * @return The price of this StructurePlan
     */
    public double getPrice();
    
    /**
     * Sets the price of the StructurePlan
     * @param price The price of the StructurePlan
     */
    public void setPrice(double price);
    
    /**
     * Gets the placement
     * @return The placement of this StructurePlan
     */
    public Placement getPlacement();
    
    
    /**
     * Gets the File that represents this StructurePlan or null if file doesn't exist...
     * @return The file
     */
    public File getFile();
    
    
}
