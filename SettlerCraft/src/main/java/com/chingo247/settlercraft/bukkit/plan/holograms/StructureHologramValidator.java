/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.settlercraft.bukkit.plan.holograms;

import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
import com.chingo247.settlercraft.structureapi.structure.plan.document.Validator;
import com.chingo247.settlercraft.util.document.Elements;
import com.chingo247.settlercraft.util.document.Nodes;
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
