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
package com.chingo247.settlercraft.structure.plan.document;

import com.chingo247.settlercraft.structure.plan.SettlerCraftPlanManager;
import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.plan.SettlerCraftPlan;
import com.chingo247.settlercraft.structure.plan.data.Elements;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class PlanGenerator {
    
    public static final File SCHEMATIC_TO_PLAN_FOLDER = new File(SettlerCraft.getInstance().getDataFolder(), "SchematicToPlan");
    
    public static void generate(File targetFolder) {
        // Scan the folder called 'SchematicToPlan' for schematic files
        Iterator<File> it = FileUtils.iterateFiles(SCHEMATIC_TO_PLAN_FOLDER, new String[]{"schematic"}, true);

        int count = 0;
        long start = System.currentTimeMillis();

        // Generate Plans
        while (it.hasNext()) {
            File schematic = it.next();

            Document d = DocumentHelper.createDocument();
            d.addElement(Elements.ROOT)
                    .addElement(Elements.SETTLERCRAFT)
                    .addElement(Elements.SCHEMATIC)
                    .setText(schematic.getName());

            File plan = new File(schematic.getParent(), FilenameUtils.getBaseName(schematic.getName()) + ".xml");

            try {
                XMLWriter writer = new XMLWriter(new FileWriter(plan));
                writer.write(d);
                writer.close();
                SettlerCraftPlan sp = new SettlerCraftPlan();
                PlanDocument pd = new PlanDocument(plan);
                
                sp.load(pd);
                if (sp.getCategory().equals("Default") && !schematic.getParentFile().getName().equals(SCHEMATIC_TO_PLAN_FOLDER.getName())) {
                    sp.setCategory(schematic.getParentFile().getName());
                }

                sp.save();

            } catch (DocumentException ex) {
                Logger.getLogger(SettlerCraftPlanManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | StructureDataException ex) {
                Logger.getLogger(SettlerCraftPlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            count++;
        }
        if (count > 0) {
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "Generated " + count + " plans in " + (System.currentTimeMillis() - start) + "ms");
        }
    }
    
}
