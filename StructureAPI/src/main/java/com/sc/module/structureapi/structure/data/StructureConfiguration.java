/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.data;

import construction.exception.StructureDataException;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructureConfiguration {

    private final String id;
    private final String name;
    private final String category;
    private final String faction;
    private final double price;
    private final File schematic;

    StructureConfiguration(String id, String name, String category, String faction, double price, File schematic) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.faction = faction;
        this.price = price;
        this.schematic = schematic;
    }

    public static StructureConfiguration load(File config) throws DocumentException, StructureDataException {
        SAXReader reader = new SAXReader();
        Document cfg = reader.read(config);

        Node idNode = cfg.selectSingleNode("StructurePlan/StructureAPI/Configuration/Id");
        Node nameNode = cfg.selectSingleNode("StructurePlan/StructureAPI/Configuration/Name");
        Node categoryNode = cfg.selectSingleNode("StructurePlan/StructureAPI/Configuration/Category");
        Node factionNode = cfg.selectSingleNode("StructurePlan/StructureAPI/Configuration/Faction");
        Node priceNode = cfg.selectSingleNode("StructurePlan/StructureAPI/Configuration/Price");

        String id;
        String name;
        String category;
        String faction;
        double price;

        if (idNode == null) {
            throw new StructureDataException("missing id node for: " + config.getAbsolutePath());
        }
        id = idNode.getText();

        if (nameNode == null) {
            throw new StructureDataException("Missing name node for: " + config.getAbsolutePath());
        }
        name = nameNode.getText();

        category = categoryNode != null ? categoryNode.getText() : "All";
        faction = factionNode != null ? factionNode.getText() : "Default";

        try {
            price = priceNode != null ? Double.parseDouble(priceNode.getText()) : 0d;
        } catch (NumberFormatException nfe) {
            throw new StructureDataException("Invalid price value for: " + config.getAbsolutePath());
        }

        String path = cfg.selectSingleNode("StructurePlan/StructureAPI/Schematic").getText();

        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        File schematicFile = new File(config.getParent(), path);

        if (!schematicFile.exists()) {
            return null;
        }

        if (!FilenameUtils.isExtension(schematicFile.getName(), "schematic")) {
            return null;
        }

        return new StructureConfiguration(id, name, category, faction, price, schematicFile);
    }

}
