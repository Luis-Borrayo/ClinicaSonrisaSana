package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.database.Dbconexion;
import com.luisborrayo.clinicasonrisasana.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UseRepository implements IUserRepository {
    @Override
    public List<User> findAll(int offset, int limit){
        String sql= "SELECT id, correo, nombres, apellidos, usuario, password, role, active FROM users ORDER BY id LIMIT ? OFFSET ?";
        try(PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql)){
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()){
                users.add(map(rs));
            }
            return users;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countAll(){
        try (Statement st = Dbconexion.getConnection().createStatement())
        {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            return rs.getInt(1);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(int id){
        String sql = "SELECT id, correo, nombres, apellidos, usuario, password, role, active FROM users WHERE id = ?";
        try (PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql))
        {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByUsername(String usuario){
        String sql = "SELECT id, correo, nombres, apellidos, usuario, password, role, active FROM users WHERE usuario = ?";
        try (PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql)){
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean existsByUsername(String usuario){
        String sql = "SELECT 1 FROM users WHERE usuario = ?";
        try (PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql)){
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public void create(User user){
        String sql = "INSERT INTO users (correo, nombres, apellidos, usuario, password, role, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql)){
            ps.setString(1, user.getCorreo());
            ps.setString(2, user.getNombres());
            ps.setString(3, user.getApellidos());
            ps.setString(4, user.getUsuario());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());
            ps.setBoolean(7, user.isActive());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public  void update (User user){
        String sql = "UPDATE users SET correo=?, nombres=?, apellidos=?,usuario=?,password=?,role=?,active=? WHERE id = ?";
        try (PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql)){
            ps.setString(1, user.getCorreo());
            ps.setString(2, user.getNombres());
            ps.setString(3, user.getApellidos());
            ps.setString(4, user.getUsuario());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());
            ps.setBoolean(7, user.isActive());
            ps.setInt(8, user.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteById(int id){
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = Dbconexion.getConnection().prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs)throws SQLException{
        return new User(
                rs.getInt("id"),
                rs.getString("correo"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("usuario"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getBoolean("active")
        );
    }
}
