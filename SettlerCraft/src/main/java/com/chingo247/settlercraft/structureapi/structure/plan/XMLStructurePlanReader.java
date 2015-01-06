
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
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class XMLStructurePlanReader extends AXMLReader {

    public StructurePlan read(File structurePlan) throws DocumentException {
        read(structurePlan, null, Direction.EAST, Vector.ZERO, true);
        // ROTATE STUFF HERE!
        
        // Perform Checks HERE!
    }
    
    private StructurePlan read(File structurePlan, StructurePlan parent, Vector position, boolean recursive) throws DocumentException {
        StructurePlan plan = read(structurePlan, parent, Direction.EAST, position, recursive);
        
        // Validate Parent-Child relation
        checkParentChild(parent, plan, true);
        checkOverlapOnSameLevel(plan);
    }
    
    /**
     * Validates the parent child relation of the StructurePlans. Constraints as location
     * @param parent
     * @param child 
     */
    private void checkParentChild(StructurePlan parent, StructurePlan child, boolean shouldCheckAnother) {
        if(parent == null) return; // No relation to check
        
        // If there is overlap check if this is allowed.
        if(CuboidDimension.overlaps(parent.placeable, child.placeable)) {
            if(isGenerated(parent.placeable)) {
                return; // Generated Stuff may be overlapped whatsoever
            
            // If there is overlap on a StructureLot, the child needs to be completely inside it's parent
            } else if((isStructureLot(parent.placeable) && !CuboidDimension.isDimensionWithin(parent.placeable, child.placeable))
                        || (isSchematic(parent.placeable) && !CuboidDimension.isDimensionWithin(parent.placeable, child.placeable))) {
                String message = "SubStructurePlan #" + getChildIndex(child) + " ";
                    if(shouldCheckAnother) {
                        message += " overlaps it's parent, but was not fully inside"; 
                    } else {
                        message += " needs to be atleast within it"; 
                    }
            } 
        // Only check one level above    
        } else if(shouldCheckAnother) {
            // If parent was null method will do nothing
            // Otherwise the Same rules are applied
            checkParentChild(parent.getParent(), child, false);
        }
        
    }
    
    private void checkOverlapOnSameLevel(StructurePlan plan) {
        Map<Integer, List<StructurePlan>> tree = new HashMap<>();
        recursivePutLevels(plan, tree, 0);
        for(int i = 0; i < tree.size(); i++) {
            List<StructurePlan> plans = tree.get(i);
            
            
            Iterator<StructurePlan> allIterator = plans.iterator();
            while(allIterator.hasNext()) {
                StructurePlan p = allIterator.next();
                
                // Guarantees that plans are not checked twice upon eachoter
                Iterator<StructurePlan> it = plans.iterator();
                while(it.hasNext()) {
                    StructurePlan o = it.next();
                    if(p.uuid.equals(o.uuid)) continue;
                    
                    if(CuboidDimension.overlaps(p.placeable, o.placeable)) {
                        checkMayOverlap(plan, o); 
                    }
                    it.remove();
                }
            }
            
            
            
            
        }
    }
    
    private void checkMayOverlap(StructurePlan plan, StructurePlan other) {
        
    }
    
    /**
     * Populates a map recursively, so we know at what levels what StructurePlans may overlap eachother
     * @param plan The plan
     * @param tree The holder
     * @param depth The current depth
     */
    private void recursivePutLevels(StructurePlan plan, Map<Integer, List<StructurePlan>> tree, int depth) {
        for(StructurePlan subStructurePlan : plan.getSubStructurePlans()) {
            int m_depth = depth;
            
            // if it's outside its parent than it's actually a level higher in the Tree
            if(plan.hasParent() && !CuboidDimension.overlaps(plan.parent.placeable, plan.placeable)) {
                m_depth--; 
            }
            
            if(!tree.containsKey(m_depth)) tree.put(depth, new ArrayList<StructurePlan>());
            tree.get(m_depth).add(subStructurePlan);
            
            // Check children, recursive call here!
            recursivePutLevels(subStructurePlan, tree, depth + 1);
        }
    }
    
    
    
   
    
    private boolean isSchematic(Placeable placeable) {
        return placeable instanceof PlaceableSchematic;
    }
    
    private boolean isStructureLot(Placeable placeable) {
        return placeable instanceof PlaceableStructureLot;
    }
    
    private boolean isGenerated(Placeable placeable) {
        return placeable instanceof PlaceableGenerated;
    }
    
    /**
     * Used to get the index of the StructurePlan where an error occurred.
     * returns -1 if there is no parent.
     * @param plan
     * @return 
     */
    private int getChildIndex(StructurePlan plan) {
        if(!plan.hasParent()) {
            return -1;
        } else {
            int index = 0;
            for(StructurePlan p : plan.getParent().getSubStructurePlans()) {
                if(p.uuid.equals(plan.uuid)) return index;
                else index++;
            }
        }
        throw new AssertionError("StructurePlan was not a Child of it's parent!");
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
