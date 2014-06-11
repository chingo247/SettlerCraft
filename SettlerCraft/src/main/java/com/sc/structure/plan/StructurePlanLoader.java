
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
package com.sc.structure.plan;

import com.sc.structure.StructurePlanManager;
import com.sc.structure.entity.plan.StructurePlan;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanLoader {
    

     


    public void loadStructures(File buildingFolder) {
        String[] extensions = {"yml"};
        Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);

        while (it.hasNext()) {
            final File f = it.next();
                    File yamlStructureFile = f;
                    StructurePlan plan;
                    try {
                        plan = load(yamlStructureFile);
                        File schematic = FileUtils.getFile(yamlStructureFile.getParent(), YamlConfiguration.loadConfiguration(yamlStructureFile).getString("schematic"));
                        StructurePlanManager.getInstance().add(plan, schematic);
                    } catch (StructurePlanException | FileNotFoundException ex) {
                        Logger.getLogger(StructurePlanLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
            
        }
    }

    public StructurePlan load(File structureYAML) throws FileNotFoundException, StructurePlanException {
        StructurePlan spv = null;
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(structureYAML);

            File schematicStructureFile = FileUtils.getFile(structureYAML.getParent(), config.getString("schematic"));
            if (!schematicStructureFile.exists()) {
                throw new FileNotFoundException("No such file: " + structureYAML.getParent() + "\"" + config.getString("schematic"));
            }

            SchematicFormat format = SchematicFormat.getFormat(schematicStructureFile);
            if (!format.isOfFormat(schematicStructureFile)) {
                System.err.print("[SCStructureAPI]: Unsupported format for " + format.getName() + " in: " + schematicStructureFile.getName());
                return null;
            }

            String id;
            if (config.contains("id")) {
                id = String.valueOf(config.get("id"));
            } else {
                throw new StructurePlanException("Missing 'id' node in " + structureYAML.getAbsolutePath());
            }
            String displayName;
            if (config.contains("displayname")) {
                displayName = String.valueOf(config.get("displayname"));
            } else {
                throw new StructurePlanException("Missing 'displayname' node");
            }
            

            spv = new StructurePlan(id, displayName, schematicStructureFile);

            if (config.contains("sign")) {
                String[] sign = config.getString("sign").split("\\s");
                if (sign.length != 3) {
                    throw new StructurePlanException("Invalid coordinates for sign in " + structureYAML.getAbsolutePath());
                } else {
                    try {
                        int x = Integer.parseInt(sign[0]);
                        int y = Integer.parseInt(sign[1]);
                        int z = Integer.parseInt(sign[2]);
                        spv.setSignLocation(x, y, z);
                    } catch (NumberFormatException nfe) {
                        throw new StructurePlanException("Invalid coordinates for sign in " + structureYAML.getAbsolutePath());
                    }
                }
            }
            
            

            if (config.contains("hide-sign-onComplete")) {
                spv.setHideSignOnComplete(config.getBoolean("hide-sign-onComplete"));
            }

//            if (config.contains("exits")) {
//                for (Object o : config.getList("exits")) {
//                    if (o instanceof List) {
//                        List l = (List) o;
//                        for (int i = 0; i < 3; i++) { // X Y Z
//                            System.out.println(l.get(i));
//                        }
//                    } else {
//                        throw new StructurePlanException("Invalid exits list");
//                    }
//                }
//            }
            if (config.contains("price")) {
                spv.setPrice(config.getDouble("price"));
            }

            if (config.contains("description")) {
                spv.setDescription(config.getString("description"));
            }

            if (config.contains("faction")) {
                spv.setFaction(config.getString("faction"));
            }

            if (config.contains("category")) {
                spv.setCategory(config.getString("category"));
            }

            if (config.contains("start-y")) {
                spv.setStartY(config.getInt("start-y"));
            }

            return spv;
        } catch (IOException | DataException ex) {
            Logger.getLogger(StructurePlanLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return spv;
    }

}