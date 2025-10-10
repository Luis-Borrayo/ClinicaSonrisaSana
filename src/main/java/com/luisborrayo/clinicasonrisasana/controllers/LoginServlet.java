package com.luisborrayo.clinicasonrisasana.controllers;

import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.services.impl.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");


        Optional<User> user = userService.getByUser(usuario);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("auth", true);
            newSession.setAttribute("usuario", usuario);
            newSession.setAttribute("role", user.get().getRole());
            newSession.setMaxInactiveInterval(15 * 60);

            response.sendRedirect(request.getContextPath() + "/principal.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp?err=1");
        }
    }
}
