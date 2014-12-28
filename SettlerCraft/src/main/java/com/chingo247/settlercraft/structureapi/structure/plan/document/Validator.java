/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.structureapi.structure.plan.document;

import com.chingo247.settlercraft.structureapi.exception.PlanException;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public abstract class Validator {
    
    public abstract void validate(Element element) throws PlanException;
    
    public void validate(Node node) throws PlanException {
        validate((Element) node);
    }
    
}
