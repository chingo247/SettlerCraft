/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.plan.document;

import com.chingo247.settlercraft.structureapi.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structureapi.structure.QStructure;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureDocumentManager extends AbstractDocumentManager<Long, StructureDocument> {


    public StructureDocumentManager(AbstractStructureAPI structureAPI, ExecutorService executorService) {
        super(structureAPI, executorService);
    }

    public void save(StructureDocument d) {
        save(d.getStructure().getId(), d);
    }
    
    public void save(StructureDocumentPluginElement pluginElement) {
        save(pluginElement.root.getStructure().getId(), pluginElement);
    }
    
    public void register(Structure structure) {
        if (getDocument(structure.getId()) == null) {
            StructureDocument d;
            try {
                d = new StructureDocument(this, structure, structureAPI.getStructurePlanFile(structure));
                List<Element> elements = d.document.getRootElement().elements();

                // Form plan document
                for (Element pluginElement : elements) {
                    d.putPluginElement(pluginElement.getName(), new StructureDocumentPluginElement(pluginElement.getName(), d, pluginElement));
                }

                put(structure.getId(), d);
                structureAPI.getStructurePlanManager().updatePlan(d);
            } catch (DocumentException ex) {
                Logger.getLogger(StructureDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Loads all structureDocuments multi-threaded. Number of cores used is defined by
     * the number of cores available using Runtime.getRuntime.availableProcessors()
     */
    @Override
    public synchronized void loadDocuments() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        session.close();

        final List<Future> tasks = new LinkedList<>();

        for (final Structure structure : structures) {
            final File structureDocFile = structureAPI.getStructurePlanFile(structure);
            tasks.add(executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        SAXReader reader = new SAXReader();
                        Document d = reader.read(structureDocFile);
                        // If the RootElement is not 'StructurePlan', skip it
                        if (!isStructurePlan(d)) {
                            return;
                        }
                        List<Element> elements = d.getRootElement().elements();

                        // Form plan document
                        StructureDocument structureDocument = new StructureDocument(StructureDocumentManager.this, structure, structureDocFile);
                        for (Element pluginElement : elements) {
                            structureDocument.putPluginElement(pluginElement.getName(), new StructureDocumentPluginElement(pluginElement.getName(), structureDocument, pluginElement));
                        }
                        // Save the document
                        put(structure.getId(), structureDocument);
                    } catch (DocumentException ex) {
                        Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }));
        }

        // Block until all tasks are done
        for (Future f : tasks) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
                for (Future fu : tasks) {
                    fu.cancel(true);
                }
            }
        }
       
    }

    

   

    
    
    
}
