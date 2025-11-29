package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "odontologos")
public class Odontologo {

    public enum Especialidad {
        ODONTOLOGO_GENERAL,
        ODONTOPEDIATRIA,
        ORTODONCIA,
        ENDODONCIA,
        PERIODONCIA,
        CIRUGIA_ORAL_Y_MAXILOFACIAL,
        PROSTODONCIA,
        IMPLANTOLOGIA_ORAL,
        ODONTOLOGIA_ESTETICA;

        // Método helper para mostrar nombres legibles
        public String getDisplayName() {
            return name().replace("_", " ");
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @NotBlank
    @Pattern(regexp = "C-\\d{4}", message = "Formato correcto: C-1234")
    @Column(unique = true, nullable = false)
    private String colegiado;

    @NotBlank
    @Column(nullable = false)
    private String antiguedad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidad especialidad;

    // Constructores
    public Odontologo() {}

    public Odontologo(User usuario, String colegiado, String antiguedad, Especialidad especialidad) {
        this.usuario = usuario;
        this.colegiado = colegiado;
        this.antiguedad = antiguedad;
        this.especialidad = especialidad;
    }

    // ========================================
    // MÉTODOS HELPER PARA LA INTERFAZ
    // ========================================

    /**
     * Obtiene el nombre completo del odontólogo con su especialidad
     * Usado en selectOneMenu para mostrar la lista completa
     */
    public String getNombreCompleto() {
        if (usuario != null) {
            return usuario.getNombres() + " " + usuario.getApellidos() + " - " + especialidad.getDisplayName();
        }
        return "Odontólogo " + colegiado + " - " + especialidad.getDisplayName();
    }

    /**
     * Obtiene solo el nombre del odontólogo sin especialidad
     * Usado en tablas para mostrar solo el nombre
     */
    public String getNombre() {
        if (usuario != null) {
            return usuario.getNombres() + " " + usuario.getApellidos();
        }
        return "Odontólogo " + colegiado;
    }

    // ========================================
    // GETTERS Y SETTERS
    // ========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public String getColegiado() {
        return colegiado;
    }

    public void setColegiado(String colegiado) {
        this.colegiado = colegiado;
    }

    public String getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(String antiguedad) {
        this.antiguedad = antiguedad;
    }

    public Boolean getActivo() {
        // Asumimos que si el usuario está activo, el odontólogo también lo está
        return usuario != null && usuario.isActive();
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    // ========================================
    // MÉTODOS CRÍTICOS PARA OMNIFACES CONVERTER
    // ========================================

    /**
     * CRÍTICO: Este método es usado por omnifaces.SelectItemsConverter
     * para convertir el objeto a String
     */
    @Override
    public String toString() {
        // Usar el ID como identificador único
        return String.format("Odontologo[id=%d]", id != null ? id : 0);
    }

    /**
     * CRÍTICO: Necesario para que JSF pueda comparar objetos correctamente
     * OmniFaces SelectItemsConverter requiere equals() implementado
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Odontologo)) return false;
        Odontologo that = (Odontologo) o;
        return id != null && id.equals(that.id);
    }

    /**
     * CRÍTICO: Debe ser consistente con equals()
     * Necesario para uso en colecciones (HashSet, HashMap, etc.)
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}