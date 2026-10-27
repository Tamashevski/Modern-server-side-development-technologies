package org.jdbc_lab.statement.dao;

import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Executor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ExecutorDao implements StatementDao<Executor> {
    private static final Logger logger = Logger.getLogger(ExecutorDao.class.getName());

    @Override
    public void clearTable(){
        try (Statement stmt = ConnectionManager.getConnection().createStatement()){
                stmt.executeUpdate("DELETE FROM executors");
            }
        catch (SQLException e){
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void insert(Executor executor) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO executors (first_name, last_name, specialization, rating) " +
                    "VALUES ('" + executor.getFirstName() + "', '" + executor.getLastName() + "', '" +
                    executor.getSpecialization() + "', " + executor.getRating() + ")");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount(){
        int count = 0;

        try (Statement stmt = ConnectionManager.getConnection().createStatement()){
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM executors");
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
    public List<Executor> getAll() {
        List<Executor> executors = new ArrayList<>();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM executors");

            while (resultSet.next()) {
                Executor executor = new Executor();
                executor.setId(resultSet.getLong("id"));
                executor.setFirstName(resultSet.getString("first_name"));
                executor.setLastName(resultSet.getString("last_name"));
                executor.setSpecialization(resultSet.getString("specialization"));
                executor.setRating(resultSet.getInt("rating"));
                executors.add(executor);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return executors;
    }

    @Override
    public Executor getById(Long id) {
        Executor executor = null;

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM executors WHERE id = " + id);

            if (resultSet.next()) {
                executor = new Executor();
                executor.setId(resultSet.getLong("id"));
                executor.setFirstName(resultSet.getString("first_name"));
                executor.setLastName(resultSet.getString("last_name"));
                executor.setSpecialization(resultSet.getString("specialization"));
                executor.setRating(resultSet.getInt("rating"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return executor;
    }

    public List<Executor> getAllSortedBySurname(){
       List<Executor> executors = new ArrayList<>();

       try (Statement stmt = ConnectionManager.getConnection().createStatement()){
           ResultSet resultSet = stmt.executeQuery("SELECT * FROM executors ORDER BY last_name, first_name");

           while (resultSet.next()){
               Executor executor = new Executor();
               executor.setId(resultSet.getLong("id"));
               executor.setFirstName(resultSet.getString("first_name"));
               executor.setLastName(resultSet.getString("last_name"));
               executor.setSpecialization(resultSet.getString("specialization"));
               executor.setRating(resultSet.getInt("rating"));
               executors.add(executor);
           }
       }
       catch (SQLException e) {
           logger.severe("Ошибка: " + e.getMessage());
           throw new RuntimeException(e);
       }

       return executors;
    }
}
