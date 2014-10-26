/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.util.FileUtil;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.exception.StructureDataException;
import com.chingo247.structureapi.plan.StructurePlan;
import com.sc.module.menuapi.menus.menu.CategoryMenu;
import com.sc.module.menuapi.menus.menu.MenuAPI;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.bukkit.Material;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class PlanMenuManager {

    private static final String PLANSHOP_NAME = "Buy & Build";
    private static final String RESOURCE_FOLDER = "com/chingo247/settlercraft/resources";
    private static final File PLUGIN_FOLDER = SettlerCraft.getInstance().getDataFolder();
    private CategoryMenu planMenu;
    private final Logger LOGGER = Logger.getLogger(PlanMenuManager.class);
    private boolean loadingPlans = false;
    private final SettlerCraft setterCraft;
    
    public PlanMenuManager(SettlerCraft settlerCraft) {
        this.setterCraft = settlerCraft;
    }

    public CategoryMenu getPlanMenu() {
        return planMenu;
    }

    
    
    /**
     * Loads the PlanMenu from the menu.xml. If menu.xml doesn't exist, the menu.xml from the jar
     * will be written to the FileSystem.
     *
     * @throws DocumentException When XML is invalid
     * @throws StructureAPIException When XML contains invalid data
     */
    public final void init() throws DocumentException, StructureAPIException {
        File file = new File(PLUGIN_FOLDER, "menu.xml");
        if (!file.exists()) {
            InputStream input = PlanMenuManager.class.getClassLoader().getResourceAsStream(RESOURCE_FOLDER + "/menu.xml");
            FileUtil.write(input, file);
        }

        Document d = new SAXReader().read(file);
        List<Node> rows = d.selectNodes("Menu/SlotRow");
        if (rows.size() > 2) {
            throw new StructureAPIException("Max rows is 2 for menu.xml");
        }

        planMenu = MenuAPI.createMenu(SettlerCraft.getInstance(), PLANSHOP_NAME, 54);
        boolean hasRow2 = false;

        for (int row = 0; row < rows.size(); row++) {
            Node rowNode = rows.get(row);
            List<Node> slotNodes = rowNode.selectNodes("Slot");
            // Slot #1 is reserved for all category
            if (row == 0 && slotNodes.size() > 8) {
                throw new StructureAPIException(" 'SlotRow#1' has max 8 slots to customize");
            } else if (slotNodes.size() > 9) {
                throw new StructureAPIException(" 'SlotRow#" + (row + 2) + "' has max 9 slots to customize");
            }
            int count = 0;

            for (Node categorySlotNode : slotNodes) {

                
                
                if (!categorySlotNode.hasContent()) {
                    count++;
                    continue;
                }

                Node mId = categorySlotNode.selectSingleNode("MaterialID");
                Node cat = categorySlotNode.selectSingleNode("Category");
                Node ali = categorySlotNode.selectSingleNode("Aliases");

                if (mId == null) {
                    throw new StructureAPIException("Missing 'MaterialID' element in 'SlotRow#" + (row + 1) + "' 'Slot#" + (count + 1) + "'");
                }
                if (cat == null) {
                    throw new StructureAPIException("Missing 'Category' element in 'SlotRow#" + (row + 1) + "' 'Slot#" + (count + 1) + "'");
                }

                int id;
                try {
                    id = Integer.parseInt(mId.getText());
                } catch (NumberFormatException nfe) {
                    throw new StructureAPIException("Invalid number for 'MaterialID' element in 'SlotRow#" + (row + 1) + "' 'Slot#" + (count + 1) + "'");
                }
                String category = cat.getText();
                if (category.isEmpty()) {
                    Element catEl = (Element) cat;
                    category = catEl.attributeValue("value");
                }
                if (category.trim().isEmpty()) {
                    throw new StructureAPIException("Empty 'Category' element in 'SlotRow#" + (row + 1) + "' and 'Slot#" + (count + 1) + "'");
                }
                category = category.replaceAll(" AND ", "&");

                String[] aliases;
                if (ali == null) {
                    aliases = new String[0];
                } else {
                    List<Node> aliasNodes = ali.selectNodes("Alias");
                    aliases = new String[aliasNodes.size()];
                    for (int j = 0; j < aliasNodes.size(); j++) {
                        String alias = aliasNodes.get(j).getText();

                        if (alias.isEmpty()) {
                            Element aliasEl = (Element) cat;
                            alias = aliasEl.attributeValue("value");
                        }
                        if (alias.trim().isEmpty()) {
                            throw new StructureAPIException("Empty 'Alias' element in  'SlotRow#" + (row + 1) + "' and 'Slot#" + (count + 1) + "' and 'Alias#" + (j + 1) + "'");
                        }

                        aliases[j] = aliasNodes.get(j).getText();
                    }
                }
                int slot = count;
                if (row == 0) {
                    slot += 1; // slot 0 is reserved...
                } else {
                    
                    hasRow2 = true;
                }

                planMenu.putCategorySlot((row * 9) + slot , category, Material.getMaterial(id), aliases);

                count++;
            }
            // fill remaining
            if(count < 8 && row == 0) {
                for(int i = count; i < 8; i++) {
                    planMenu.putLocked(i);
                }
                
            } else if(row > 0 && count < 9) {
                for(int i = count; i < 9; i++) {
                    planMenu.putLocked((row * 9) + i);
                }
            }
        }

        if (hasRow2) {
            planMenu.putLocked(19, 20, 21, 22, 23, 24, 25);
            planMenu.putActionSlot(18, "Previous", Material.COAL_BLOCK);
            planMenu.putActionSlot(26, "Next", Material.COAL_BLOCK);
        } else {
            planMenu.putLocked(10, 11, 12, 13, 14, 15, 16);
            planMenu.putActionSlot(9, "Previous", Material.COAL_BLOCK);
            planMenu.putActionSlot(17, "Next", Material.COAL_BLOCK);
        }

    }
    
    public void loadPlans() {
        synchronized(this) {
            if(loadingPlans) {
                return;
            } else {
                loadingPlans = true;
                planMenu.setEnabled(false);
            }
        }
        
        
        final List<StructurePlan> plans = setterCraft.getStructureAPI().getStructurePlanManager().getPlans();
        final Iterator<StructurePlan> planIterator = plans.iterator();

        planMenu.clearItems();

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();

            try {
                // Add item to planmenu
                StructurePlanItem planItem = StructurePlanItem.load(setterCraft.getStructureAPI().getSchematicManager(), plan);
                planMenu.addItem(planItem);
            } catch (IOException | DataException | DocumentException | StructureDataException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        
        planMenu.setEnabled(true);
        loadingPlans = false;
    }
    
}
