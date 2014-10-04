/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan.worldguard;

import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.plan.data.Elements;
import com.sc.structureapi.structure.plan.data.Nodes;
import com.sc.structureapi.structure.plan.Validator;
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
                System.out.println("Flag: " + f.getName() + " Value: " + v);

            } catch (InvalidFlagFormat ex) {
                Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
