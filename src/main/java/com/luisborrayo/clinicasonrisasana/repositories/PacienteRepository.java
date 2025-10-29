package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.database.Dbconexion;
import com.luisborrayo.clinicasonrisasana.model.Paciente;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PacienteRepository {

    public List<Paciente> buscarTodos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Paciente> pacientes = new ArrayList<>();

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT * FROM pacientes ORDER BY nombre";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getLong("id"));
                paciente.setNombre(rs.getString("nombre"));
                pacientes.add(paciente);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando pacientes: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return pacientes;
    }

    public Paciente buscarPorId(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT * FROM pacientes WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getLong("id"));
                paciente.setNombre(rs.getString("nombre"));
                return paciente;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando paciente: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return null;
    }
}