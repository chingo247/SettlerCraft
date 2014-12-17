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
package com.chingo247.settlercraft.structure.plan.worldguard;

import com.chingo247.settlercraft.structure.exception.StructureDataException;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.document.Validator;
import com.chingo247.settlercraft.util.document.Elements;
import com.chingo247.settlercraft.util.document.Nodes;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class StructureRegionFlagValidator extends Validator {

    public StructureRegionFlagValidator() {
        super(Nodes.WORLDGUARD_FLAGS_NODE);
    }

    @Override
    public void validate(Element en) throws StructureDataException {
        List<Node> nodes = en.selectNodes(Elements.REGIONFLAG);
        Iterator<Node> it = nodes.iterator();

        while (it.hasNext()) {
            Node e = it.next();

            if (e.selectSingleNode("Name") == null) {
                throw new StructureDataException("Missing 'name' node");
            }
            if (e.selectSingleNode("Value") == null) {
                throw new StructureDataException("Missing 'value' node");
            }

            Flag f = DefaultFlag.fuzzyMatchFlag(e.selectSingleNode("Name").getText());
            if (f == null) {
                throw new StructureDataException("Flag '" + e.selectSingleNode("Name").getText() + "' not recognized");
            }

            try {
                Object v = f.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), e.selectSingleNode("Value").getText());

            } catch (InvalidFlagFormat ex) {
                Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
