package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
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

@WebServlet("/_health/test-save")
public class TestConnectionServlet extends HttpServlet {

    @Inject
    EntityManagerFactory emf;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h1>🔍 Diagnóstico de Base de Datos</h1>");

        // TEST 1: EntityManagerFactory
        out.println("<h2>TEST 1: EntityManagerFactory</h2>");
        if (emf == null) {
            out.println("❌ EntityManagerFactory es NULL<br>");
            return;
        }
        out.println("✅ EntityManagerFactory está inyectado<br>");
        out.println("✅ EMF está abierto: " + emf.isOpen() + "<br>");

        EntityManager em = null;
        try {
            // TEST 2: Crear EntityManager
            out.println("<h2>TEST 2: Crear EntityManager</h2>");
            em = emf.createEntityManager();
            out.println("✅ EntityManager creado<br>");
            out.println("✅ EM está abierto: " + em.isOpen() + "<br>");

            // TEST 3: Consulta básica
            out.println("<h2>TEST 3: Consulta SELECT 1</h2>");
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            out.println("✅ Consulta exitosa: " + result + "<br>");

            // TEST 4: Verificar tabla pacientes
            out.println("<h2>TEST 4: Verificar tabla pacientes</h2>");
            Long count = em.createQuery("SELECT COUNT(p) FROM Pacientes p", Long.class).getSingleResult();
            out.println("✅ Pacientes en DB: " + count + "<br>");

            // TEST 5: Insertar paciente de prueba
            out.println("<h2>TEST 5: Insertar paciente de prueba</h2>");

            try {
                out.println("🔵 Iniciando transacción...<br>");
                em.getTransaction().begin();
                out.println("✅ Transacción iniciada<br>");

                Pacientes testPaciente = new Pacientes();
                testPaciente.setDpi("1234567890123");
                testPaciente.setNombre("TEST");
                testPaciente.setApellido("DIAGNOSTICO");
                testPaciente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                testPaciente.setContacto("12345678");
                testPaciente.setDireccion("Test Address");

                out.println("🔵 Persistiendo paciente...<br>");
                em.persist(testPaciente);
                out.println("✅ Persist ejecutado<br>");

                out.println("🔵 Haciendo flush...<br>");
                em.flush();
                out.println("✅ Flush ejecutado<br>");

                out.println("🔵 Haciendo commit...<br>");
                em.getTransaction().commit();
                out.println("✅ Commit exitoso<br>");

                out.println("<h3>✅ PACIENTE GUARDADO CON ID: " + testPaciente.getId() + "</h3>");

                // Verificar que se guardó
                Long newCount = em.createQuery("SELECT COUNT(p) FROM Pacientes p", Long.class).getSingleResult();
                out.println("✅ Pacientes ahora en DB: " + newCount + "<br>");

            } catch (Exception e) {
                out.println("<h3>❌ ERROR AL GUARDAR PACIENTE:</h3>");
                out.println("<pre>");
                e.printStackTrace(out);
                out.println("</pre>");

                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                    out.println("🔄 Rollback ejecutado<br>");
                }
            }

        } catch (Exception e) {
            out.println("<h3>❌ ERROR GENERAL:</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                out.println("<br>✅ EntityManager cerrado<br>");
            }
        }
    }
}