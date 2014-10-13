/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.data.worldguard;

import com.chingo247.settlercraft.structure.data.Elements;
import com.chingo247.settlercraft.structure.data.SettlerCraftElement;
import com.sk89q.worldguard.protection.flags.Flag;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class StructureRegionFlag <T> implements SettlerCraftElement {
    
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
