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

import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.entities.structure.QStructure;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.plan.data.Elements;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class PlanDocumentManager {

    private final SettlerCraft PLUGIN = SettlerCraft.getInstance();
    private final File PLANS_FOLDER = new File(PLUGIN.getDataFolder(), "Plans");

//    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Map<String, PlanDocument> planDocuments = Collections.synchronizedMap(new HashMap<String, PlanDocument>());
    private final Map<Long, StructureDocument> structureDocuments = Collections.synchronizedMap(new HashMap<Long, StructureDocument>());

    private final Lock planDocumentsLock = new ReentrantLock();
    private final Lock structureDocumentsLock = new ReentrantLock();

    private ExecutorService executor;

    private final ExecutorService planService = Executors.newSingleThreadExecutor();

    private static PlanDocumentManager instance;

    private PlanDocumentManager() {
    }

    public static PlanDocumentManager getInstance() {
        if (instance == null) {
            instance = new PlanDocumentManager();
        }
        return instance;
    }

    public Map<String, PlanDocument> getPlanDocuments() {
        try {
            planDocumentsLock.lock();
            Map<String, PlanDocument> plans = new HashMap<>(planDocuments);
            return plans;
        } finally {
            planDocumentsLock.unlock();
        }
    }

    public Map<Long, StructureDocument> getStructureDocuments() {
        try {
            structureDocumentsLock.lock();
            Map<Long, StructureDocument> plans = new HashMap<>(structureDocuments);
            return plans;
        } finally {
            structureDocumentsLock.unlock();
        }
    }

    public void register(Structure structure) {
        if (structureDocuments.get(structure.getId()) == null) {
            StructureDocument d;
            try {
                d = new StructureDocument(structure, structure.getConfig());

                List<Element> elements = d.document.getRootElement().elements();

                        // Form plan document
                for (Element pluginElement : elements) {
                    d.putPluginElement(pluginElement.getName(), pluginElement);
                }

                structureDocuments.put(structure.getId(), d);
            } catch (DocumentException ex) {
                Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void save(final PlanDocument d) {
        planService.execute(new Runnable() {

            @Override
            public void run() {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setExpandEmptyElements(true);
                XMLWriter writer = null;
                try {
                    writer = new XMLWriter(new FileWriter(d.documentFile), format);
                    writer.write(d.document);
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

    void save(final PluginElement element) {
        planService.execute(new Runnable() {

            @Override
            public void run() {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setExpandEmptyElements(true);
                XMLWriter writer = null;
                try {
                    File d = element.root.documentFile;
                    writer = new XMLWriter(new FileWriter(d), format);
                    writer.write(element.pluginElement);
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

//    
//    void loadDocument(final PlanDocument d) {
//        planService.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                d.elements.clear();
//                List<Element> eles = d.document.getRootElement().elements();
//
//                // Form plan document
//                for (Element pluginElement : eles) {
//                    d.elements.put(pluginElement.getName(), new PluginElement(pluginElement.getName(), d, pluginElement));
//                }
//            }
//        });
//    }
//    
//    void loadElement(final PluginElement element) {
//        planService.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                SAXReader reader = new SAXReader();
//                try {
//                    Document d = reader.read(element.root.documentFile);
//                    Element newElement = (Element) d.selectSingleNode(element.pluginName);
//                    Element pluginElement = element.pluginElement;
//                    
//                    pluginElement.clearContent();
//                    pluginElement.setContent(newElement.elements());
//                } catch (DocumentException ex) {
//                    Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//    }
    private String getRelativePath(File config) {
        String path = config.getAbsolutePath();
        String minus = "\\plugins\\" + PLUGIN.getName() + "\\";
        path = path.substring(path.indexOf(minus) + minus.length());
        int length = path.length();
        path = path.substring(0, length - 4); // minus XML
        return path;
    }

    /**
     * Loads all planDocuments & structureDocuments Multi-Core. Number of cores used is defined by
     * the number of cores available using Runtime.getRuntime.availableProcessors()
     */
    public void load() {
        try {
            planDocumentsLock.lock();
            structureDocumentsLock.lock();
            // Loaded within on enable
            loadPlanDocuments();
            loadStructureDocuments();
        } finally {
            planDocumentsLock.unlock();
            structureDocumentsLock.unlock();
        }

    }

    /**
     * Loads all StructurePlans under the 'Plans' folder
     */
    private void loadPlanDocuments() {
        shutdown();

        // Go throug all XML files inside the 'Plans' folder
        Iterator<File> it = FileUtils.iterateFiles(PLANS_FOLDER, new String[]{"xml"}, true);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
                        PlanDocument planDocument = new PlanDocument(planDocFile);
                        for (Element pluginElement : elements) {
                            planDocument.putPluginElement(pluginElement.getName(), pluginElement);
                        }
                        // Save the document
                        planDocuments.put(getRelativePath(planDocFile), planDocument);
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
        try {
            executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Loads all StructurePlans under the 'Structures' folder Only loads plans of Structures that
     * are not removed
     */
    private void loadStructureDocuments() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.state.ne(Structure.State.REMOVED)).list(qs);
        session.close();

        shutdown();

        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final List<Future> tasks = new LinkedList<>();

        for (final Structure structure : structures) {
            final File structureDocFile = structure.getConfig();
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
                        StructureDocument structureDocument = new StructureDocument(structure, structureDocFile);
                        for (Element pluginElement : elements) {
                            structureDocument.putPluginElement(pluginElement.getName(), pluginElement);
                        }
                        // Save the document
                        structureDocuments.put(structure.getId(), structureDocument);
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
        try {
            executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isStructurePlan(Document d) {
        return d.getRootElement().getName().equals(Elements.ROOT);
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
