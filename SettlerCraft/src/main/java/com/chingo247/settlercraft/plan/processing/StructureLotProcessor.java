/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.plan.processing;

import com.chingo247.settlercraft.plan.placement.StructureLot;
import com.chingo247.settlercraft.exception.PlanException;
import com.chingo247.settlercraft.commons.logging.SCLogger;
import com.chingo247.settlercraft.commons.util.LogLevel;
import com.chingo247.settlercraft.plan.xml.XMLUtils;
import com.sk89q.worldedit.Vector;
import java.io.File;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class StructureLotProcessor extends PlacementProcessor {
    
    private final SCLogger LOG = SCLogger.getLogger();
    
    /**
     *
     * @param structurePlanFile
     * @param structureLotContainingNode
     */
    public StructureLotProcessor(File structurePlanFile, Node structureLotContainingNode) {
        super(structurePlanFile, structureLotContainingNode);
    }

    @Override
    protected StructureLot compute() {
        long start = System.currentTimeMillis();
        Element pe = (Element) placeableNode;
//        Direction d = XMLUtils.hasDirection(pe) ? XMLUtils.getDirectionFrom(pe) : Direction.NORTH;
        Vector p = XMLUtils.hasPosition(pe) ? XMLUtils.getXYZFrom(pe) : Vector.ZERO;

        int width = XMLUtils.getXPathIntValue(pe, "Width");
        int height = XMLUtils.getXPathIntValue(pe, "Height");
        int length = XMLUtils.getXPathIntValue(pe, "Length");

        if (width < 0) {
            throw new PlanException("Width must be greater than 0");
        }
        if (height < 0) {
            throw new PlanException("height must be greater than 0");
        }
        if (length < 0) {
            throw new PlanException("length must be greater than 0");
        }

        StructureLot structureLot = new StructureLot(p, width, height, length);
//        structureLot.rotate(d);
        LOG.print(LogLevel.DEBUG, structureLot.getWidth() + "x" + structureLot.getHeight() + "x" + structureLot.getLength(), "StructureLot", start);
        return structureLot;
    }
    
}
