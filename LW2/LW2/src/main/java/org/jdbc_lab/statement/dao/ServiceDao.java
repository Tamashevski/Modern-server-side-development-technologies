package org.jdbc_lab.statement.dao;

import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ServiceDao implements StatementDao<Service> {
    private static final Logger logger = Logger.getLogger(ServiceDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM services");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Service service) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO services (title, executor_id, price, category) " +
                    "VALUES ('" + service.getTitle() + "', " + service.getExecutorId() + ", " +
                    service.getPrice() + ", '" + service.getCategory() + "')");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM services");

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
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM services");

            while (resultSet.next()) {
                Service service = new Service();
                service.setId(resultSet.getLong("id"));
                service.setTitle(resultSet.getString("title"));
                service.setExecutorId(resultSet.getLong("executor_id"));
                service.setPrice(resultSet.getInt("price"));
                service.setCategory(resultSet.getString("category"));
                services.add(service);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return services;
    }

    @Override
    public Service getById(Long id) {
        Service service = null;

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM services WHERE id = " + id);

            if (resultSet.next()) {
                service = new Service();
                service.setId(resultSet.getLong("id"));
                service.setTitle(resultSet.getString("title"));
                service.setExecutorId(resultSet.getLong("executor_id"));
                service.setPrice(resultSet.getInt("price"));
                service.setCategory(resultSet.getString("category"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return service;
    }

    public List<Service> getByPriceGreaterThan(int price) {
        List<Service> services = new ArrayList<>();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM services WHERE price > " + price + " ORDER BY price");

            while (resultSet.next()) {
                Service service = new Service();
                service.setId(resultSet.getLong("id"));
                service.setTitle(resultSet.getString("title"));
                service.setExecutorId(resultSet.getLong("executor_id"));
                service.setPrice(resultSet.getInt("price"));
                service.setCategory(resultSet.getString("category"));
                services.add(service);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return services;
    }

    public List<Service> getByCategoryStartingWith(String letter) {
        List<Service> services = new ArrayList<>();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM services WHERE category LIKE '" + letter + "%'");

            while (resultSet.next()) {
                Service service = new Service();
                service.setId(resultSet.getLong("id"));
                service.setTitle(resultSet.getString("title"));
                service.setExecutorId(resultSet.getLong("executor_id"));
                service.setPrice(resultSet.getInt("price"));
                service.setCategory(resultSet.getString("category"));
                services.add(service);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return services;
    }

    public List<String> getServicesCountByCategory() {
        List<String> result = new ArrayList<>();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {

            String sql = "SELECT category, COUNT(*) as count FROM services GROUP BY category";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                int count = resultSet.getInt("count");
                result.add(category + " — " + count + " услуг(и)");
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return result;
    }
}