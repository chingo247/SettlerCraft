/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.plan.document;

import com.chingo247.settlercraft.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.structure.util.Elements;
import com.chingo247.settlercraft.structure.util.KeyPool;
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
