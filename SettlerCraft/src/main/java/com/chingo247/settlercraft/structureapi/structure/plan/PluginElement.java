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
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.exception.StructurePlanException;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public abstract class PluginElement implements PlanElement {

    private final StructureDocument document;

    private final String pluginName;

    public PluginElement(String pluginName, StructureDocument document) {
        this.document = document;
        this.pluginName = pluginName;
    }
    
    public StructureDocument getDocument() {
        return document;
    }
    
    public abstract void load() throws StructurePlanException;

    public Element getElement() {
        return document.getElement(pluginName);
    }

    public String getPluginName() {
        return pluginName;
    }

    /**
     * Saves the Element
     *
     * @param writeToFile Whether the element should be written to the file
     */
    public void save(boolean writeToFile) {
        document.setElement(this);
        if (writeToFile) {
            document.save();
        }
    }

}
