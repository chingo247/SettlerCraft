/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement.options;

/**
 *
 * @author Chingo
 */
public abstract class PlacementOptions {
    
    private int cubeX;
    private int cubeY;
    private int cubeZ;

    public PlacementOptions() {
        this.cubeX = 16;
        this.cubeY = 16;
        this.cubeZ = 16;
    }

    public int getCubeX() {
        return cubeX;
    }

    public void setCubeX(int cubeX) {
        this.cubeX = cubeX;
    }

    public int getCubeY() {
        return cubeY;
    }

    public void setCubeY(int cubeY) {
        this.cubeY = cubeY;
    }

    public int getCubeZ() {
        return cubeZ;
    }

    public void setCubeZ(int cubeZ) {
        this.cubeZ = cubeZ;
    }
    
    
    
   
    
}
