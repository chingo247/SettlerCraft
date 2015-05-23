/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.util;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.PlacementTypes;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.PlacementXMLConstants;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.StructurePlanXMLConstants;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class PlanGenerator {
    
    public static void generate(File directory) {
        Iterator<File> fileIterator = FileUtils.iterateFiles(directory, new String[]{"schematic"}, true);
        
        while(fileIterator.hasNext()) {
            File schemaFile = fileIterator.next();
            try {
                generatePlanFromSchematic(schemaFile);
            } catch (IOException ex) {
                Logger.getLogger(PlanGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void generatePlanFromSchematic(File file) throws IOException {
        String name = FilenameUtils.getBaseName(file.getName());
        File directory = file.getParentFile();
        
        Document d = DocumentHelper.createDocument();
        Element root = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT);
        d.add(root);
        
       
        
        Element nameElement = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_NAME_ELEMENT);
        nameElement.setText(name);
        root.add(nameElement);
        
        Element categoryElement = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_CATEGORY_ELEMENT);
        categoryElement.setText("Default");
        
        Element placementElment = new BaseElement(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);
        
        Element typeElement = new BaseElement(PlacementXMLConstants.TYPE_ELEMENT);
        typeElement.setText(PlacementTypes.SCHEMATIC);
        
        Element schematicElement = new BaseElement(PlacementXMLConstants.SCHEMATIC_ELEMENT);
        schematicElement.setText(file.getName());
        
        placementElment.add(typeElement);
        placementElment.add(schematicElement);
        
        root.add(placementElment);
        
        File planFile = new File(directory, name + ".xml");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileWriter(planFile), format);
        writer.write( d );
        writer.close();
    }
    
   
    
}
