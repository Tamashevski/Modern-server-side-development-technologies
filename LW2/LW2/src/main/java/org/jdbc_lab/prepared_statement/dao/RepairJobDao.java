package org.jdbc_lab.prepared_statement.dao;

import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.RepairJob;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RepairJobDao {
    private static final Logger logger = Logger.getLogger(RepairJobDao.class.getName());

    public void clearTable(){
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("DELETE FROM repair_jobs")){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            logger.severe("Ошибка при очистке таблицы repair_jobs: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int getCount() {
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM repair_jobs")) {
            ResultSet resultSet = stmt.executeQuery();

            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            logger.severe("Ошибка при подсчёте ремонтов: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void insert(RepairJob job) {
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("INSERT INTO repair_jobs (car_id, mechanic_id, repair_date, cost) VALUES (?, ?, ?, ?)")) {
            stmt.setLong(1, job.getCarId());
            stmt.setLong(2, job.getMechanicId());
            stmt.setString(3, job.getRepairDate());
            stmt.setInt(4, job.getCost());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка при добавлении ремонта: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<RepairJob> getAll() {
        List<RepairJob> jobs = new ArrayList<>();
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT * FROM repair_jobs ORDER BY id")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RepairJob job = new RepairJob();
                job.setId(rs.getLong("id"));
                job.setCarId(rs.getLong("car_id"));
                job.setMechanicId(rs.getLong("mechanic_id"));
                job.setRepairDate(rs.getString("repair_date"));
                job.setCost(rs.getInt("cost"));
                jobs.add(job);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка при получении списка ремонтов: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return jobs;
    }

    public List<RepairJob> getByMechanicId(Long mechanicId) {
        List<RepairJob> jobs = new ArrayList<>();
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT * FROM repair_jobs WHERE mechanic_id = ?")) {
            stmt.setLong(1, mechanicId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RepairJob job = new RepairJob();
                job.setId(rs.getLong("id"));
                job.setCarId(rs.getLong("car_id"));
                job.setMechanicId(rs.getLong("mechanic_id"));
                job.setRepairDate(rs.getString("repair_date"));
                job.setCost(rs.getInt("cost"));
                jobs.add(job);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка при поиске ремонтов: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return jobs;
    }

    public void updateCost(Long jobId, Integer newCost) {
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("UPDATE repair_jobs SET cost = ? WHERE id = ?")) {
            stmt.setInt(1, newCost);
            stmt.setLong(2, jobId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка при обновлении стоимости: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int deleteOlderThan(String date) {
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("DELETE FROM repair_jobs WHERE repair_date < ?")) {
            stmt.setString(1, date);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка при удалении старых ремонтов: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}