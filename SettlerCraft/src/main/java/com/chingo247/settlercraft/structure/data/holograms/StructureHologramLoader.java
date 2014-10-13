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
package com.chingo247.settlercraft.structure.data.holograms;

import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.structure.data.Elements;
import com.chingo247.settlercraft.structure.data.Loader;
import com.chingo247.settlercraft.structure.data.Nodes;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class StructureHologramLoader extends Loader<StructureHologram> {

    public StructureHologramLoader() {
        super(Nodes.HOLOGRAMS_NODE);
    }
    
    @Override
    public List<StructureHologram> load(Element overviewsElement) throws StructureDataException {
        if (overviewsElement == null) {
            throw new AssertionError("Overviews element was null");
        }

        if (!overviewsElement.getName().equals(Elements.STRUCTURE_HOLOGRAMS)) {
            throw new AssertionError("Expected '" + Elements.STRUCTURE_HOLOGRAMS + "' element, but got '" + overviewsElement.getName() + "'");
        }

//        System.out.println("Loading");

        List<StructureHologram> holograms = new ArrayList<>();
        List<Node> hologramNodes = overviewsElement.selectNodes(Elements.STRUCTURE_OVERVIEW);
        int count = 0;

        for (Node hologramNode : hologramNodes) {
            
            Node xNode = hologramNode.selectSingleNode("x");
            Node yNode = hologramNode.selectSingleNode("y");
            Node zNode = hologramNode.selectSingleNode("z");
            if (xNode == null) {
                throw new StructureDataException("Missing 'x' node for 'StructureOverview#" + count + "'");
            }
            if (yNode == null) {
                throw new StructureDataException("Missing 'y' node for 'StructureOverview#" + count + "'");
            }
            if (zNode == null) {
                throw new StructureDataException("Missing 'z' node for 'StructureOverview#" + count + "'");
            }

            try {
                int x = Integer.parseInt(xNode.getText());
                int y = Integer.parseInt(yNode.getText());
                int z = Integer.parseInt(zNode.getText());
                
                List<Node> lines = hologramNode.selectNodes("Lines/Line");
                String[] tArray = new String[lines.size()];
                for(int i = 0; i <  tArray.length; i++) {
                    tArray[i] = (lines.get(i)).getText();
                }
                holograms.add(new StructureHologram(x, y, z, tArray));
                
            } catch (NumberFormatException nfe) {
                throw new StructureDataException("Values for (x,y,z) are not of type number in 'StructureOverview#" + count + "'");
            }
            count++;

        }

        return holograms;
    }





}
