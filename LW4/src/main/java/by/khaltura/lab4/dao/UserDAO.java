package by.khaltura.lab4.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import by.khaltura.lab4.config.SessionConfig;
import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.ExecutorProfile;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class UserDAO {

    private final SessionConfig sc = SessionConfig.getInstance();

    // Вход: ищем пользователя по email (Criteria), пароль сверяем с bcrypt-хэшем.
    // Профиль исполнителя LAZY, поэтому инициализируем его, пока сессия открыта
    public AppUser authUser(String email, String password) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<AppUser> criteria = cb.createQuery(AppUser.class);
            Root<AppUser> root = criteria.from(AppUser.class);
            criteria.where(cb.equal(cb.lower(root.<String>get("email")), email.trim().toLowerCase()));
            AppUser user = session.createQuery(criteria).uniqueResult();
            if (user != null) {
                Hibernate.initialize(user.getProfile());
            }
            tx.commit();
            // Одинаковый ответ для неверного email и пароля – не раскрываем, какие адреса есть в системе
            if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
                return null;
            }
            return user;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<AppUser> getUsers() {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<AppUser> users = session.createQuery("FROM AppUser u ORDER BY u.userId", AppUser.class).getResultList();
            tx.commit();
            return users;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public AppUser getUserById(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            AppUser user = session.get(AppUser.class, id);
            tx.commit();
            return user;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // ЗАДАНИЕ (аналог getUserByUsername из методички): найти пользователя по email
    public AppUser getUserByEmail(String email) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            AppUser user = session.createQuery("FROM AppUser u WHERE lower(u.email) = :email", AppUser.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .uniqueResult();
            tx.commit();
            return user;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Регистрация: пароль хранится только в виде bcrypt-хэша
    public AppUser addUser(AppUser user, String rawPassword) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt(10)));
            session.persist(user);
            tx.commit();
            return user;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Подтверждение личности администратором. Объект загружается в сессию,
    // изменение поля отслеживается Hibernate (dirty checking) и сохраняется при commit
    public void setVerified(Integer userId, boolean verified) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            AppUser user = session.get(AppUser.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь не найден");
            }
            user.setVerified(verified);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Смена пароля через HQL-запрос на изменение (параметры передаются через setParameter,
    // а не склеиваются в строку – защита от SQL-инъекций)
    public void updatePassword(Integer userId, String rawPassword) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.createMutationQuery("UPDATE AppUser u SET u.passwordHash = :hash WHERE u.userId = :id")
                    .setParameter("hash", BCrypt.hashpw(rawPassword, BCrypt.gensalt(10)))
                    .setParameter("id", userId)
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public ExecutorProfile getProfile(Integer userId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            ExecutorProfile profile = session.get(ExecutorProfile.class, userId);
            tx.commit();
            return profile;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Создание или обновление профиля исполнителя (связь один к одному через @MapsId)
    public ExecutorProfile saveProfile(Integer userId, String specialization, String skills,
                                       String location, java.math.BigDecimal hourlyRate) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            ExecutorProfile profile = session.get(ExecutorProfile.class, userId);
            if (profile == null) {
                AppUser user = session.getReference(AppUser.class, userId);
                profile = new ExecutorProfile(user, specialization, skills, location, hourlyRate);
                session.persist(profile);
            } else {
                profile.setSpecialization(specialization);
                profile.setSkills(skills);
                profile.setLocation(location);
                profile.setHourlyRate(hourlyRate);
            }
            tx.commit();
            return profile;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}
