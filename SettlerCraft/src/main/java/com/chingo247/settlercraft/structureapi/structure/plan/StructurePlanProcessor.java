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

import com.chingo247.settlercraft.structureapi.structure.plan.schematic.PlacementSchematicProcessorManager;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.exception.PlanException;
import com.chingo247.settlercraft.structureapi.structure.plan.XMLUtils;
import com.chingo247.settlercraft.structureapi.structure.plan.XMLUtils;
import com.chingo247.settlercraft.util.LogLevel;
import com.chingo247.settlercraft.util.Logger;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Level;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlanProcessor extends RecursiveTask<StructurePlan> {
    
    private File structurePlan;
    private StructurePlan parent;
    private StructurePlanProcessorManager sppm;  
    private PlacementSchematicProcessorManager spm;
    private final Logger LOG = new Logger();
    

    public StructurePlanProcessor(File structurePlan, StructurePlanProcessorManager sppm, PlacementSchematicProcessorManager spm) {
        this(structurePlan, null, sppm, spm);
    }

    public StructurePlanProcessor(File structurePlan, StructurePlan parent, StructurePlanProcessorManager sppm, PlacementSchematicProcessorManager spm) {
        Preconditions.checkNotNull(structurePlan);
        Preconditions.checkArgument(structurePlan.exists());
        this.structurePlan = structurePlan;
        this.parent = parent;
        this.sppm = sppm;
        this.spm = spm;
    }

    @Override
    protected StructurePlan compute() {
        long start = System.currentTimeMillis();
        String path = structurePlan.getAbsolutePath();
        
        // Check if the StructurePlan was already loaded
        StructurePlanManager pmgr = StructurePlanManager.getInstance();
        StructurePlan loadedPlan = pmgr.getPlan(path);
        if(loadedPlan != null) {
            return loadedPlan;
        }
        
        SAXReader reader = new SAXReader();
        StructurePlan plan;
        try {
//            plan = new StructurePlan(structurePlan, parent);
            Document d = reader.read(structurePlan);
            //TODO VALIDATE DOCUMENT USING XSD
            
            // Read the data
            Element root = d.getRootElement();
            
            // Get and Set the Name
            String name = XMLUtils.getXPathNodeValue(root, "Name") == null ? "" : XMLUtils.getXPathNodeValue(root, "Name") ;
            
            
            // Get and Set the Price
            Node priceNode = root.selectSingleNode("Price"); // May be null
            double price  = priceNode != null ? handlePrice(structurePlan,priceNode) : 0.0;
            
            
            
            // Get and Set the Placeable
            Node placementNode = root.selectSingleNode("Placeable"); // May NEVER be null (Checked in XSD)
            Node typeNode = placementNode.selectSingleNode("Type");
            if(typeNode == null) {
                throw new PlanException("Element <Type> was not defined in <Placeable>");
            }
            String type = typeNode.getText();
            PlacementProcessor placeableProcessor = null;
            Placement p = null;
            switch(type.trim().toLowerCase()) {
                case "schematic":
                    placeableProcessor = spm.task(structurePlan, placementNode);
                    break;
                case "structurelot":
                    p = handleStructureLot(placementNode);
                    break;
                case "generated":
                    placeableProcessor = new GeneratedPlacementProcessor(structurePlan, placementNode);
                    break;
            }
            if(placeableProcessor != null) {
                placeableProcessor.fork();
                p = placeableProcessor.join();
                if(p == null) return null;
            } 
            plan = new StructurePlan(structurePlan, parent, p);
            
            // Now we have a plan... Set the name and the price
            plan.setName(name);
            plan.setPrice(price);
            
            // Get and Set subplaceables
            
            
            List<Node> substructureNodes = root.selectNodes("SubStructures/SubStructure");
            List<StructurePlanProcessor> spps = null;
            List<PlacementProcessor> pps = null;
            if(!substructureNodes.isEmpty()) {
            spps = new ArrayList<>();
            pps = new ArrayList<>();    
               
                int index = 1; //DEBUG PURPOSES,  User is probably not a programmer... start with 1...
                for(Node n : substructureNodes) {
                    String t = handleSubStructureType(n);
                    
                    // Handle substructure types
                    switch(t.trim().toLowerCase()) {
                        case "embedded": 
                            // Perform recursion check here!
                            // Fully check branch for matchin types!
                            File f = sppm.handleEmbeddedPlan(structurePlan, n);
                            if(plan.matchesAnyAncestors(f.getAbsolutePath())) {
                                throw new PlanException("SubStructure #" + index + " matches a plan in his branch!");
                            }
                            
                            spps.add(sppm.task(structurePlan, parent, n, spm));
                            break;
                        case "schematic": 
                            pps.add(spm.task(structurePlan, n));
                            break;
                        case "structurelot":
                            plan.addPlacement(handleStructureLot(n));
                            break;
                        case "generated":
                            pps.add(new GeneratedPlacementProcessor(structurePlan, n));
                            break;
                        default:throw new PlanException("Unknown type : '" + t + "'");
                    }
                    index++;
                }
            }
            
            // Fork the processes
            if(pps != null) for(PlacementProcessor pp : pps) pp.fork();
            if(spps != null)for(StructurePlanProcessor spp : spps) spp.fork();
                
            // Collect the data
            if(pps != null) for(PlacementProcessor pp : pps) plan.addPlacement(pp.join());
            if(spps != null)for(StructurePlanProcessor spp : spps) plan.addStructurePlan(spp.join());
            
            // Recursive process SubStructurePlans
            
            if(parent == null) {
//                StructurePlanUtil.validate(plan);
            }
            LOG.print(LogLevel.INFO, structurePlan, "StructurePlan", System.currentTimeMillis() - start);
        } catch (DocumentException | IOException | PlanException ex) { // Catch every exception because we are on the highest level!
            LOG.print(LogLevel.ERROR, "Error in '" + structurePlan.getAbsolutePath() + "' >> " + ex.getMessage(), "StructurePlan", null);
            plan = null;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            plan = null;
        }
        
        if(plan != null) {
            StructurePlanManager.getInstance().putStructurePlan(plan);
        }
        
        return plan;
    }
    
    private String handleSubStructureType(Node n) {
        Node typeNode = n.selectSingleNode("Type");
        if(typeNode == null) {
            throw new PlanException("The element '<Type>' was not defined for Element '<"+n.getName()+">'");
        }
        return typeNode.getText().trim();
    }
    
    private StructureLot handleStructureLot(Node n) {
        long start = System.currentTimeMillis();
        Element pe = (Element) n;
//        Direction d = XMLUtils.hasDirection(pe) ? XMLUtils.getDirectionFrom(pe) : Direction.NORTH;
        Vector p = XMLUtils.hasPosition(pe) ? XMLUtils.getXYZFrom(pe) : Vector.ZERO;
        
        int width = XMLUtils.getXPathIntValue(pe, "Width");
        int height = XMLUtils.getXPathIntValue(pe, "Height");
        int length = XMLUtils.getXPathIntValue(pe, "Length");
        
        if(width < 0) throw new PlanException("Width must be greater than 0");
        if(height < 0) throw new PlanException("height must be greater than 0");
        if(length < 0) throw new PlanException("length must be greater than 0");
        
        StructureLot structureLot = new StructureLot(p, width, height, length);
//        structureLot.rotate(d);
        LOG.print(LogLevel.DEBUG, structureLot.getWidth() + "x" + structureLot.getHeight() + "x" + structureLot.getLength(), "StructureLot", start);
        return structureLot;
    }
   
    
    private Double handlePrice(File structurePlan, Node n) {
        String value = n.getText();
        if(!NumberUtils.isNumber(value)) {
            throw new PlanException("Error in '"+structurePlan.getAbsolutePath()+"': The value of the element 'Price' is not a number");
        }
        return Double.parseDouble(value);
    }
    
    private PlacementProcessor handlePlaceable(File structurePlanFile, Node placeableContainingNode) throws IOException {
        String type = XMLUtils.getXPathNodeValue(placeableContainingNode, "Type");
        if(type == null) throw new PlanException("Error in '"+structurePlanFile.getAbsolutePath()+":'The element '<Type>' was not defined in Element '<"+placeableContainingNode.getName()+">'");
            
        switch(type.toLowerCase()) {
//            case "generated": return new PlaceableGeneratedProcessor(structurePlanFile,placeableContainingNode);
            case "schematic": 
               
//                String schematic = XMLUtils.getXPathNodeValue(placeableContainingNode, "Schematic");
//                if(schematic == null) {
//                    throw new PlanException("Error in '"+structurePlanFile.getAbsolutePath()+"': No '<Schematic>' element defined");
//                }
//                File schematicFile = new File(structurePlanFile.getParent(), schematic);
//                if(!schematicFile.exists()) {
//                    throw new PlanException("Error in '"+structurePlanFile.getAbsolutePath()+"': File '"+schematicFile.getAbsolutePath()+"' defined in element '<Schematic>' does not exist!");
//                }
                return spm.task(structurePlanFile, placeableContainingNode);
//            case "structurelot": return new PlaceableStructureLotProcessor(structurePlanFile, placeableContainingNode);
            default: throw new AssertionError("Unreachable");
        }
        
    }
    
}
