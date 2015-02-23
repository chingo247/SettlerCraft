/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.structure.placement.Placement;
import java.io.File;
import java.util.List;

/**
 *
 * @author Chingo
 */
public interface StructurePlan {
    
    public String getName();
    public File getFile();
    public String getRelativePath();
    public String getCategory();
    public String getDescription();
    public double getPrice();
    public Placement getPlacement();
    public List<Placement> getSubPlacements();
    public List<StructurePlan> getSubStructurePlans();
    
}
