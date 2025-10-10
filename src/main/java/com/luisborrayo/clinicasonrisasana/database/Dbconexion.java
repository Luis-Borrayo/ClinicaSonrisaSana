package com.luisborrayo.clinicasonrisasana.database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Dbconexion {
    private static final String URL = "jdbc:postgresql://localhost:5433/ClinicaSonrisaSana";
    private static final String USER = "postgres";
    private static final String PASS = "admin123";

    // Pool de conexiones básico
    private static Connection connection;

    static {
        try {
            // Cargar driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error cargando driver PostgreSQL", e);
        }
    }

    private Dbconexion() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);
            props.setProperty("ssl", "false");
            props.setProperty("tcpKeepAlive", "true");
            props.setProperty("ApplicationName", "ClinicaSonrisaSana");

            connection = DriverManager.getConnection(URL, props);
        }
        return connection;
    }

    // ✅ MÉTODO FALTANTE - closeResources
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        // Cerrar ResultSet
        if (rs != null) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println("Error cerrando ResultSet: " + e.getMessage());
            }
        }

        // Cerrar Statement
        if (stmt != null) {
            try {
                if (!stmt.isClosed()) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.err.println("Error cerrando Statement: " + e.getMessage());
            }
        }

        // Cerrar Connection
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error cerrando Connection: " + e.getMessage());
            }
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error cerrando conexión", e);
            } finally {
                connection = null;
            }
        }
    }

    // Método para verificar la conexión
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}