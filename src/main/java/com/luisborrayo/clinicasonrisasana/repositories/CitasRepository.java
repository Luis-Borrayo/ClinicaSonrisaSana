package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.database.Dbconexion;
import com.luisborrayo.clinicasonrisasana.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CitasRepository {

    // CREATE
    public void crear(Citas cita) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Dbconexion.getConnection();
            String sql = "INSERT INTO citas (paciente_id, odontologo_id, tratamiento_id, fechacita, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, cita.getPaciente().getId());
            stmt.setLong(2, cita.getOdontologo().getId());
            if (cita.getTratamiento() != null) {
                stmt.setLong(3, cita.getTratamiento().getId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            stmt.setTimestamp(4, Timestamp.valueOf(cita.getFechaCita()));
            stmt.setString(5, cita.getEstado());
            stmt.setString(6, cita.getObservaciones());

            stmt.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cita.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creando cita: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, null);
        }
    }

    // READ - Buscar todas las citas
    public List<Citas> buscarTodas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Citas> citas = new ArrayList<>();

        try {
            conn = Dbconexion.getConnection();
            String sql = """
                SELECT c.*, p.nombre as paciente_nombre, p.email as paciente_email, p.telefono as paciente_telefono,
                       o.nombre as odontologo_nombre, o.especialidad as odontologo_especialidad, o.matricula as odontologo_matricula,
                       t.nombre as tratamiento_nombre, t.descripcion as tratamiento_descripcion, t.costo as tratamiento_costo
                FROM citas c
                LEFT JOIN pacientes p ON c.paciente_id = p.id
                LEFT JOIN odontologos o ON c.odontologo_id = o.id
                LEFT JOIN tratamientos t ON c.tratamiento_id = t.id
                ORDER BY c.fechacita DESC
                """;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Citas cita = new Citas();
                cita.setId(rs.getLong("id"));
                cita.setFechaCita(rs.getTimestamp("fechacita").toLocalDateTime());
                cita.setEstado(rs.getString("estado"));
                cita.setObservaciones(rs.getString("observaciones"));

                // Crear y asignar Paciente
                Paciente paciente = new Paciente();
                paciente.setId(rs.getLong("paciente_id"));
                paciente.setNombre(rs.getString("paciente_nombre"));
                cita.setPaciente(paciente);

                // Crear y asignar Odontologo
                Odontologo odontologo = new Odontologo();
                odontologo.setId(rs.getLong("odontologo_id"));
                odontologo.setEspecialidad(rs.getString("odontologo_especialidad"));
                cita.setOdontologo(odontologo);

                // Crear y asignar Tratamiento (si existe)
                Long tratamientoId = rs.getLong("tratamiento_id");
                if (!rs.wasNull()) {
                    Tratamiento tratamiento = new Tratamiento();
                    tratamiento.setId(tratamientoId);
                    tratamiento.setNombre(rs.getString("tratamiento_nombre"));
                    tratamiento.setDescripcion(rs.getString("tratamiento_descripcion"));
                    tratamiento.setCosto(rs.getDouble("tratamiento_costo"));
                    cita.setTratamiento(tratamiento);
                }

                citas.add(cita);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando citas: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return citas;
    }

    // DELETE
    public void eliminar(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Dbconexion.getConnection();
            String sql = "DELETE FROM citas WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando cita: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, null);
        }
    }

    // Verificar si existe cita en horario
    public boolean existeCitaEnHorario(Odontologo odontologo, LocalDateTime fechaCita) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Dbconexion.getConnection();
            String sql = "SELECT COUNT(*) FROM citas WHERE odontologo_id = ? AND fechacita = ? AND estado != 'CANCELADA'";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, odontologo.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(fechaCita));
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error verificando horario: " + e.getMessage(), e);
        } finally {
            Dbconexion.closeResources(conn, stmt, rs);
        }
        return false;
    }
}