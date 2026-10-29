package by.khaltura.lab4.dao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import by.khaltura.lab4.config.SessionConfig;
import by.khaltura.lab4.model.Category;

public class CategoryDAO {

    private final SessionConfig sc = SessionConfig.getInstance();

    public List<Category> getCategories() {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            // parent – LAZY, подгружаем сразу, чтобы дерево категорий строилось вне сессии
            List<Category> list = session.createQuery(
                    "FROM Category c LEFT JOIN FETCH c.parent ORDER BY c.categoryId", Category.class)
                    .getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Решение проблемы n+1: категории вместе с задачами одним запросом (LEFT JOIN FETCH).
    // Без FETCH пришлось бы выполнить 1 запрос за категориями + по запросу на задачи каждой категории.
    // JOIN дублирует категорию для каждой её задачи, поэтому складываем результат в Set
    public Set<Category> getCategoriesWithTasks() {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Category> rows = session.createQuery(
                    // t.customer тоже подтягиваем сразу: иначе Hibernate догрузит заказчиков отдельными запросами
                    "FROM Category c LEFT JOIN FETCH c.tasks t LEFT JOIN FETCH t.customer ORDER BY c.categoryId",
                    Category.class)
                    .getResultList();
            tx.commit();
            return new LinkedHashSet<>(rows);
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // ЗАДАНИЕ (аналог findDepartmentByID)
    public Category findCategoryById(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Category category = session.get(Category.class, id);
            tx.commit();
            return category;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // ЗАДАНИЕ (аналог addDepartament)
    public Category addCategory(Category category) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(category);
            tx.commit();
            return category;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Удаление категории. Задачи с ней не удаляются (каскада нет), поэтому удалять
    // можно только пустую категорию. У дочерних категорий родитель сбрасывается
    public void deleteCategory(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Category category = session.get(Category.class, id);
            if (category == null) {
                throw new IllegalArgumentException("Категория не найдена");
            }
            Long tasks = session.createQuery("SELECT COUNT(t) FROM Task t WHERE t.category = :c", Long.class)
                    .setParameter("c", category).getSingleResult();
            if (tasks > 0) {
                throw new IllegalStateException("В категории есть задачи (" + tasks + "), удалить её нельзя");
            }
            session.createMutationQuery("UPDATE Category c SET c.parent = NULL WHERE c.parent = :c")
                    .setParameter("c", category).executeUpdate();
            session.remove(category);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}
