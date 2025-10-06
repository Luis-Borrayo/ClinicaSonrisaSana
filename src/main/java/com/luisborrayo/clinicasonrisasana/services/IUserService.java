package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> list(int page, int size);
    int totalpaginas(int size);
    Optional<User> getById(int id);
    Optional<User> getByUser(String usuario);
    void save(User user);
    void delete(int id, int currentUserId);
    boolean validateLogin(String usuario, String password);

}
