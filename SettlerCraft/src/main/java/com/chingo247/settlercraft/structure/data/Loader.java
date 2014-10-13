/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.structure.data;

import com.chingo247.settlercraft.exception.StructureDataException;
import java.io.File;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 * @param <T> The type of the objects that will be loaded
 */
public abstract class Loader<T> {
    
    protected final String xPath;
    
    public Loader(String xPath) {
        this.xPath = xPath;
    }
    
    public abstract List<T> load(Element e) throws StructureDataException;
    
    public List<T> load(Document d) throws StructureDataException {
        return load((Element) d.selectSingleNode(xPath));
    }
    
    public List<T> load(File configXML) throws DocumentException, StructureDataException {
        return load(new SAXReader().read(configXML));
    }
    
}
