/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan;

import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.structure.plan.data.Nodes;
import com.sc.structureapi.structure.plan.data.SettlerCraftElement;
import com.sc.structureapi.structure.plan.holograms.StructureHologram;
import com.sc.structureapi.structure.plan.holograms.StructureHologramLoader;
import com.sc.structureapi.structure.plan.overview.StructureOverview;
import com.sc.structureapi.structure.plan.overview.StructureOverviewLoader;
import com.sk89q.worldguard.protection.flags.Flag;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class StructurePlan {

    private final File configXML;
    private final Document configDoc;
    private File schematic;
    private String name;
    private String category = "Default";
    private String faction = "Default";
    private String description = "";
    private double price = 0.0d;
    private int startHeight;
    private List<StructureOverview> overviews;
    private List<StructureHologram> holograms;
    private List<Flag> flags;

    private List<File> resources;

    public StructurePlan(File configXML) throws DocumentException {
        this.configXML = configXML;
        this.configDoc = new SAXReader().read(configXML);
    }

    public void save() throws DocumentException, IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setExpandEmptyElements(true);

        XMLWriter writer = new XMLWriter(new FileWriter(configXML), format);
        try {
            set(Nodes.NAME_NODE, name);
            set(Nodes.CATEGORY_NODE, category);
            set(Nodes.DESCRIPTION_NODE, description);
            set(Nodes.FACTION_NODE, faction);
            set(Nodes.PRICE_NODE, price);
            set(Nodes.STRUCTURE_SCHEMATIC_NODE, schematic.getName());

            writer.write(configDoc);

        } finally {
            writer.close();
        }
    }
    
    public File getConfigXML() {
        return configXML;
    }

    public void set(String xPath, Object e) throws IOException {
        set(xPath, String.valueOf(e));
    }

    public void set(String xPath, String text) throws IOException {
        synchronized (configDoc) {

            Node n = configDoc.selectSingleNode(xPath);
            if (n == null) {
                makeNodes(xPath);
                n = configDoc.selectSingleNode(xPath);
            }
            n.setText(text);
        }
    }

    public void set(String xPathParent, List<? extends SettlerCraftElement> elements) throws IOException {

        synchronized (configDoc) {
            remove(xPathParent);
            for (SettlerCraftElement e : elements) {
                append(xPathParent, e);
            }
        }
    }

    public void append(String xPathParent, SettlerCraftElement element) throws IOException {
        synchronized (configDoc) {
            Node n = configDoc.selectSingleNode(xPathParent);
            if (n == null) {
                makeNodes(xPathParent);
                n = configDoc.selectSingleNode(xPathParent);
            }
            Element e = (Element) n;
            System.out.println(e);
            e.add(element.asElement());
        }
    }

    public void remove(String xPath) throws IOException {
        synchronized (configDoc) {
            remove(configDoc.selectSingleNode(xPath));
        }
    }

    public void remove(Node node) {
        if (node != null) {
            synchronized (configDoc) {
                node.detach();
            }
        }
    }

    private void makeNodes(String xPath) throws IOException {

//        XMLWriter writer = new XMLWriter(new FileWriter(configXML));
        String[] nodes = xPath.split("/");
        Element root = configDoc.getRootElement();

        for (int i = 1; i < nodes.length; i++) {
            String node = nodes[i];
            Node child = root.selectSingleNode(node);
            if (child == null) {
                root = root.addElement(node);
            } else {
                root = (Element) child;
            }
        }

        root.selectNodes(xPath);

//        writer.write(configDoc);
//        writer.close();
    }

    public void load() throws DocumentException, StructureDataException, IOException {
        Document d;
        synchronized (configXML) {
            d = new SAXReader().read(configXML);
        }
        handleSchematic(d);
        name = getValue(d, Nodes.NAME_NODE, FilenameUtils.getBaseName(schematic.getName()));

        category = getValue(d, Nodes.CATEGORY_NODE, "Default");

        faction = getValue(d, Nodes.FACTION_NODE, "Default");

        description = getValue(d, Nodes.DESCRIPTION_NODE, "Default");

        try {
            price = Double.parseDouble(getValue(d, Nodes.PRICE_NODE, "0.0"));
        } catch (NumberFormatException nfe) {
            throw new StructureDataException("Value of 'Price' must be a number in " + configXML.getAbsolutePath());
        }
        if (d.selectSingleNode(Nodes.STRUCTURE_OVERVIEWS_NODE) != null) {
            overviews = new StructureOverviewLoader().load(d);
        }
        if (d.selectSingleNode(Nodes.HOLOGRAMS_NODE) != null) {
            holograms = new StructureHologramLoader().load(d);
        }

    }

    private String getValue(Document d, String xPath, String defaultValue) throws StructureDataException {
        Node n = d.selectSingleNode(xPath);
        if (n == null || n.getText().trim().isEmpty()) {
            return defaultValue;
        }
        return n.getText();
    }

    private void handleSchematic(Document d) throws StructureDataException, FileNotFoundException {
        Node schematicNode = d.selectSingleNode(Nodes.STRUCTURE_SCHEMATIC_NODE);
        if (schematicNode == null) {
            throw new StructureDataException("Missing  Structure Schematic in " + configXML.getAbsolutePath());
        }

        File s = new File(configXML.getParent(), schematicNode.getText());
        if (s.exists()) {
            schematic = s;
        } else {
            throw new FileNotFoundException("Couldn't resolve path for " + s.getAbsolutePath() + " for config:  " + configXML.getAbsolutePath());
        }
    }

    public static void main(String... args) {

        try {
            StructurePlan plan = new StructurePlan(new File("E:\\GAMES\\MineCraftServers\\Bukkit 1.7.9\\plugins\\SettlerCraft\\Plans\\Arena\\Colosseum.xml"));
            plan.load();
//            plan.append(Nodes.STRUCTURE_OVERVIEWS_NODE, new StructureOverview(0, 2, 0));
            List<StructureOverview> overviews = new ArrayList<>();
            overviews.add(new StructureOverview(1, 1, 1));
            overviews.add(new StructureOverview(1, 2, 1));
            overviews.add(new StructureOverview(1, 1, 3));
            plan.set(Nodes.STRUCTURE_OVERVIEWS_NODE, overviews);

            plan.save();

        } catch (DocumentException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (StructureDataException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    

    public File getSchematic() {
        return schematic;
    }

    public void setSchematic(File schematic) {
        this.schematic = schematic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public void setStartHeight(int startHeight) {
        this.startHeight = startHeight;
    }
    
    public String getRelativePath() {
        String basePath = StructurePlanManager.getInstance().getPlanFolder().getAbsolutePath();
        return configXML.getAbsolutePath().substring(basePath.length() + 1);
    }
    
    

    public static boolean isStructurePlan(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getType() != Material.PAPER) {
            return false;
        }

        List<String> lore = itemStack.getItemMeta().getLore();
        if (lore.isEmpty()) {
            return false;
        } else {
            for (String s : lore) {
                if (s.contains("Type") && s.contains("Plan")) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String getPlanID(ItemStack itemStack) {
        if (isStructurePlan(itemStack)) {
            List<String> lore = itemStack.getItemMeta().getLore();
            for (String s : lore) {
                if (s.contains("Path")) {
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    return s.trim();
                }
            }
        }
        return null;
    }

}
