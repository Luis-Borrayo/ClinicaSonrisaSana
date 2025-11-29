package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Citas;
import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import com.luisborrayo.clinicasonrisasana.services.CitasService;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import com.luisborrayo.clinicasonrisasana.services.PacienteService;
import com.luisborrayo.clinicasonrisasana.services.TratamientoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("citasBean")
@ViewScoped
public class CitasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CitasService citasService;

    @Inject
    private PacienteService pacienteService;

    @Inject
    private OdontologoService odontologoService;

    @Inject
    private TratamientoService tratamientoService;

    private List<Citas> citas;
    private Citas citaSeleccionada;
    private Citas nuevaCita;

    private List<Pacientes> pacientes;
    private List<Odontologo> odontologos;
    private List<Tratamiento> tratamientos;

    private Long pacienteId;
    private Long odontologoId;
    private Long tratamientoId;

    @PostConstruct
    public void init() {
        nuevaCita = new Citas();
        cargarCitas();
        cargarOpciones();
    }

    public void cargarCitas() {
        try {
            citas = citasService.obtenerTodasLasCitas();
            if (citas == null) {
                citas = new ArrayList<>();
            }
            System.out.println("Citas cargadas: " + citas.size());
        } catch (Exception e) {
            e.printStackTrace();
            citas = new ArrayList<>();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar las citas: " + e.getMessage());
        }
    }

    public void cargarOpciones() {
        try {
            pacientes = pacienteService.obtenerTodosLosPacientes();
            odontologos = odontologoService.obtenerTodosLosOdontologos();
            tratamientos = tratamientoService.obtenerTodosLosTratamientos();

            System.out.println("Pacientes: " + (pacientes != null ? pacientes.size() : "null"));
            System.out.println("Odontólogos: " + (odontologos != null ? odontologos.size() : "null"));
            System.out.println("Tratamientos: " + (tratamientos != null ? tratamientos.size() : "null"));
        } catch (Exception e) {
            e.printStackTrace();
            pacientes = new ArrayList<>();
            odontologos = new ArrayList<>();
            tratamientos = new ArrayList<>();
        }
    }

    public void guardarCita() {
        try {
            if (pacienteId == null || odontologoId == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar paciente y odontólogo");
                return;
            }

            if (nuevaCita.getFechaCita() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar fecha y hora");
                return;
            }

            Pacientes paciente = pacienteService.obtenerPacientePorId(pacienteId);
            Odontologo odontologo = odontologoService.obtenerOdontologoPorId(odontologoId);
            Tratamiento tratamiento = tratamientoId != null ?
                    tratamientoService.obtenerTratamientoPorId(tratamientoId) : null;

            nuevaCita.setPaciente(paciente);
            nuevaCita.setOdontologo(odontologo);
            nuevaCita.setTratamiento(tratamiento);

            if (nuevaCita.getPaciente() == null || nuevaCita.getOdontologo() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al cargar datos del paciente u odontólogo");
                return;
            }

            if (nuevaCita.getId() == null) {
                citasService.crearCita(nuevaCita);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cita creada correctamente");
            } else {
                citasService.actualizarCita(nuevaCita);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cita actualizada correctamente");
            }

            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la cita: " + e.getMessage());
        }
    }

    private void resetForm() {
        nuevaCita = new Citas();
        pacienteId = null;
        odontologoId = null;
        tratamientoId = null;
    }

    public void eliminarCita() {
        if (citaSeleccionada != null) {
            try {
                citasService.eliminarCita(citaSeleccionada.getId());
                cargarCitas();
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cita eliminada correctamente");
            } catch (Exception e) {
                e.printStackTrace();
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la cita: " + e.getMessage());
            }
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public List<Citas> getCitas() { return citas; }
    public void setCitas(List<Citas> citas) { this.citas = citas; }

    public Citas getCitaSeleccionada() { return citaSeleccionada; }
    public void setCitaSeleccionada(Citas citaSeleccionada) { this.citaSeleccionada = citaSeleccionada; }

    public Citas getNuevaCita() { return nuevaCita; }
    public void setNuevaCita(Citas nuevaCita) { this.nuevaCita = nuevaCita; }

    public List<Pacientes> getPacientes() { return pacientes; }
    public List<Odontologo> getOdontologos() { return odontologos; }
    public List<Tratamiento> getTratamientos() { return tratamientos; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getOdontologoId() { return odontologoId; }
    public void setOdontologoId(Long odontologoId) { this.odontologoId = odontologoId; }

    public Long getTratamientoId() { return tratamientoId; }
    public void setTratamientoId(Long tratamientoId) { this.tratamientoId = tratamientoId; }
}