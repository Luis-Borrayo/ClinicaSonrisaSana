package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import com.luisborrayo.clinicasonrisasana.services.TratamientoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class TratamientoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TratamientoService tratamientoService;

    @Inject
    private OdontologoService odontologoService;

    private List<Tratamiento> lista;
    private Tratamiento tratamiento;
    private List<Odontologo> odontologos;

    @PostConstruct
    public void init() {
        limpiar();
        cargarLista();
        cargarOdontologos();
    }

    public void cargarLista() {
        try {
            lista = tratamientoService.obtenerTodosLosTratamientos();
            System.out.println("✅ Tratamientos cargados: " + (lista != null ? lista.size() : 0));
        } catch (Exception e) {
            System.err.println("❌ Error al cargar tratamientos: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar tratamientos",
                    e.getMessage());
        }
    }

    public void cargarOdontologos() {
        try {
            odontologos = odontologoService.obtenerTodosLosOdontologos();

            // Filtrar solo odontólogos activos
            if (odontologos != null) {
                odontologos = odontologos.stream()
                        .filter(o -> o.getActivo() != null && o.getActivo())
                        .collect(Collectors.toList());
            }

            System.out.println("✅ Odontólogos activos cargados: " +
                    (odontologos != null ? odontologos.size() : 0));
        } catch (Exception e) {
            System.err.println("❌ Error al cargar odontólogos: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar odontólogos",
                    e.getMessage());
        }
    }

    public void limpiar() {
        tratamiento = new Tratamiento();
        System.out.println("✅ Formulario limpiado");
    }

    public void editar(Tratamiento tratamiento) {
        try {
            if (tratamiento != null) {
                this.tratamiento = tratamiento;
                System.out.println("✅ Editando tratamiento: " + tratamiento.getNombre());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al editar: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al editar tratamiento",
                    e.getMessage());
        }
    }

    public void guardar() {
        try {
            // Validaciones
            if (tratamiento.getNombre() == null || tratamiento.getNombre().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "El nombre es obligatorio");
                return;
            }

            if (tratamiento.getDescripcion() == null || tratamiento.getDescripcion().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "La descripción es obligatoria");
                return;
            }

            if (tratamiento.getDuracion() == null || tratamiento.getDuracion() <= 0) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "La duración debe ser mayor a 0");
                return;
            }

            if (tratamiento.getCosto() == null || tratamiento.getCosto().doubleValue() <= 0) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "El costo debe ser mayor a 0");
                return;
            }

            if (tratamiento.getEspecialidad() == null) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "Debe seleccionar una especialidad");
                return;
            }

            if (tratamiento.getOdontologo() == null) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "Debe seleccionar un odontólogo");
                return;
            }

            // Guardar o actualizar
            if (tratamiento.getId() == null) {
                tratamientoService.crearTratamiento(tratamiento);
                addMessage(FacesMessage.SEVERITY_INFO,
                        "Éxito",
                        "Tratamiento creado correctamente: " + tratamiento.getNombre());
                System.out.println("✅ Tratamiento creado: " + tratamiento.getNombre());
            } else {
                tratamientoService.actualizarTratamiento(tratamiento);
                addMessage(FacesMessage.SEVERITY_INFO,
                        "Éxito",
                        "Tratamiento actualizado correctamente: " + tratamiento.getNombre());
                System.out.println("✅ Tratamiento actualizado: " + tratamiento.getNombre());
            }

            limpiar();
            cargarLista();

        } catch (Exception e) {
            System.err.println("❌ Error al guardar tratamiento: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al guardar",
                    "No se pudo guardar el tratamiento: " + e.getMessage());
        }
    }

    public void eliminar(Long id) {
        try {
            if (id == null) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "ID de tratamiento inválido");
                return;
            }

            Tratamiento tratamientoAEliminar = tratamientoService.obtenerTratamientoPorId(id);

            if (tratamientoAEliminar == null) {
                addMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia",
                        "Tratamiento no encontrado");
                return;
            }

            String nombreTratamiento = tratamientoAEliminar.getNombre();
            tratamientoService.eliminarTratamiento(id);

            addMessage(FacesMessage.SEVERITY_INFO,
                    "Eliminado",
                    "Tratamiento eliminado: " + nombreTratamiento);
            System.out.println("✅ Tratamiento eliminado: " + nombreTratamiento);

            cargarLista();

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar tratamiento: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al eliminar",
                    "No se pudo eliminar el tratamiento: " + e.getMessage());
        }
    }

    // Método para obtener todas las especialidades disponibles
    public List<String> getEspecialidades() {
        return Arrays.stream(Odontologo.Especialidad.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mensajes
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getters y Setters
    public List<Tratamiento> getLista() {
        return lista;
    }

    public void setLista(List<Tratamiento> lista) {
        this.lista = lista;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public List<Odontologo> getOdontologos() {
        return odontologos;
    }

    public void setOdontologos(List<Odontologo> odontologos) {
        this.odontologos = odontologos;
    }
}