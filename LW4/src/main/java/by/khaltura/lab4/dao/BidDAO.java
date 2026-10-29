package by.khaltura.lab4.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import by.khaltura.lab4.config.SessionConfig;
import by.khaltura.lab4.model.Bid;

public class BidDAO {

    private final SessionConfig sc = SessionConfig.getInstance();

    public Bid findBidById(Integer id) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Bid bid = session.get(Bid.class, id);
            tx.commit();
            return bid;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    // Отклик исполнителя на конкретную задачу (не больше одного – UNIQUE в таблице)
    public Bid findOwnBid(Integer taskId, Integer executorId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Bid bid = session.createQuery(
                    "FROM Bid b WHERE b.task.taskId = :t AND b.executor.userId = :e", Bid.class)
                    .setParameter("t", taskId)
                    .setParameter("e", executorId)
                    .uniqueResult();
            tx.commit();
            return bid;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public List<Bid> getBidsByExecutor(Integer executorId) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Bid> list = session.createQuery(
                    "FROM Bid b WHERE b.executor.userId = :e ORDER BY b.createdAt DESC, b.bidId DESC", Bid.class)
                    .setParameter("e", executorId).getResultList();
            tx.commit();
            return list;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public Bid addBid(Bid bid) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(bid);
            tx.commit();
            return bid;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    public Bid updateBid(Bid bid) {
        Session session = sc.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Bid merged = session.merge(bid);
            tx.commit();
            return merged;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}
