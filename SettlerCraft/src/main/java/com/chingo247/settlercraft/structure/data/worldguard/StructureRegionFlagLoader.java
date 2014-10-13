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
package com.chingo247.settlercraft.structure.data.worldguard;

import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.structure.data.Elements;
import com.chingo247.settlercraft.structure.data.Loader;
import com.chingo247.settlercraft.structure.data.Nodes;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import java.util.Iterator;
import java.util.LinkedList;
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
public class StructureRegionFlagLoader extends Loader<StructureRegionFlag> {

    public StructureRegionFlagLoader() {
        super(Nodes.WORLDGUARD_FLAGS_NODE);
    }

    @Override
    public List<StructureRegionFlag> load(Element regionFlagsElement) throws StructureDataException {
        if(!regionFlagsElement.getName().equals(Elements.REGIONFLAGS)) {
            throw new AssertionError("Expected element '"+Elements.REGIONFLAGS+"', but got '"+regionFlagsElement.getName()+"'");
        }
        
        new StructureRegionFlagValidator().validate(regionFlagsElement);
        List<StructureRegionFlag> flags = new LinkedList<>();
        
        Iterator<Node> it = regionFlagsElement.selectNodes(Elements.REGIONFLAG).iterator();
        while(it.hasNext()) {
            Node n = it.next();
            
            
            Flag f = DefaultFlag.fuzzyMatchFlag(n.selectSingleNode(Elements.NAME).getText());
            if (f == null) {
                throw new StructureDataException("Flag '" + n.selectSingleNode(Elements.NAME).getText() + "' not recognized");
            }

            try {
                Object v = f.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), n.selectSingleNode(Elements.VALUE).getText());
//                System.out.println("Flag: " + f.getName() + " Value: " + v);
                StructureRegionFlag regionFlag = new StructureRegionFlag(f, v);
                flags.add(regionFlag);

            } catch (InvalidFlagFormat ex) {
                Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return flags;
    }
    
}
