package com.chingo247.settlercraft.structure.plan;

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
import com.chingo247.settlercraft.model.util.LogLevel;
import com.chingo247.settlercraft.model.util.SCLogger;
import com.chingo247.settlercraft.structure.plan.exception.PlanException;
import com.chingo247.settlercraft.structure.placement.PlacementProcessor;
import com.chingo247.settlercraft.structure.plan.document.PlacementElement;
import com.chingo247.settlercraft.structure.plan.document.SimpleElement;
import com.chingo247.settlercraft.structure.plan.document.StructurePlanDocument;
import com.chingo247.settlercraft.structure.plan.document.SubStructureElement;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlanProcessor extends RecursiveTask<StructurePlan> {

    private File structurePlanFile;
    private StructurePlanComplex parent;
    private final SCLogger LOG = SCLogger.getLogger();

    StructurePlanProcessor(File structurePlan) {
        this(structurePlan, null);
    }

    StructurePlanProcessor(File structurePlan, StructurePlanComplex parent) {
        Preconditions.checkNotNull(structurePlan);
        Preconditions.checkArgument(structurePlan.exists());
        this.structurePlanFile = structurePlan;
        this.parent = parent;
    }

    @Override
    protected StructurePlan compute() {
        long start = System.currentTimeMillis();

        StructurePlanDocument planDocument = null;
        try {
            planDocument = StructurePlanDocument.read(structurePlanFile);
            planDocument.checkNotNull("Placement");

            String name = planDocument.getName();
            String id = planDocument.getReferenceId();
            double price = planDocument.getPrice();
            String description = planDocument.getDescription();

            PlacementElement placementElement = planDocument.getPlacementElement();
            PlacementProcessor placementProcessor = new PlacementProcessor(placementElement);
            placementProcessor.fork();

            if (planDocument.hasSubStructureElements()) {

                StructurePlanComplex plan = new StructurePlanComplex(id, structurePlanFile, parent, placementProcessor.join());
                plan.setName(name);
                plan.setPrice(price);
                plan.setDescription(description);

                // Get and Set subplaceables
                List<SubStructureElement> substructureElements = planDocument.getSubStructureElements();
                List<StructurePlanProcessor> spps = null;
                List<PlacementProcessor> pps = null;
                if (!substructureElements.isEmpty()) {
                    spps = new ArrayList<>();
                    pps = new ArrayList<>();

                    for (SubStructureElement subStructureElement : substructureElements) {
                        String t = subStructureElement.getType();

                        if (t.trim().toLowerCase().equals("embedded")) {
                            // Perform recursion check here!
                            // Fully check branch for matchin types!
                            File f = handleEmbeddedPlan(structurePlanFile, subStructureElement);
                            if (plan.matchesAnyAncestors(f)) {
                                throw new PlanException("Element <" + subStructureElement.getElementName() + "> on line " + subStructureElement.getLine()
                                        + " matches a plan in his branch!");
                            }
                            spps.add(new StructurePlanProcessor(f, parent));
                        } else {
                            pps.add(new PlacementProcessor(subStructureElement));
                        }
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

            } else {
                SimpleStructurePlan plan = new SimpleStructurePlan(id, structurePlanFile, placementProcessor.join());
                plan.setName(name);
                plan.setPrice(price);
                plan.setDescription(description);
                return plan;
            }

            LOG.print(LogLevel.INFO, structurePlanFile, "StructurePlan", System.currentTimeMillis() - start);
        } catch (PlanException ex) {
            LOG.print(LogLevel.ERROR, "Error in '" + structurePlanFile.getAbsolutePath() + "' >> " + ex.getMessage(), "StructurePlan", null);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
        }

        return null;
    }

    private File handleEmbeddedPlan(File structurePlan, SubStructureElement element) throws DocumentException {
        element.checkNotNull("File");

        SimpleElement simpleElement = element.selectSingleElement("File");
        simpleElement.checkNotEmpty();

        File f = new File(structurePlan.getParent(), element.getTextValue());
        if (!f.exists()) {
            throw new PlanException("File reference '" + f.getAbsolutePath() + "' defined on line " + simpleElement.getLine() + " does not exist!");
        }
        if (!isStructurePlan(f)) {
            throw new PlanException("File is not a valid StructurePlan file");
        }
        return f;
    }

    private boolean isStructurePlan(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document d = reader.read(file);
        return d.getRootElement().getName().equals("StructurePlan");
    }

}
