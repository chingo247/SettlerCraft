
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.exception.PlanException;
import static com.chingo247.settlercraft.structureapi.structure.plan.XMLStructurePlan.*;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class XMLStructurePlanReader extends AbstractXMLReader {

    public StructurePlan read(File structurePlan) throws DocumentException {
        return read(structurePlan, null, Direction.EAST, Vector.ZERO, true);
    }
    
    private StructurePlan read(File structurePlan, StructurePlan parent, Vector position, boolean recursive) throws DocumentException {
        return read(structurePlan, parent, Direction.EAST, position, recursive);
    }

    private StructurePlan read(File structurePlan, StructurePlan parent, Direction direction, Vector position,  boolean recursive) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.setValidation(true);
        Document d = reader.read(structurePlan);

        checkIsStructurePlan(d);

        StructurePlan plan = new StructurePlan(parent, structurePlan, getPlaceable(d.getRootElement(), structurePlan, direction, position));
        if (recursive) {
            if (hasSubstructures(d)) {
                List<Node> substructures = d.selectNodes(ROOT_ELEMENT + "/" + SUBSTRUCTURES + "/" + SUBSTRUCTURE);

                for (Node n : substructures) {
                    if (n.selectSingleNode(ROOT_ELEMENT) != null) {
                        plan.addStructurePlan(handleSubStructurePlan((Element) n, plan));
                    } else {
                        plan.addPlaceable(handleSubPlaceable((Element) n, plan.getFile()));
                    }
                }

            }
        }
        return plan;
    }

    private StructurePlan handleSubStructurePlan(Element substructureElement, StructurePlan parent) throws DocumentException {
        Node planNode = substructureElement.selectSingleNode(ROOT_ELEMENT);
        String plan = planNode.getStringValue().trim();
        File planFile = new File(parent.getFile().getParent(), plan);
        if(!planFile.exists()) throw new PlanException("StructurePlan referenced in '" + parent.getFile().getAbsolutePath() + "' doesn't exist");
        
        // Soft Read to get the placeable type
        StructurePlan preReadedPlan = read(planFile, parent, Direction.EAST, Vector.ZERO, false);
        Placeable p = preReadedPlan.getPlaceable();
        
        XMLStructurePlanReader reader = new XMLStructurePlanReader();
        
        Direction direction;
        Vector position;
        if(p instanceof PlaceableDirectional) {
            checkHasNode(substructureElement, DIRECTION);
            checkHasNode(substructureElement, POSITION);

            direction = matchDirection(substructureElement.selectSingleNode(DIRECTION).getStringValue());
            position = readVectorValues((Element)substructureElement.selectSingleNode(POSITION));
            
            return reader.read(planFile, parent, direction, position, true);
            
        } else /*if (p instanceof PlaceableGenerated || p instanceof PlaceableStructureLot) */{
            checkHasNode(substructureElement, POSITION);
            
            position = readVectorValues(substructureElement);
            
            return reader.read(planFile,parent, position,  true);
        }
        
    }
    
    private void checkHasNode(Element e, String xPath) {
        if(e.selectSingleNode(xPath) == null) throw new PlanException("Element '"+e.getName()+"' doesnt have a '" + xPath + "' defined");
    }

    private Placeable handleSubPlaceable(Element substructureElement, File structurePlan) {
        checkHasNode(substructureElement, POSITION);
        
        Vector position = readVectorValues((Element)substructureElement.selectSingleNode(POSITION));
        Direction direction;
        if(substructureElement.selectSingleNode(DIRECTION) != null) {
            String d = substructureElement.selectSingleNode(DIRECTION).getStringValue().trim();
            direction = matchDirection(d);
        } else {
            direction = Direction.EAST;
        }
        
        return getPlaceable(substructureElement, structurePlan, direction, position);
    }

    private Vector readVectorValues(Element e) {
        int x = getXPathIntValue(e, "X");
        int y = getXPathIntValue(e, "Y");
        int z = getXPathIntValue(e, "Z");
        return new Vector(x, y, z);
    }

    private Direction matchDirection(String value) {
        switch (value.toLowerCase().trim()) {
            case "east":
                return Direction.EAST;
            case "west":
                return Direction.WEST;
            case "south":
                return Direction.SOUTH;
            case "north":
                return Direction.NORTH;
            default:
                throw new PlanException("Expected values for 'Direction', got '" + value + "' but expected values [NORTH|EAST|SOUTH|WEST]");
        }
    }

    private boolean hasSubstructures(Document d) {
        return d.selectSingleNode(ROOT_ELEMENT + "/" + SUBSTRUCTURES) != null;
    }

    private Placeable getPlaceable(Element e, File structurePlan, Direction direction, Vector position) {
        Preconditions.checkArgument(isStructurePlan(e));
        if (e.selectSingleNode(SCHEMATIC_ELEMENT) != null) {
            return handleSchematic((Element) e.selectSingleNode(SCHEMATIC_ELEMENT), structurePlan, direction, position);
        } else if (e.selectSingleNode(STRUCTURELOT_ELEMENT) != null) {
            try {
                return handleStructureLot((Element) e.selectSingleNode(STRUCTURELOT_ELEMENT), position);
            } catch (NumberFormatException nfe) {
                throw new PlanException(nfe);
            }
        } else if (e.selectSingleNode(GENERATED_STRUCTURE_ELEMENT) != null) {
            return handlePlaceableGenerated(e, position);
        }
        throw new PlanException("StructurePlan doesn't have any placeable defined!");
    }

    private PlaceableSchematic handleSchematic(Element schematicElement, File structurePlan, Direction direction, Vector position) {
        String schematic = getXPathNodeValue(schematicElement, SCHEMATIC_ELEMENT);
        if (schematic != null) {
            File schematicFile = new File(structurePlan.getParent(), schematic);
            if (!schematicFile.exists()) {
                throw new PlanException("Schematic '" + schematicFile.getAbsolutePath() + "' doesn't exist");
            }
            return new PlaceableSchematic(schematicFile, direction, position);
        } else {
            throw new PlanException("Value of element 'Schematic' was invalid");
        }
    }

    private PlaceableStructureLot handleStructureLot(Element structureLotElement, Vector position) {
        int width = getXPathIntValue(structureLotElement, WIDTH_ELEMENT);
        int height = getXPathIntValue(structureLotElement, HEIGHT_ELEMENT);
        int length = getXPathIntValue(structureLotElement, LENGTH_ELEMENT);
        return new PlaceableStructureLot(position, width, height, length);
    }
    
    private PlaceableGenerated handlePlaceableGenerated(Element generatePlaceable, Vector position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void checkIsStructurePlan(Document d) {
        if (!isStructurePlan(d.getRootElement())) {
            throw new PlanException("Root element was not of type '" + ROOT_ELEMENT + "'");
        }
    }

    private boolean isStructurePlan(Element e) {
        return e.getName().equals(ROOT_ELEMENT);
    }

}
