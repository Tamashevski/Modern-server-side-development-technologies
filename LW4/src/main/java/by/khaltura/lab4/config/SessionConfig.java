package by.khaltura.lab4.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Bid;
import by.khaltura.lab4.model.Category;
import by.khaltura.lab4.model.ExecutorProfile;
import by.khaltura.lab4.model.Task;

// Одна фабрика сессий на всё приложение (синглтон): создавать её дорого,
// а все DAO работают с одним и тем же набором классов-моделей
public class SessionConfig {

    private static SessionConfig instance;
    private final SessionFactory sessionFactory;

    private SessionConfig() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(AppUser.class)
                .addAnnotatedClass(ExecutorProfile.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(Task.class)
                .addAnnotatedClass(Bid.class)
                .configure()                       // читает hibernate.cfg.xml
                .buildSessionFactory();
    }

    public static synchronized SessionConfig getInstance() {
        if (instance == null) {
            instance = new SessionConfig();
        }
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutdown() {
        sessionFactory.close();
    }
}
