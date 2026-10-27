package org.jdbc_lab.prepared_statement.dao;

import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Car;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CarDao {
    private static final Logger logger = Logger.getLogger(CarDao.class.getName());

    public void clearTable(){
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("DELETE FROM cars")){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            logger.severe("Ошибка при очистке таблицы cars: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int getCount() {
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM cars")) {
            ResultSet resultSet = stmt.executeQuery();

            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            logger.severe("Ошибка при подсчёте машин: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void insert(Car car) {
        insertAndReturnId(car);
    }

    public Long insertAndReturnId(Car car) {
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("INSERT INTO cars (brand, model, year, license_plate) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, car.getBrand());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            logger.severe("Ошибка при добавлении авто: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public List<Car> getByRepairCostGreaterThan(Integer cost) {
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT DISTINCT c.* FROM cars c JOIN repair_jobs r ON c.id = r.car_id "
                        + "WHERE r.cost > ? ORDER BY c.id")) {
            stmt.setInt(1, cost);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setLicensePlate(rs.getString("license_plate"));
                cars.add(car);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка при поиске машин по стоимости ремонта: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return cars;
    }

    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT * FROM cars ORDER BY id")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setLicensePlate(rs.getString("license_plate"));
                cars.add(car);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка при получении авто: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return cars;
    }
}