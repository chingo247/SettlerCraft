package com.chingo247.settlercraft.structure.plan.processing;

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

import com.chingo247.settlercraft.structure.exception.PlanException;
import com.chingo247.settlercraft.commons.logging.SCLogger;
import com.chingo247.settlercraft.commons.util.LogLevel;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structure.plan.xml.XMLUtils;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlanProcessor extends RecursiveTask<StructurePlanComplex> {

    private File structurePlan;
    private StructurePlanComplex parent;
    private StructurePlanProcessorManager sppm;
    private final SCLogger LOG = SCLogger.getLogger();
    private StructurePlanManager reader;

    public StructurePlanProcessor(File structurePlan, StructurePlanProcessorManager sppm) {
        this(structurePlan, null, sppm);
    }

    public StructurePlanProcessor(File structurePlan, StructurePlanComplex parent, StructurePlanProcessorManager sppm) {
        Preconditions.checkNotNull(structurePlan);
        Preconditions.checkArgument(structurePlan.exists());
        this.structurePlan = structurePlan;
        this.parent = parent;
        this.sppm = sppm;
    }

    @Override
    protected StructurePlanComplex compute() {
        long start = System.currentTimeMillis();
        String path = structurePlan.getAbsolutePath();

        // Check if the StructurePlan was already loaded
        StructurePlanManager pmgr = StructurePlanManager.getInstance();
        StructurePlanComplex loadedPlan = pmgr.getPlan(path);
        if (loadedPlan != null) {
            return loadedPlan;
        }

        SAXReader reader = new SAXReader();
        StructurePlanComplex plan;
        try {
//            plan = new StructurePlan(structurePlan, parent);
            Document d = reader.read(structurePlan);
            //TODO VALIDATE DOCUMENT USING XSD

            // Read the data
            Element root = d.getRootElement();

            // Get and Set the Name
            String name = XMLUtils.getXPathNodeValue(root, "Name") == null ? "" : XMLUtils.getXPathNodeValue(root, "Name");

            // Get and Set the Price
            Node priceNode = root.selectSingleNode("Price"); // May be null
            double price = priceNode != null ? handlePrice(structurePlan, priceNode) : 0.0;

            // Get and Set the Placeable
            Node placementNode = root.selectSingleNode("Placeable"); // May NEVER be null (Checked in XSD)
            Node typeNode = placementNode.selectSingleNode("Type");
            if (typeNode == null) {
                throw new PlanException("Element <Type> was not defined in <Placeable>");
            }
            
            PlacementProcessor placeableProcessor = handlePlaceable(structurePlan, placementNode);
            placeableProcessor.fork();
            plan = new StructurePlanComplex(structurePlan, parent, placeableProcessor.join());

            // Now we have a plan... Set the name and the price
            plan.setName(name);
            plan.setPrice(price);

            // Get and Set subplaceables
            List<Node> substructureNodes = root.selectNodes("SubStructures/SubStructure");
            List<StructurePlanProcessor> spps = null;
            List<PlacementProcessor> pps = null;
            if (!substructureNodes.isEmpty()) {
                spps = new ArrayList<>();
                pps = new ArrayList<>();

                int index = 1; //DEBUG PURPOSES,  User is probably not a programmer... start with 1...
                for (Node n : substructureNodes) {
                    String t = getSubstructureType(n);
                    
                    if(t.trim().toLowerCase().equals("embedded")) {
                        // Perform recursion check here!
                        // Fully check branch for matchin types!
                        File f = sppm.handleEmbeddedPlan(structurePlan, n);
                        if (plan.matchesAnyAncestors(f.getAbsolutePath())) {
                            throw new PlanException("SubStructure #" + index + " matches a plan in his branch!");
                        }
                        spps.add(sppm.task(structurePlan, parent, n));
                    } else {
                        pps.add(handlePlaceable(structurePlan, placementNode));
                    }
                    index++;
                }
            }

            // Fork the processes
            if (pps != null) {
                for (PlacementProcessor pp : pps) {
                    pp.fork();
                }
            }
            if (spps != null) {
                for (StructurePlanProcessor spp : spps) {
                    spp.fork();
                }
            }

            // Collect the data
            if (pps != null) {
                for (PlacementProcessor pp : pps) {
                    plan.addPlacement(pp.join());
                }
            }
            if (spps != null) {
                for (StructurePlanProcessor spp : spps) {
                    plan.addStructurePlan(spp.join());
                }
            }

            // Recursive process SubStructurePlans
            if (parent == null) {
//                StructurePlanUtil.validate(plan);
            }
            LOG.print(LogLevel.INFO, structurePlan, "StructurePlan", System.currentTimeMillis() - start);
        } catch (PlanException ex) { 
            LOG.print(LogLevel.ERROR, "Error in '" + structurePlan.getAbsolutePath() + "' >> " + ex.getMessage(), "StructurePlan", null);
            plan = null;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            plan = null;
        }

        if (plan != null) {
            StructurePlanManager.getInstance().putStructurePlan(plan);
        }

        return plan;
    }

    private String getSubstructureType(Node n) {
        Node typeNode = n.selectSingleNode("Type");
        if (typeNode == null) {
            throw new PlanException("The element '<Type>' was not defined for Element '<" + n.getName() + ">'");
        }
        return typeNode.getText().trim();
    }

    

    private Double handlePrice(File structurePlan, Node n) {
        String value = n.getText();
        if (!NumberUtils.isNumber(value)) {
            throw new PlanException("Error in '" + structurePlan.getAbsolutePath() + "': The value of the element 'Price' is not a number");
        }
        return Double.parseDouble(value);
    }

    private PlacementProcessor handlePlaceable(File structurePlanFile, Node placeableContainingNode) throws IOException {
        String type = XMLUtils.getXPathNodeValue(placeableContainingNode, "Type");
        if (type == null) {
            throw new PlanException("Error in '" + structurePlanFile.getAbsolutePath() + ":'The element '<Type>' was not defined in Element '<" + placeableContainingNode.getName() + ">'");
        }

        switch (type.toLowerCase()) {
            case "structurelot": return new StructureLotProcessor(structurePlanFile, placeableContainingNode);
            case "generated": return new GeneratedPlacementProcessor(structurePlanFile,placeableContainingNode);
            case "schematic":
                String schematic = XMLUtils.getXPathNodeValue(placeableContainingNode, "Schematic");
                if(schematic == null) {
                    throw new PlanException("Error in '"+structurePlanFile.getAbsolutePath()+"': No '<Schematic>' element defined");
                }
                File schematicFile = new File(structurePlanFile.getParent(), schematic);
                if(!schematicFile.exists()) {
                    throw new PlanException("Error in '"+structurePlanFile.getAbsolutePath()+"': File '"+schematicFile.getAbsolutePath()+"' defined in element '<Schematic>' does not exist!");
                }
                return new SchematicPlacementProcessor(structurePlan, placeableContainingNode, schematicFile);
            default: throw new AssertionError("Unknown type: " + type);
        }

    }
    
    

}
