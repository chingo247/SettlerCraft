/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.structure.plan.document.DocumentHelper;
import com.chingo247.settlercraft.bukkit.plan.holograms.StructureHologram;
import com.chingo247.settlercraft.bukkit.plan.overviews.StructureOverview;
import com.chingo247.settlercraft.bukkit.plan.worldguard.StructureRegionFlag;
import com.chingo247.settlercraft.structureapi.exception.PlanException;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public class SettlerCraftPlan extends PluginElement {

    private static final String SETTLERCRAFT_ROOT = "SettlerCraft";

    private String name;

    private File schematic;

    private String category;

    private String faction;

    private String description;

    private double price;

    public SettlerCraftPlan(StructureDocument d) {
        super(SETTLERCRAFT_ROOT, d);
        this.price = 0.0d;
        this.faction = "Default";
        this.category = "Default";
    }

    @Override
    public void load() {
        Element root = getElement();
        DocumentHelper.checkNotNull(root, "Schematic");

        Iterator<Element> it = root.elements().iterator();
        while (it.hasNext()) {
            Element e = it.next();

            switch (e.getName()) {
                case "Name": name = e.getText(); break;
                case "Category": category = e.getText(); break;
                case "Faction": faction = e.getText(); break;
                case "Schematic": 
                    schematic = new File(getDocument().getDirectory(), e.getText());
                    if(!schematic.exists()) throw new PlanException("Schematic '" + schematic.getAbsolutePath() + "' doesn't exist");
                    break;
                case "Price": price = DocumentHelper.getDouble(e); break;
                case "Description": description = e.getText(); break;
                case "Substructures":
                default: break; // ignore unknown elements
            }

        }

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public String getFaction() {
        return faction;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public File getSchematic() {
        return schematic;
    }

    @Override
    public Element toElement() throws DocumentException {
        XStream stream = new XStream();
        Document d = org.dom4j.DocumentHelper.parseText(stream.toXML(this));
        return null;
    }

    public static void main(String[] args) {
        try {
            StructureDocument d = new StructureDocument(new File("F:\\GAMES\\MineCraftServers\\Bukkit 1.7.10-SettlerCraft-RC4\\plugins\\SettlerCraft\\Plans\\Example.xml"));
            SettlerCraftPlan plan = new SettlerCraftPlan(d);
            plan.load();

            System.out.println(plan.toElement().asXML());

        } catch (DocumentException ex) {
            Logger.getLogger(SettlerCraftPlan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
