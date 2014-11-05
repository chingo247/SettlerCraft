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
package com.chingo247.structureapi.main.plan.worldguard;

import com.chingo247.structureapi.main.plan.IStructurePlanElement;
import com.chingo247.structureapi.main.util.Elements;
import com.sk89q.worldguard.protection.flags.Flag;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 * @param <T>
 */
public class StructureRegionFlag <T> implements IStructurePlanElement {
    
    private final Flag flag;
    private final T value;

    public StructureRegionFlag(Flag flag, T value) {
        this.flag = flag;
        this.value = value;
    }

    public Flag getFlag() {
        return flag;
    }

    public T getValue() {
        return value;
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
