package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:data/books.db");
                System.out.println("Database is ready");
            } catch (SQLException e) {
                System.out.println("Database connecting Error: " + e.getMessage());
            }
        }
        return conn;
    }
}
