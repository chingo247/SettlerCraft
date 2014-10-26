/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.plan.document;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractDocument<T extends DocumentPluginElement> {
    
    protected Document document;
    protected File documentFile;
    protected final Map<String, DocumentPluginElement> elements = new HashMap<>();

    AbstractDocument(File documentFile) throws DocumentException {
        this.documentFile = documentFile;
        this.document = new SAXReader().read(documentFile);
    }
    
    void putPluginElement(String plugin, Element e) {
        this.elements.put(plugin, new DocumentPluginElement(plugin, this, e));
    }

    void removePluginElement(String plugin, Element e) {
        this.elements.remove(plugin);
    }
    
    public File getDocumentFile() {
        return documentFile;
    }

    public boolean hasContent(Plugin plugin) {
        return elements.get(plugin.getName()) != null;
    }

    public DocumentPluginElement getPluginElement(Plugin plugin) {
        return elements.get(plugin.getName());
    }
    
    public abstract void save();
    
    protected abstract void save(T element);
    
    public String getRelativePath() {
        String path = documentFile.getAbsolutePath();
        String minus = "\\plugins\\SettlerCraft\\";
        path = path.substring(path.indexOf(minus) + minus.length());
        int length = path.length();
        path = path.substring(0, length - 4); // minus XML
        return path;
    }
    
 
    
}
