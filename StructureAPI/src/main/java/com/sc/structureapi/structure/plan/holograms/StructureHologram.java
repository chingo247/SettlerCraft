/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan.holograms;

/**
 *
 * @author Chingo
 */
public class StructureHologram {

    int x;
    int y;
    int z;
    String[] text;

    StructureHologram(int x, int y, int z, String[] lines) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.text = lines;
    }

    

}