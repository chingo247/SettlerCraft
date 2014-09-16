/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.plan;

import construction.exception.StructurePlanException;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlan {

    public static final String STRUCTUREPLAN_NODE = "structureplan";
    private final String configPath;

    private StructurePlan(File configPath) {
        this.configPath = configPath.getAbsolutePath();
    }
    
    public File getSchematic() {
        return getSchematic(new File(configPath));
    }
    
    
    public File getConfig() {
        return new File(configPath);
    }
    
    public String getId() {
        return getNodeTextValue("StructurePlan/Id");
    }
    
    public String getName() {
        return getNodeTextValue("StructurePlan/Name");
    }
    
    public String getCategory() {
        return getNodeTextValue("StructurePlan/Category");
    }

    public String getFaction() {
        return getNodeTextValue("StructurePlan/Faction");
    }
    
    public double getPrice() {
        double value = 0;
        String textValue = getNodeTextValue("StructurePlan/Price");
        if(textValue != null) {
            try {
                value = Double.parseDouble(textValue);
            } catch (NumberFormatException nfe) {
                value = 0; // Stays 0... anyway
            }
        }
        return value;
    }
    
    public String getNodeTextValue(String path) {
        Document d = getDocument();
        String value = null;
        if(d == null) {
            return null;
        }
        
        Node n = d.selectSingleNode(path);
        if(n != null) {
            value = n.getText();
        }
        return value;
    }
     
    private Document getDocument() {
        SAXReader reader = new SAXReader();
        try {
            Document d = reader.read(getConfig());
            return d;
        } catch (DocumentException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static StructurePlan load(File config) throws StructurePlanException {
        StructurePlan.valdidateConfig(config);
        return new StructurePlan(config);
    }

    private static boolean isStructurePlan(Document d) {
        Element root = d.getRootElement();

        return root.getName().equals(STRUCTUREPLAN_NODE);
    }
    
    private static boolean hasSchematic(File xml) {
        return getSchematic(xml) != null;
    }
    
    private static File getSchematic(File xml) {
        SAXReader reader = new SAXReader();
        Document d;
        File file;
        try {
            d = reader.read(xml);

            String path = d.selectSingleNode("StructurePlan/Schematic").getText();

            if (path == null || path.trim().isEmpty()) {
                return null;
            }

            file = new File(xml.getParent(), path);

            System.out.println(file.getAbsolutePath());

            if (!file.exists()) {
                return null;
            }

            if (!FilenameUtils.isExtension(file.getName(), "schematic")) {
                return null;
            }

        } catch (DocumentException ex) {
            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return file;
    }

    private static void valdidateConfig(File xml) throws StructurePlanException {
        
        SAXReader reader = new SAXReader();
        try {
            Document d = reader.read(xml);
            if (isStructurePlan(d)) {

                if (!hasSchematic(xml)) {
                    throw new StructurePlanException("no schematic was found for " + xml.getAbsolutePath());
                }

                Node idNode = d.selectSingleNode("StructurePlan/Id");
                if (idNode == null) {
                    throw new StructurePlanException("missing 'Id' node");
                }

                String id = idNode.getText();
                if(id.trim().length() == 0) {
                    throw new StructurePlanException("'Id' node is empty for " + xml.getAbsolutePath());
                }
                
                Node nameNode = d.selectSingleNode("StructurePlan/Name");
                if(nameNode == null) {
                    throw new StructurePlanException("Missing 'Name' node for " + xml.getAbsolutePath());
                }
                
            }

        } catch (DocumentException ex) {
            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isStructurePlan(ItemStack itemStack) {
        List<String> lore = itemStack.getItemMeta().getLore();
        if(lore.isEmpty()) return false;
        else {
            for(String s : lore) {
                if(s.contains("[Type]") && s.contains("Plan")){
                    return true;
                }
            }
            return false;
        }
    }
    
    public static String getPlanID(ItemStack itemStack) {
        if(isStructurePlan(itemStack)) {
            List<String> lore = itemStack.getItemMeta().getLore();
            for(String s : lore) {
                if(s.contains("Id")){
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    return s.trim();
                }
            }
        } 
        return null;
    }

}
