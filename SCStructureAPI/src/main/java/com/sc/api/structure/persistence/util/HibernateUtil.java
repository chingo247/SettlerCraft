package com.sc.api.structure.persistence.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {

    private static SessionFactory factory;
    
    static {
        factory = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public static Session getSession() {
        return factory.openSession();
    }
    
   

    public static void addAnnotatedClass(Class clazz) {
        annotatedClasses.add(clazz);
        factory = config.buildSessionFactory();
    }
    
    public static void addAnnotatedClasses(Class... clazzes) {
        for(Class clazz : clazzes) {
            config.addAnnotatedClass(clazz);
        }
        factory = config.configure().buildSessionFactory();
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
