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
package com.chingo247.settlercraft.bukkit.menu;

import com.chingo247.menu.CategoryMenu;
import com.chingo247.menu.MenuAPI;
import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.bukkit.SettlerCraftPlugin;
import com.chingo247.settlercraft.util.FileUtil;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.exception.StructureDataException;
import com.chingo247.structureapi.plan.StructurePlan;
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
    private CategoryMenu planMenu;
    private final Logger LOGGER = Logger.getLogger(PlanMenuManager.class.getName());
    private boolean loadingPlans = false;
    private final SettlerCraft settlerCraft;

    public PlanMenuManager(SettlerCraft settlerCraft) {
        this.settlerCraft = settlerCraft;
    }
    
    

    public CategoryMenu getPlanMenu() {
        return planMenu;
    }

    /**
     * Loads the PlanMenu from the menu.xml. If menu.xml doesn't exist, the
     * menu.xml from the jar will be written to the FileSystem.
     *
     * @throws DocumentException When XML is invalid
     * @throws StructureAPIException When XML contains invalid data
     */
    public final void initialize() throws DocumentException, StructureAPIException {
        File file = new File(settlerCraft.getWorkingDirectory(), "menu.xml");
        if (!file.exists()) {
            InputStream input = PlanMenuManager.class.getClassLoader().getResourceAsStream(RESOURCE_FOLDER + "/menu.xml");
            FileUtil.write(input, file);
        }

        Document d = new SAXReader().read(file);
        List<Node> rows = d.selectNodes("Menu/SlotRow");
        if (rows.size() > 2) {
            throw new StructureAPIException("Max rows is 2 for menu.xml");
        }

        planMenu = MenuAPI.createMenu(SettlerCraftPlugin.getInstance(), PLANSHOP_NAME, 54);
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

                planMenu.putCategorySlot((row * 9) + slot, category, Material.getMaterial(id), aliases);

                count++;
            }
            // fill remaining
            if (count < 8 && row == 0) {
                for (int i = count; i < 8; i++) {
                    planMenu.putLocked(i);
                }

            } else if (row > 0 && count < 9) {
                for (int i = count; i < 9; i++) {
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

    public void load() {
        synchronized (this) {
            if (loadingPlans) {
                return;
            } else {
                loadingPlans = true;
                planMenu.setEnabled(false);
            }
        }

        StructureAPI structureAPI = settlerCraft.getStructureAPI();
        final List<StructurePlan> plans = structureAPI.getStructurePlans();
        final Iterator<StructurePlan> planIterator = plans.iterator();

        planMenu.clearItems();

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();

            try {
                // Add item to planmenu
                StructurePlanItem planItem = StructurePlanItem.createItemFromPlan(plan);
                planMenu.addItem(planItem);
            } catch (IOException | DataException | DocumentException | StructureDataException ex) {
                LOGGER.error(ex.getMessage());
            }
        }

        planMenu.setEnabled(true);
        loadingPlans = false;
    }

}
