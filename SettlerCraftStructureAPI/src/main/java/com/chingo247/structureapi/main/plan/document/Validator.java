/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.structureapi.main.plan.document;

import com.chingo247.structureapi.main.exception.StructureDataException;
import java.io.File;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public abstract class Validator {
    
    private final String xPath;

    public Validator(String xPath) {
        this.xPath = xPath;
    }
    
    
    public abstract void validate(Element e) throws StructureDataException;
    
    public void validate(Document d) throws StructureDataException {
        validate((Element) d.selectSingleNode(xPath));
    }
    
    public void validate(File configXML) throws DocumentException, StructureDataException {
        validate(new SAXReader().read(configXML));
    }
    
}
