/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.structure.plan.overview;

import com.chingo247.settlercraft.structure.plan.data.Elements;
import com.chingo247.settlercraft.structure.plan.data.SettlerCraftElement;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class StructureOverview implements SettlerCraftElement{
    
    private int x;
    private int y;
    private int z;

    public StructureOverview(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public Element asElement() {
        Element root = new BaseElement(Elements.STRUCTURE_OVERVIEW);
        // Set X
        Element xElement = new BaseElement(Elements.X);
        xElement.setText(String.valueOf(x));
        // Set Y
        Element yElement = new BaseElement(Elements.Y);
        yElement.setText(String.valueOf(y));
        // Set Z
        Element zElement = new BaseElement(Elements.Z);
        zElement.setText(String.valueOf(z));
        // Add nodes
        root.add(xElement);
        root.add(yElement);
        root.add(zElement);
        return root;
    }

 
    
    
}
