package by.khaltura.lab4.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import by.khaltura.lab4.config.SessionConfig;
import by.khaltura.lab4.model.Bid;
import by.khaltura.lab4.model.BidStatus;
import by.khaltura.lab4.model.Category;
import by.khaltura.lab4.model.Task;
import by.khaltura.lab4.model.TaskStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TaskDAO {

    private final SessionConfig sc = SessionConfig.getInstance();

    public record CategoryStats(String category, long tasks, BigDecimal avgBudget,
                                BigDecimal minBudget, BigDecimal maxBudget) {
    }

    public Task findTaskById(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = session.get(Task.class, id);
            tx.commit();
            return task;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public Task getTaskWithBids(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = session.get(Task.class, id);
            if (task != null) {
                Hibernate.initialize(task.getBids());
            }
            tx.commit();
            return task;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public Task addTask(Task task) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(task);
            tx.commit();
            return task;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public Task updateTask(Task task) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Task merged = session.merge(task);
            tx.commit();
            return merged;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public Task moveTaskToCategory(Integer taskId, Category category) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                throw new IllegalArgumentException("Задача не найдена");
            }
            task.setCategory(session.get(Category.class, category.getCategoryId()));
            tx.commit();
            return task;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public void deleteTask(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = session.get(Task.class, id);
            if (task == null) {
                throw new IllegalArgumentException("Задача не найдена");
            }
            session.remove(task);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<Task> getTasksByCustomer(Integer customerId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Task> list = session.createQuery(
                    "FROM Task t WHERE t.customer.userId = :id ORDER BY t.createdAt DESC, t.taskId DESC", Task.class)
                    .setParameter("id", customerId).getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Task> list = session.createQuery(
                    "FROM Task t WHERE t.status = :s ORDER BY t.createdAt, t.taskId", Task.class)
                    .setParameter("s", status).getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<Task> getFeed() {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Task> list = session.createQuery(
                    "FROM Task t WHERE t.status = :s ORDER BY t.createdAt DESC, t.taskId DESC", Task.class)
                    .setParameter("s", TaskStatus.PUBLISHED).getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public void acceptBid(Integer bidId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Bid bid = session.get(Bid.class, bidId);
            Task task = bid.getTask();
            bid.setStatus(BidStatus.ACCEPTED);
            session.createMutationQuery(
                    "UPDATE Bid b SET b.status = :rejected WHERE b.task = :task AND b.bidId <> :id AND b.status = :pending")
                    .setParameter("rejected", BidStatus.REJECTED)
                    .setParameter("task", task)
                    .setParameter("id", bidId)
                    .setParameter("pending", BidStatus.PENDING)
                    .executeUpdate();
            task.setStatus(TaskStatus.IN_PROGRESS);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public void cancelTask(Integer taskId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = session.get(Task.class, taskId);
            task.setStatus(TaskStatus.CANCELLED);
            session.createMutationQuery("UPDATE Bid b SET b.status = :r WHERE b.task = :t AND b.status = :p")
                    .setParameter("r", BidStatus.REJECTED)
                    .setParameter("t", task)
                    .setParameter("p", BidStatus.PENDING)
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<Task> findPublishedByBudget(BigDecimal min, BigDecimal max,
                                            Integer categoryId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            // LEFT JOIN к родителю: у корневых категорий родителя нет, их задачи не должны пропасть
            String hql = "SELECT t FROM Task t JOIN t.category c LEFT JOIN c.parent p"
                    + " WHERE t.status = :status AND t.budget BETWEEN :min AND :max"
                    + (categoryId != null
                        ? " AND (c.categoryId = :cat OR p.categoryId = :cat)" : "")
                    + " ORDER BY t.budget";
            var query = session.createQuery(hql, Task.class)
                    .setParameter("status", TaskStatus.PUBLISHED)
                    .setParameter("min", min)
                    .setParameter("max", max);
            if (categoryId != null) {
                query.setParameter("cat", categoryId);
            }
            List<Task> list = query.getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<Task> findByCityAndStatus(String city, TaskStatus status) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Task> criteria = cb.createQuery(Task.class);
            Root<Task> root = criteria.from(Task.class);
            List<Predicate> where = new ArrayList<>();
            String pattern = "%" + city.trim().toLowerCase() + "%";
            where.add(cb.like(cb.lower(root.<String>get("location")), pattern));
            if (status != null) {
                where.add(cb.equal(root.get("status"), status));
            }
            criteria.select(root).where(cb.and(where.toArray(new Predicate[0])))
                    .orderBy(cb.desc(root.get("createdAt")));
            List<Task> list = session.createQuery(criteria).getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<CategoryStats> categoryStats() {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Object[]> rows = session.createQuery(
                    "SELECT c.name, COUNT(t), AVG(t.budget), "
                            + "MIN(t.budget), MAX(t.budget) "
                            + "FROM Task t JOIN t.category c "
                            + "WHERE t.status NOT IN (:draft, :cancelled) "
                            + "GROUP BY c.name ORDER BY COUNT(t) DESC, c.name",
                    Object[].class)
                    .setParameter("draft", TaskStatus.DRAFT)
                    .setParameter("cancelled", TaskStatus.CANCELLED)
                    .getResultList();
            tx.commit();
            List<CategoryStats> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new CategoryStats((String) r[0], (Long) r[1],
                        BigDecimal.valueOf((Double) r[2])
                                .setScale(2, RoundingMode.HALF_UP),
                        (BigDecimal) r[3], (BigDecimal) r[4]));
            }
            return result;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}
