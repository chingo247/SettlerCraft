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
package com.chingo247.structureapi.plan.overview;

import com.chingo247.structureapi.exception.StructureDataException;
import com.chingo247.structureapi.plan.document.Loader;
import com.chingo247.structureapi.util.Elements;
import com.chingo247.structureapi.util.Nodes;
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
