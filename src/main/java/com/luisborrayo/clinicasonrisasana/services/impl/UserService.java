package com.luisborrayo.clinicasonrisasana.services.impl;

import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.repositories.IUserRepository;
import com.luisborrayo.clinicasonrisasana.repositories.UseRepository;
import com.luisborrayo.clinicasonrisasana.services.IUserService;

import java.util.List;
import java.util.Optional;

public class UserService implements IUserService {
    private final IUserRepository repo = new UseRepository();

    @Override
    public List<User> list(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return repo.findAll(offset, size);
    }

    @Override
    public int totalpaginas(int size) {
        {
            int total = repo.countAll();
            return (int) Math.ceil(total / (double) size);
        }
    }

    @Override
    public Optional<User> getById(int id) {
        return repo.findById(id);
    }
    @Override
    public Optional<User> getByUser(String usuario){
        return repo.findByUsername(usuario);
    }
    @Override
    public void save(User user){
        if(user.getId() == null){
            if (repo.existsByUsername(user.getUsuario()))
                throw new RuntimeException("Usuario ya existe");
            repo.create(user);
        }else {
            repo.update(user);
        }
    }
    @Override
    public void delete(int id, int currentUserId){
        if (id == currentUserId)
            throw new RuntimeException("No puede eliminar su proppio usuario.");
            repo.deleteById(id);
    }
    @Override
    public boolean validateLogin(String usuario, String password){
        return repo.findByUsername(usuario)
                .filter(User::isActive)
                .filter(u -> u .getPassword().equals(password))
                .isPresent();
    }
}