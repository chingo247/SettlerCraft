package com.chingo247.structureapi.world;

import com.chingo247.structureapi.structure.exception.WorldConfigException;
import fiber.core.impl.BuildContextImpl;
import fiber.core.impl.xml.ModelReader;
import fiber.core.impl.xml.located.LocatedElement;
import fiber.core.impl.xml.located.LocatedElementDocumentFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.BaseElement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chingo
 */
public class WorldConfig {
    
    private File file;
    private boolean allowsStructures;
    private boolean zonesOnly;
    
    protected WorldConfig(WorldConfig config) {
        this.file = config.file;
        this.allowsStructures = config.allowsStructures;
        this.zonesOnly = config.zonesOnly;
    }
    
    
    private WorldConfig(File file) {
        this.file = file;
    }

    public boolean isZonesOnly() {
        return zonesOnly;
    }

    public void setAllowsStructures(boolean allowsStructures) {
        this.allowsStructures = allowsStructures;
    }

    public boolean allowsStructures() {
        return allowsStructures;
    }

    public void setZonesOnly(boolean zonesOnly) {
        this.zonesOnly = zonesOnly;
    }
    
    public static WorldConfig load(File file) throws WorldConfigException {
        ModelReader reader = ModelReader.getNonValidatingInstance(new BuildContextImpl());
        try {
            Document d = reader.read(file);
            
            
            Element root = d.getRootElement();
            
            boolean isZonesOnly = false;
            boolean allowsStructures = false;
            
            
           
            
            LocatedElement isZonesOnlyElement = (LocatedElement) root.selectSingleNode("IsZonesOnly");
            if(isZonesOnlyElement == null) {
                throw new WorldConfigException("Missing element '<IsZonesOnly>' in '<WorldConfig>'");
            }
            
            String value = isZonesOnlyElement.getText().toLowerCase().trim();
            if(value.isEmpty() || (!value.equals("false") && !value.equals("true"))) {
                throw new WorldConfigException("Error in WorldConfig '"+file.getAbsolutePath()+"' on line " + isZonesOnlyElement.getLine() + ": "
                        + "value should be either of 'true' or 'false'");
            } else {
                isZonesOnly = Boolean.parseBoolean(value);
            }
            
            LocatedElement allowStructuresElement = (LocatedElement) root.selectSingleNode("AllowStructures");
            if(allowStructuresElement == null) {
                throw new WorldConfigException("Missing element '<AllowStructures>' in '<WorldConfig>'");
            }
            
            value = allowStructuresElement.getText().toLowerCase().trim();
            if(value.isEmpty() || (!value.equals("false") && !value.equals("true"))) {
                throw new WorldConfigException("Error in WorldConfig '"+file.getAbsolutePath()+"' on line " + allowStructuresElement.getLine() + ": "
                        + "value should be either of 'true' or 'false'");
            } else {
                allowsStructures = Boolean.parseBoolean(value);
            }
            
            
            
            WorldConfig config = new WorldConfig(file);
            config.setAllowsStructures(allowsStructures);
            config.setZonesOnly(isZonesOnly);
            
            return config;
        } catch (DocumentException ex) {
            Logger.getLogger(WorldConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static WorldConfig createDefault(File file) {
        Document d = LocatedElementDocumentFactory.getInstance().createDocument(new BaseElement("WorldConfig"));
        
        Element root = d.getRootElement();
        
        // set zones only
        Element zonesOnlyElement = new LocatedElement("IsZonesOnly");
        zonesOnlyElement.setText("false");
        root.add(zonesOnlyElement);
        
        // set allows structures
        Element allowStructureElement = new LocatedElement("AllowStructures");
        allowStructureElement.setText("true");
        root.add(allowStructureElement);
        XMLWriter writer = null;
        try {
            
            OutputFormat format = OutputFormat.createPrettyPrint();
            writer = new XMLWriter(new FileWriter(file), format);
            writer.write(d);
            
            writer.close();
            
            return load(file);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorldConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | WorldConfigException ex) {
            Logger.getLogger(WorldConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    // close silent...
                }
            }
        }
        return null;
    }
    
    
}
