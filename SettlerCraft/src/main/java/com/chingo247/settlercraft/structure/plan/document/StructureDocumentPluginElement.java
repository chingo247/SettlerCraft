/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.plan.document;

import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public class StructureDocumentPluginElement extends DocumentPluginElement<StructureDocument> {

    public StructureDocumentPluginElement(String pluginName, StructureDocument root, Element pluginElement) {
        super(pluginName, root, pluginElement);
    }

}
