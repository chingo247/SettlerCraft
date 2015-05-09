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
package com.chingo247.settlercraft.structureapi.menu;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.menuapi.menu.DefaultCategoryMenu;
import com.chingo247.menuapi.menu.slots.CategorySlot;
import com.chingo247.menuapi.menu.slots.SlotFactory;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlanMenuReader {
    
    public CategoryMenu read(File file) throws DocumentException, SettlerCraftException {
        Preconditions.checkArgument(file.exists());

        Document d = new SAXReader().read(file);
        
        CategoryMenu menu = new DefaultCategoryMenu("Buy & Build");
        
        List<Node> rows = d.selectNodes("Menu/SlotRow");
        if (rows.size() > 2) {
            throw new SettlerCraftException("Max rows is 2 for menu.xml");
        }
        
        boolean hasRow2 = false;

        for (int row = 0; row < rows.size(); row++) {
            Node rowNode = rows.get(row);
            List<Node> slotNodes = rowNode.selectNodes("Slot");
            // Slot #1 is reserved for all category
            if (row == 0 && slotNodes.size() > 8) {
                throw new SettlerCraftException(" 'SlotRow#1' has max 8 slots to customize");
            } else if (slotNodes.size() > 9) {
                throw new SettlerCraftException(" 'SlotRow#" + (row + 2) + "' has max 9 slots to customize");
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
                    throw new SettlerCraftException("Missing 'MaterialID' element in 'SlotRow#" + (row + 1) + "' 'Slot#" + (count + 1) + "'");
                }
                if (cat == null) {
                    throw new SettlerCraftException("Missing 'Category' element in 'SlotRow#" + (row + 1) + "' 'Slot#" + (count + 1) + "'");
                }

                int id;
                try {
                    id = Integer.parseInt(mId.getText());
                } catch (NumberFormatException nfe) {
                    throw new SettlerCraftException("Invalid number for 'MaterialID' element in 'SlotRow#" + (row + 1) + "' 'Slot#" + (count + 1) + "'");
                }
                String category = cat.getText();
                if (category.isEmpty()) {
                    Element catEl = (Element) cat;
                    category = catEl.attributeValue("value");
                }
                if (category.trim().isEmpty()) {
                    throw new SettlerCraftException("Empty 'Category' element in 'SlotRow#" + (row + 1) + "' and 'Slot#" + (count + 1) + "'");
                }
                category = category.replaceAll(" AND ", "&");

                
                // Set aliases
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
                            throw new SettlerCraftException("Empty 'Alias' element in  'SlotRow#" + (row + 1) + "' and 'Slot#" + (count + 1) + "' and 'Alias#" + (j + 1) + "'");
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

                CategorySlot categorySlot = SlotFactory.getInstance().createCategorySlot(category, id);
                categorySlot.addAliases(aliases);
                menu.setCategorySlot((row * 9) + slot, categorySlot);

                count++;
            }
            // fill remaining
            if (count < 8 && row == 0) {
                for (int i = count; i < 8; i++) {
                    menu.setLocked(i);
                }

            } else if (row > 0 && count < 9) {
                for (int i = count; i < 9; i++) {
                    menu.setLocked((row * 9) + i);
                }
            }
        }

        if (hasRow2) {
            menu.setLocked(19, 20, 21, 22, 23, 24, 25);
            menu.setActionSlot(18, "Previous", 173); // block of coal
            menu.setActionSlot(26, "Next", 173);
        } else {
            menu.setLocked(10, 11, 12, 13, 14, 15, 16);
            menu.setActionSlot(9, "Previous",173);
            menu.setActionSlot(17, "Next", 173);
        }
        return menu;
    }
    
}
