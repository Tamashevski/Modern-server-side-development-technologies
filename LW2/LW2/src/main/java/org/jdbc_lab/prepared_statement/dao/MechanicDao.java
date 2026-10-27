package org.jdbc_lab.prepared_statement.dao;

import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Mechanic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MechanicDao implements PreparedStatementDao<Mechanic> {
    private static final Logger logger = Logger.getLogger(MechanicDao.class.getName());

    @Override
    public void clearTable(){
        try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM mechanics")){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Mechanic mechanic)
    {
        insertAndReturnId(mechanic);
    }

    public Long insertAndReturnId(Mechanic mechanic)
    {
        try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO mechanics (first_name, last_name, experience, specialization, phone) " +
                "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, mechanic.getFirstName());
            stmt.setString(2, mechanic.getLastName());
            stmt.setInt(3, mechanic.getExperience());
            stmt.setString(4, mechanic.getSpecialization());
            stmt.setString(5, mechanic.getPhone());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            return null;
        }
        catch (SQLException e){
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;

        try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT COUNT(*) FROM mechanics")) {
            ResultSet resultSet = stmt.executeQuery();

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
    public List<Mechanic> getAll() {
        List<Mechanic> mechanics = new ArrayList<>();

        try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM mechanics ORDER BY id")) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Mechanic mechanic = new Mechanic();
                mechanic.setId(resultSet.getLong("id"));
                mechanic.setFirstName(resultSet.getString("first_name"));
                mechanic.setLastName(resultSet.getString("last_name"));
                mechanic.setExperience(resultSet.getInt("experience"));
                mechanic.setSpecialization(resultSet.getString("specialization"));
                mechanic.setPhone(resultSet.getString("phone"));
                mechanics.add(mechanic);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mechanics;
    }

    @Override
    public Mechanic getById(Long id) {
        Mechanic mechanic = null;

        try (PreparedStatement stmt = ConnectionManager.getConnection()
                .prepareStatement("SELECT * FROM mechanics WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                mechanic = new Mechanic();
                mechanic.setId(resultSet.getLong("id"));
                mechanic.setFirstName(resultSet.getString("first_name"));
                mechanic.setLastName(resultSet.getString("last_name"));
                mechanic.setExperience(resultSet.getInt("experience"));
                mechanic.setSpecialization(resultSet.getString("specialization"));
                mechanic.setPhone(resultSet.getString("phone"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return mechanic;
    }

}
