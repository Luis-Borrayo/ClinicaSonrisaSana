package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.database.Dbconexion;
import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OdontologoRepository {

    public List<Odontologo> buscarTodos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Odontologo> odontologos = new ArrayList<>();

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT * FROM odontologos ORDER BY nombre";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Odontologo odontologo = new Odontologo();
                odontologo.setId(rs.getLong("id"));
                odontologo.setEspecialidad(rs.getString("especialidad"));
                odontologo.setColegiado(rs.getString("colegiado"));
                odontologos.add(odontologo);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando odontólogos: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return odontologos;
    }

    public Odontologo buscarPorId(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT * FROM odontologos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Odontologo odontologo = new Odontologo();
                odontologo.setId(rs.getLong("id"));
                odontologo.setEspecialidad(rs.getString("especialidad"));
                odontologo.setColegiado(rs.getString("colegiado"));
                return odontologo;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando odontólogo: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return null;
    }
}
