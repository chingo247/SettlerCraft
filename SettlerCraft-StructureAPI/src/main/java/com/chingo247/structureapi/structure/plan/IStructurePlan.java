/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.structureapi.structure.plan;

import com.chingo247.structureapi.structure.plan.placement.Placement;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Chingo
 */
public interface IStructurePlan {
    
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
    
    public void save() throws IOException;
    
}
