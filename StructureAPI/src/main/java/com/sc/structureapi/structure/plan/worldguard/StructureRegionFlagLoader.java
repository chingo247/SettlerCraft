/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan.worldguard;

import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.structure.plan.Loader;
import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.plan.data.Elements;
import com.sc.structureapi.structure.plan.data.Nodes;
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
