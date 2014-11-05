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
package com.chingo247.structureapi.main.plan.document;

import com.chingo247.structureapi.main.StructureAPI;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class PlanDocumentManager extends AbstractDocumentManager<String, PlanDocument>{



    public PlanDocumentManager(StructureAPI structureAPI, ExecutorService service) {
        super(structureAPI, service);
    }

    public void save(PlanDocument d) {
        save(d.getRelativePath(), d);
    }

    public void save(PlanDocumentPluginElement element) {
        save(element.root.getRelativePath(), element);
    }
    
    /**
     * Loads all planDocuments & structureDocuments Multi-Core. Number of cores used is defined by
     * the number of cores available using Runtime.getRuntime.availableProcessors()
     */
    @Override
    public synchronized void loadDocuments() {
        
        // Go throug all XML files inside the 'Plans' folder
        Iterator<File> it = FileUtils.iterateFiles(structureAPI.getPlanDataFolder(), new String[]{"xml"}, true);
        final List<Future> tasks = new LinkedList<>();
        while (it.hasNext()) {
            final File planDocFile = it.next();
            tasks.add(executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        SAXReader reader = new SAXReader();
                        Document d = reader.read(planDocFile);
                        // If the RootElement is not 'StructurePlan', skip it
                        if (!isStructurePlan(d)) {
                            return;
                        }
                        List<Element> elements = d.getRootElement().elements();

                        // Form plan document
                        PlanDocument planDocument = new PlanDocument(PlanDocumentManager.this, planDocFile);
                        for (Element pluginElement : elements) {
                            planDocument.putPluginElement(pluginElement.getName(), new PlanDocumentPluginElement(pluginElement.getName(), planDocument, pluginElement));
                        }
                        // Save the document
                        PlanDocumentManager.this.put(planDocument.getRelativePath(), planDocument);
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
