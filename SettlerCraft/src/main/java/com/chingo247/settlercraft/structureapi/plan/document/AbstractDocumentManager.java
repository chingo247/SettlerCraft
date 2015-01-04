
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

import com.chingo247.settlercraft.structureapi.structure.old.AbstractStructureAPI;
import com.chingo247.settlercraft.util.document.Elements;
import com.chingo247.settlercraft.util.KeyPool;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 * @param <K>
 * @param <V>
 */
public abstract class AbstractDocumentManager<K,V extends AbstractDocument> {


    private final Map<K, V> documents = Collections.synchronizedMap(new HashMap<K, V>());
    private final KeyPool<K> documentPool;
    protected final AbstractStructureAPI structureAPI;
    protected final ExecutorService executor;

    public AbstractDocumentManager(AbstractStructureAPI structureAPI, ExecutorService service) {
        this.documentPool = new KeyPool<>(service);
        this.structureAPI = structureAPI;
        this.executor = service;
    }

    public List<V> getDocuments() {
        LinkedList<V> plans = new LinkedList<>(documents.values());
        return plans;
    }
    
    public V getDocument(K key) {
        return documents.get(key);
    }

    protected void save(K key, final V document) {
        documentPool.execute(key, new Runnable() {

            @Override
            public void run() {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setExpandEmptyElements(true);
                XMLWriter writer = null;
                try {
                    writer = new XMLWriter(new FileWriter(document.documentFile), format);
                    writer.write(document.document);
                } catch (IOException ex) {
                    Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ex) {
                            Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    protected void save(K key, final DocumentPluginElement element) {
        documentPool.execute(key, new Runnable() {

            @Override
            public void run() {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setExpandEmptyElements(true);
                XMLWriter writer = null;
                try {
                    File d = element.root.documentFile;
                    writer = new XMLWriter(new FileWriter(d), format);
                    writer.write(element.pluginElement.getDocument());
                } catch (IOException ex) {
                    Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ex) {
                            Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }
    
    protected void put(K key, final V document) {
        documents.put(key, document);
    }
 
    public abstract void loadDocuments();

    protected boolean isStructurePlan(Document d) {
        return d.getRootElement().getName().equals(Elements.ROOT);
    }
    
}
