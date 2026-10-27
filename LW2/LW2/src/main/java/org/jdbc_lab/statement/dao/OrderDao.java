package org.jdbc_lab.statement.dao;

import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OrderDao implements StatementDao<Order> {
    private static final Logger logger = Logger.getLogger(OrderDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM orders");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Order order) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO orders (client_id, service_id, order_date, status) " +
                    "VALUES (" + order.getClientId() + ", " + order.getServiceId() + ", '" +
                    order.getOrderDate() + "', '" + order.getStatus() + "')");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM orders");

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return count;
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM orders");

            while (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getLong("id"));
                order.setClientId(resultSet.getLong("client_id"));
                order.setServiceId(resultSet.getLong("service_id"));
                order.setOrderDate(resultSet.getString("order_date"));
                order.setStatus(resultSet.getString("status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return orders;
    }

    @Override
    public Order getById(Long id) {
        Order order = null;

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM orders WHERE id = " + id);

            if (resultSet.next()) {
                order = new Order();
                order.setId(resultSet.getLong("id"));
                order.setClientId(resultSet.getLong("client_id"));
                order.setServiceId(resultSet.getLong("service_id"));
                order.setOrderDate(resultSet.getString("order_date"));
                order.setStatus(resultSet.getString("status"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return order;
    }

    public void updateRandomOrderStatus(Long clientId, String newStatus) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            String sql = "UPDATE orders SET status = '" + newStatus + "' " +
                    "WHERE id = (SELECT id FROM orders WHERE client_id = " + clientId + " ORDER BY RANDOM() LIMIT 1)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}