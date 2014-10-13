/*
 * Copyright (C) 2014 Chingo
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

package com.chingo247.settlercraft.structure.data.overview;

import com.chingo247.settlercraft.structure.data.Elements;
import com.chingo247.settlercraft.structure.data.SettlerCraftElement;
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
