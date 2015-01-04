
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.plan.document;

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
    protected final Map<String, T> elements = new HashMap<>();

    AbstractDocument(File documentFile) throws DocumentException {
        this.documentFile = documentFile;
        this.document = new SAXReader().read(documentFile);
    }
    
    void putPluginElement(String plugin, T e) {
        this.elements.put(plugin, e);
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

    public T getPluginElement(Plugin plugin) {
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
