package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Facturas;
import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/_health/test-save-complete")
public class TestSaveCompletePaciente extends HttpServlet {

    @Inject
    EntityManagerFactory emf;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h1>🔍 Test Guardar Paciente Completo</h1>");

        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            // PASO 1: Buscar un odontólogo existente
            out.println("<h2>PASO 1: Buscar Odontólogo</h2>");
            List<Odontologo> odontologos = em.createQuery(
                    "SELECT o FROM Odontologo o", Odontologo.class
            ).getResultList();

            if (odontologos.isEmpty()) {
                out.println("❌ NO HAY ODONTÓLOGOS EN LA BASE DE DATOS<br>");
                out.println("<strong>SOLUCIÓN: Debes crear al menos un odontólogo primero</strong><br>");
                return;
            }

            Odontologo odontologo = odontologos.get(0);
            out.println("✅ Odontólogo encontrado: " + odontologo.getNombreCompleto() + "<br>");
            out.println("   ID: " + odontologo.getId() + "<br>");

            // PASO 2: Crear paciente con TODOS los campos
            out.println("<h2>PASO 2: Crear Paciente</h2>");
            Pacientes testPaciente = new Pacientes();
            testPaciente.setDpi("9876543210987");
            testPaciente.setNombre("Juan");
            testPaciente.setApellido("Pérez");
            testPaciente.setFechaNacimiento(LocalDate.of(1985, 5, 15));
            testPaciente.setContacto("55551234");
            testPaciente.setCorreo("juan.perez@test.com");
            testPaciente.setDireccion("Zona 10, Guatemala");
            testPaciente.setAlergias("Ninguna");
            testPaciente.setCondiciones("Ninguna");
            testPaciente.setObservaciones("Paciente de prueba");

            // ✅ ASIGNAR ODONTÓLOGO
            testPaciente.setOdontologo(odontologo);
            out.println("✅ Odontólogo asignado<br>");

            // ✅ ASIGNAR SEGURO
            testPaciente.setSeguro(Facturas.Seguro.INTEGRO);
            out.println("✅ Seguro asignado: " + Facturas.Seguro.INTEGRO + "<br>");

            // PASO 3: Guardar en base de datos
            out.println("<h2>PASO 3: Guardar en Base de Datos</h2>");
            em.getTransaction().begin();
            out.println("🔵 Transacción iniciada<br>");

            em.persist(testPaciente);
            out.println("✅ Persist ejecutado<br>");

            em.flush();
            out.println("✅ Flush ejecutado<br>");

            em.getTransaction().commit();
            out.println("✅ Commit exitoso<br>");

            out.println("<h2>🎉 ¡PACIENTE GUARDADO EXITOSAMENTE!</h2>");
            out.println("<div style='background: #d4edda; padding: 20px; border-radius: 5px; margin: 20px 0;'>");
            out.println("<strong>ID del paciente:</strong> " + testPaciente.getId() + "<br>");
            out.println("<strong>Nombre:</strong> " + testPaciente.getNombreCompleto() + "<br>");
            out.println("<strong>DPI:</strong> " + testPaciente.getDpi() + "<br>");
            out.println("<strong>Odontólogo:</strong> " + testPaciente.getOdontologo().getNombreCompleto() + "<br>");
            out.println("<strong>Seguro:</strong> " + testPaciente.getSeguro() + "<br>");
            out.println("</div>");

            // Verificar en base de datos
            Long totalPacientes = em.createQuery("SELECT COUNT(p) FROM Pacientes p", Long.class).getSingleResult();
            out.println("<p><strong>Total de pacientes en DB:</strong> " + totalPacientes + "</p>");

            out.println("<h2>✅ CONCLUSIÓN</h2>");
            out.println("<p style='color: green; font-size: 18px;'>");
            out.println("<strong>El backend funciona perfectamente. El problema está en el formulario JSF.</strong>");
            out.println("</p>");

        } catch (Exception e) {
            out.println("<h2>❌ ERROR</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");

            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}