package com.settlercraft.core.util;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {

    private static SessionFactory factory;
    private static Set<Class> annotatedClasses = Sets.newHashSet();
    private static AnnotationConfiguration config;

    public static Session getSession() {
        if (factory == null) {
            config = HibernateUtil.getInitializedConfiguration();
            factory = config.buildSessionFactory();
            annotatedClasses = new HashSet<>();
        }
        return factory.openSession();
    }
    
    public static Session getCurrentSession() {
        return factory.getCurrentSession();
    }

    public static AnnotationConfiguration getInitializedConfiguration() {
        if(config == null) {
             config = new AnnotationConfiguration();
        }
       
        for (Class clazz : annotatedClasses) {
            config.addAnnotatedClass(clazz);
        }
        config.configure();
        return config;
    }

    public static void addAnnotatedClass(Class clazz) {
        annotatedClasses.add(clazz);
        config = getInitializedConfiguration();
        factory = config.buildSessionFactory();
        System.out.println("[SettlerCraft]: registered " + clazz);
    }
    
    public static void addAnnotatedClasses(Class... clazzes) {
        for(Class clazz : clazzes) {
            System.out.println("[SettlerCraft]: registered " + clazz);
            annotatedClasses.add(clazz);
        }
        config = getInitializedConfiguration();
        factory = config.buildSessionFactory();
    }

    public static void shutdown() {
        factory.close();
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
