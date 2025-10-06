package com.luisborrayo.clinicasonrisasana.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dbconexion {
    private static final String URL = "jdbc:postgresql://localhost:5433/ClinicaSonrisaSana";
    private static final String USER = "postgres";
    private static final String PASS = "admin123";

    private static Connection connection;
    private Dbconexion() {}

    public static Connection getConnection(){
        if (connection != null){ return connection; }
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            return connection;
        }catch (Exception e){
        throw new RuntimeException("Error al conectar base de dato" + e.getMessage(), e);}
    }
    public static void closeConnection(){
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e){
                throw  new RuntimeException(e);
            }
        }
    }


}
