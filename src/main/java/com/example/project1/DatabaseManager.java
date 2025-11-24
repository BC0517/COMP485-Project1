// DatabaseManager.java
package com.example.project1;

import java.io.File;
import java.sql.*;

// Handles SQLite database initialization and connections
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:database/chatapp.db";

    // Ensure the database directory exists
    static {
        File dbDir = new File("database");
        if (!dbDir.exists()) dbDir.mkdirs();
        initializeDatabase();
    }

    // Get a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Initialize tables
    public static void initializeDatabase() {
        String usersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
        """;

        String messagesTable = """
            CREATE TABLE IF NOT EXISTS messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sender TEXT NOT NULL,
                receiver TEXT NOT NULL,
                content TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(messagesTable);
            System.out.println("SQLite database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing SQLite DB: " + e.getMessage());
        }
    }
}
