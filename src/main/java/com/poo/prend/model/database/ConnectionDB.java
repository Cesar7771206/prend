package com.poo.prend.model.database;

import java.sql.*; 
import io.github.cdimascio.dotenv.Dotenv;


public class ConnectionDB {
    private static Connection connection; 
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {

            Dotenv dotenv = Dotenv.load(); 

            String URL = dotenv.get("ERP_DB_URL"); // Variables de entorno, se encuentran en el archivo .env
            String USER = dotenv.get("ERP_DB_USER"); 
            String PASSWORD = dotenv.get("ERP_DB_PASSWORD");
            connection = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
    
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    
    
}
