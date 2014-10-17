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
package com.chingo247.settlercraft.structure.plan.document;

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
 */
public class PlanDocument {

    protected Document document;
    protected File documentFile;
    protected final Map<String, PluginElement> elements = new HashMap<>();

    PlanDocument(File documentFile) throws DocumentException {
        this.documentFile = documentFile;
        this.document = new SAXReader().read(documentFile);
    }
    
    
    
    void putPluginElement(String plugin, Element e) {
        this.elements.put(plugin, new PluginElement(plugin, this, e));
    }

    void removePluginElement(String plugin, Element e) {
        this.elements.remove(plugin, new PluginElement(plugin, this, e));
    }
    
    public File getDocumentFile() {
        return documentFile;
    }

    public boolean hasContent(Plugin plugin) {
        return elements.get(plugin.getName()) != null;
    }

    public PluginElement getPluginElement(Plugin plugin) {
        return elements.get(plugin.getName());
    }
    
    public void save() {
        PlanDocumentManager.getInstance().save(this);
    }
    
    public String getRelativePath() {
        String path = documentFile.getAbsolutePath();
        String minus = "\\plugins\\SettlerCraft\\";
        path = path.substring(path.indexOf(minus) + minus.length());
        int length = path.length();
        path = path.substring(0, length - 4); // minus XML
        return path;
    }
    
    protected void savePluginElement(PluginElement element) {
        Element e = (Element) document.getRootElement().selectSingleNode(element.pluginName);
        e.clearContent();
        e.setContent(element.pluginElement.elements());
        PlanDocumentManager.getInstance().save(element);
    }

    

}
