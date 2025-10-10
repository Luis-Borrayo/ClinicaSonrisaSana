package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.database.Dbconexion;
import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TratamientoRepository {

    public List<Tratamiento> buscarTodos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Tratamiento> tratamientos = new ArrayList<>();

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT * FROM tratamientos ORDER BY nombre";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Tratamiento tratamiento = new Tratamiento();
                tratamiento.setId(rs.getLong("id"));
                tratamiento.setNombre(rs.getString("nombre"));
                tratamiento.setDescripcion(rs.getString("descripcion"));
                tratamiento.setCosto(rs.getDouble("costo"));
                tratamientos.add(tratamiento);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando tratamientos: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return tratamientos;
    }

    public Tratamiento buscarPorId(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT * FROM tratamientos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Tratamiento tratamiento = new Tratamiento();
                tratamiento.setId(rs.getLong("id"));
                tratamiento.setNombre(rs.getString("nombre"));
                tratamiento.setDescripcion(rs.getString("descripcion"));
                tratamiento.setCosto(rs.getDouble("costo"));
                return tratamiento;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando tratamiento: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return null;
    }
}