
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



    public PlanDocumentManager(AbstractStructureAPI structureAPI, ExecutorService service) {
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
