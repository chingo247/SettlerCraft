/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structureapi.structure.plan.xml.handlers;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.structure.plan.placement.DirectionalPlacement;
import com.chingo247.structureapi.structure.plan.placement.Placement;
import com.chingo247.structureapi.structure.plan.xml.PlacementXMLConstants;
import com.sk89q.worldedit.Vector;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractPlacementXMLHandler<T extends Placement> implements PlacementXMLHandler<T> {

    @Override
    public Element handle(T placement) {
        Element placementRoot = new BaseElement(PlacementXMLConstants.PLACEMENT_ROOT_ELEMENT);
//        d.add(placementRoot);

        Vector v = placement.getPosition();
        if (!v.equals(Vector.ZERO)) { // Not equal to default
            Element xElement = new BaseElement(PlacementXMLConstants.PLACEMENT_X_ELEMENT);
            Element yElement = new BaseElement(PlacementXMLConstants.PLACEMENT_Y_ELEMENT);
            Element zElement = new BaseElement(PlacementXMLConstants.PLACEMENT_Z_ELEMENT);

            xElement.setText(String.valueOf(v.getBlockX()));
            yElement.setText(String.valueOf(v.getBlockY()));
            zElement.setText(String.valueOf(v.getBlockZ()));

            placementRoot.add(xElement);
            placementRoot.add(yElement);
            placementRoot.add(zElement);
        }

        if (placement instanceof DirectionalPlacement) {
            Direction direction = ((DirectionalPlacement) placement).getDirection();
            Element directionElement = new BaseElement(PlacementXMLConstants.PLACEMENT_DIRECTION_ELEMENT);
            directionElement.setText(direction.name());
            placementRoot.add(directionElement);
        }
        
        Element typeElement = new BaseElement(PlacementXMLConstants.PLACEMENT_TYPE_ELEMENT);
        typeElement.setText(placement.getTypeName());
        placementRoot.add(typeElement);
        
        
        return placementRoot;
    }

}
