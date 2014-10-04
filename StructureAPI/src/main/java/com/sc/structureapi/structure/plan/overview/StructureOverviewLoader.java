/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan.overview;

import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.structure.plan.data.Elements;
import com.sc.structureapi.structure.plan.data.Loader;
import com.sc.structureapi.structure.plan.data.Nodes;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class StructureOverviewLoader extends Loader<StructureOverview>{

    public StructureOverviewLoader() {
        super(Nodes.STRUCTURE_OVERVIEWS_NODE);
    }

    
    @Override
    public List<StructureOverview> load(Element overviewsElement) throws StructureDataException  {
        if (overviewsElement == null) {
            throw new AssertionError("Overviews element was null");
        }

        if (!overviewsElement.getName().equals(Elements.STRUCTURE_OVERVIEWS)) {
            throw new AssertionError("Expected '" + Elements.STRUCTURE_OVERVIEWS + "' element, but got '" + overviewsElement.getName() + "'");
        }
        
        new StructureOverviewValidator().validate(overviewsElement);

        List<StructureOverview> overviews = new ArrayList<>();
        List<Node> overviewNodes = overviewsElement.selectNodes(Elements.STRUCTURE_OVERVIEW);

        for (Node overviewNode : overviewNodes) {
            Node xNode = overviewNode.selectSingleNode(Elements.X);
            Node yNode = overviewNode.selectSingleNode(Elements.Y);
            Node zNode = overviewNode.selectSingleNode(Elements.Z);

            int x = Integer.parseInt(xNode.getText());
            int y = Integer.parseInt(yNode.getText());
            int z = Integer.parseInt(zNode.getText());
            overviews.add(new StructureOverview(x, y, z));
        }

        return overviews;
    }

    
}
