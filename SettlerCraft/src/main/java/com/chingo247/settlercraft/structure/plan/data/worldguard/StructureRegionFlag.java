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
package com.chingo247.settlercraft.structure.plan.data.worldguard;

import com.chingo247.settlercraft.structure.plan.data.Elements;
import com.chingo247.settlercraft.structure.plan.data.StructurePlanElement;
import com.sk89q.worldguard.protection.flags.Flag;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class StructureRegionFlag <T> implements StructurePlanElement {
    
    private final Flag flag;
    private final T value;

    public StructureRegionFlag(Flag flag, T value) {
        this.flag = flag;
        this.value = value;
    }

    @Override
    public Element asElement() {
        Element element = new BaseElement(Elements.REGIONFLAG);
        
        Element flagName = new BaseElement(Elements.NAME);
        flagName.setText(flag.getName());
        
        Element flagValue = new BaseElement(Elements.VALUE);
        flagValue.setText(String.valueOf(value));
        
        element.add(flagName);
        element.add(flagValue);
        
        return element;
    }
    
}
