package com.settlercraft.core.util;

import com.google.common.collect.Sets;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory factory;
    private static SessionFactory versionFactory;
    private static Set<Class> annotatedClasses = Sets.newHashSet();
    private static AnnotationConfiguration config = new AnnotationConfiguration();
    private static AnnotationConfiguration versionConfig = new  AnnotationConfiguration();

    public static Session getSession() {
        if (factory == null) {
            
            initializeConfiguration(config);
            factory = config.configure("hibernate.cfg.xml").buildSessionFactory();
        }
        return factory.openSession();
    }
    
    public static Session getVersionDBSession() {
        if(versionFactory == null) {
            initializeConfiguration(versionConfig);
            versionFactory = versionConfig.configure("version_history.cfg.xml").buildSessionFactory();
        }
        return versionFactory.openSession();
    }
    

    private static Configuration initializeConfiguration(final AnnotationConfiguration configuration) {
        for (Class clazz : annotatedClasses) {
            configuration.addAnnotatedClass(clazz);
        }
        return configuration.configure();
    }

    public static void addAnnotatedClass(Class clazz) {
        annotatedClasses.add(clazz);
        factory = config.buildSessionFactory();
        versionFactory = versionConfig.buildSessionFactory();
        System.out.println("[SettlerCraft]: registered " + clazz);
    }
    
    public static void addAnnotatedClasses(Class... clazzes) {
        for(Class clazz : clazzes) {
            System.out.println("[SettlerCraft]: registered " + clazz);
            annotatedClasses.add(clazz);
        }
        initializeConfiguration(config);
        initializeConfiguration(versionConfig);
        factory = config.configure("hibernate.cfg.xml").buildSessionFactory();
        versionFactory = versionConfig.configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public static void shutdown() {
        factory.close();
        versionFactory.close();
    }

//    private static List<Class> getAnnotatedClasses() {
//        System.out.println("getting annotated clazzes");
//        List<Class> clazzes = new LinkedList();
//        ClassPathScanningCandidateComponentProvider scanner
//                = new ClassPathScanningCandidateComponentProvider(false);
//        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
//
//        // only register classes within "com.settlercraft.model" package
//        for (BeanDefinition bd : scanner.findCandidateComponents(null)) {
//            String name = bd.getBeanClassName();
//            System.out.println(name);
//            try {
//                clazzes.add(Class.forName(name));
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
//            }
//        }
//        System.out.println(clazzes.size());
//        return clazzes;
//    }
}
