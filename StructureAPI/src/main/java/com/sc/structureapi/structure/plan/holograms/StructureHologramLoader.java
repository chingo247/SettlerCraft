/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan.holograms;

import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.structure.plan.data.Elements;
import com.sc.structureapi.structure.plan.data.Loader;
import com.sc.structureapi.structure.plan.data.Nodes;
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

        System.out.println("Loading");

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
