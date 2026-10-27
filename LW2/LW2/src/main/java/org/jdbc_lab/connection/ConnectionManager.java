package org.jdbc_lab.connection;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConnectionManager {
    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                closeConnection();
                logger.severe("Драйвер для БД не найден: " + e.getMessage());
            }

            final String user = "postgres";
            final String password = "1";
            final String url = "jdbc:postgresql://localhost:5432/jdbc_lab";

            try {
                connection = DriverManager.getConnection(url, user, password);
                logger.info("Соединение с БД установлено успешно!");
            } catch (SQLException e) {
                closeConnection();
                logger.severe("Соединение с БД установить не получилось: " + e.getMessage());
            }

        }
        return connection;
    }

    public static void closeConnection(){
        if (connection != null){
            try {
                connection.close();
                logger.info("Соединение с БД закрыто успешно!");
            }
            catch (SQLException e){
                logger.severe("Соединение с БД закрыть не получилось: " + e.getMessage());
            }
            finally {
                connection = null;
            }
        }
        else {
            logger.info("Обнаружен запрос закрытия уже закрытого соединения с БД!");
        }
    }

}
