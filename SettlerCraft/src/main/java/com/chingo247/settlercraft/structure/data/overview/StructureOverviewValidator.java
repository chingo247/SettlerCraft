/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.structure.data.overview;

import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.structure.data.Elements;
import com.chingo247.settlercraft.structure.data.Nodes;
import com.chingo247.settlercraft.structure.data.Validator;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class StructureOverviewValidator extends Validator {

    public StructureOverviewValidator() {
        super(Nodes.STRUCTURE_OVERVIEWS_NODE);
    }
    
    @Override
    public void validate(Element e) throws StructureDataException {
        if(!e.getName().equals(Elements.STRUCTURE_OVERVIEWS)) {
            throw new AssertionError("Expected '" + Elements.STRUCTURE_OVERVIEWS +"' but got '"+e.getName()+"'");
        }
        
        
        List<Node> nodes = e.selectNodes(Elements.STRUCTURE_OVERVIEW);
        if (nodes != null && !nodes.isEmpty()) {
            int count = 0;
            for (Node n : nodes) {
                Node xNode = n.selectSingleNode(Elements.X);
                Node yNode = n.selectSingleNode(Elements.Y);
                Node zNode = n.selectSingleNode(Elements.Z);

                if (xNode == null) {
                    throw new StructureDataException("Missing 'X' node for '"+Elements.STRUCTURE_OVERVIEW+"#" + count);
                }
                
                if (yNode == null) {
                    throw new StructureDataException("Missing 'Y' node for '"+Elements.STRUCTURE_OVERVIEW+"#" + count);
                }
                
                if (zNode == null) {
                    throw new StructureDataException("Missing 'Z' node for '"+Elements.STRUCTURE_OVERVIEW+"#" + count);
                }

                try {
                    Integer.parseInt(xNode.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureDataException("Invalid X value for '"+Elements.STRUCTURE_OVERVIEW+"#" + count);
                }

                try {
                    Integer.parseInt(yNode.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureDataException("Invalid Y value for '"+Elements.STRUCTURE_OVERVIEW+"#" + count);
                }

                try {
                    Integer.parseInt(zNode.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureDataException("Invalid Z value for '"+Elements.STRUCTURE_OVERVIEW+"#" + count);
                }
                count++;
            }
        }
    }

}
