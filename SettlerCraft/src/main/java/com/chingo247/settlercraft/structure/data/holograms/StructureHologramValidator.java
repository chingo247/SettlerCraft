/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.structure.data.holograms;

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
public class StructureHologramValidator extends Validator {

    public StructureHologramValidator() {
        super(Nodes.HOLOGRAMS_NODE);
    }

    @Override
    public void validate(Element e) throws StructureDataException {
         List<Node> nodes = e.selectNodes(Nodes.HOLOGRAM_NODE);

        if (nodes != null && !nodes.isEmpty()) {
            int count = 0;
            for (Node n : nodes) {
                Node xNode = n.selectSingleNode(Elements.X);
                Node yNode = n.selectSingleNode(Elements.Y);
                Node zNode = n.selectSingleNode(Elements.Z);

                if (xNode == null) {
                    throw new StructureDataException("Missing 'X' node for 'Hologram#" + count + "'");
                }
                
                if (yNode == null) {
                    throw new StructureDataException("Missing 'Y' node for 'Hologram#" + count + "'");
                }
                
                if (zNode == null) {
                    throw new StructureDataException("Missing 'Z' node for 'Hologram#" + count + "'");
                }
                try {
                    Integer.parseInt(xNode.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureDataException("Invalid X value should 'Hologram#" + count + "'");
                }

                try {
                    Integer.parseInt(yNode.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureDataException("Invalid Y value for 'Hologram#" + count + "'");
                }

                try {
                    Integer.parseInt(zNode.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureDataException("Invalid Z value for 'Hologram#" + count + "'");
                }

                Node linesNode = n.selectSingleNode("Lines");
                if (linesNode == null) {
                    throw new StructureDataException("Missing 'Lines' node for 'Hologram#" + count + "'");
                }

                List<Node> lineNodes = n.selectNodes("Lines/Line");
                if (lineNodes == null || lineNodes.isEmpty()) {
                    throw new StructureDataException("Missing 'Line' nodes for 'Hologram#" + count + "'");
                }

                count++;
            }
        }
    }
    
}
