package com.sc.api.structure.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {

    private static SessionFactory factory;

    private static final AnnotationConfiguration config = new AnnotationConfiguration();

//    private static final String PATH = "plugins/SCStructureAPI/DataBase/hibernate.cfg.xml";
//    private static final String PATH = "com/sc/api/structure/persistence/util/hibernate.cfg.xml";
    static {
//        File file = new File(PATH);
//        File f = new File(".");
        factory = config.configure().buildSessionFactory();
    }

    public static Session getSession() {
        return factory.openSession();
    }

    public static void addAnnotatedClass(Class clazz) {
        config.addAnnotatedClass(clazz);
        factory = config.buildSessionFactory();
    }

    public static void addAnnotatedClasses(Class... clazzes) {
        for (Class clazz : clazzes) {
            config.addAnnotatedClass(clazz);
        }
//        File file = new File(PATH);
        factory = config.configure().buildSessionFactory();
    }

    public static void shutdown() {
        factory.close();
    }

//    private static List<Class> getAnnotatedClasses() {
//        List<Class> clazzes = new LinkedList();
//        ClassPathScanningCandidateComponentProvider scanner
//                = new ClassPathScanningCandidateComponentProvider(false);
//        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
//
//        // only register classes within "com.settlercraft.model" package
//        for (BeanDefinition bd : scanner.findCandidateComponents(null)) {
//            String name = bd.getBeanClassName();
//            try {
//                clazzes.add(Class.forName(name));
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
//            }
//        }
//        return clazzes;
//    }
}
