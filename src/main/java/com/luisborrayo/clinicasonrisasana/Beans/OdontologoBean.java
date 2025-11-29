package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class OdontologoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private OdontologoService odontologoService;

    private List<Odontologo> lista;
    private Odontologo odontologoNuevo;
    private Odontologo odontologoSeleccionado;

    @PostConstruct
    public void init() {
        lista = odontologoService.obtenerTodosLosOdontologos();
        odontologoNuevo = new Odontologo();
        odontologoSeleccionado = new Odontologo();
    }

    public void guardarNuevo() {
        odontologoService.guardarOdontologo(odontologoNuevo);
        lista = odontologoService.obtenerTodosLosOdontologos();
        odontologoNuevo = new Odontologo();
    }

    public void actualizar() {
        odontologoService.guardarOdontologo(odontologoSeleccionado);
        lista = odontologoService.obtenerTodosLosOdontologos();
    }

    public void eliminar(Long id) {
        if (id != null) {
            odontologoService.eliminarOdontologo(id);
            lista = odontologoService.obtenerTodosLosOdontologos();
        }
    }

    public void eliminar() {
        if (odontologoSeleccionado != null && odontologoSeleccionado.getId() != null) {
            odontologoService.eliminarOdontologo(odontologoSeleccionado.getId());
            lista = odontologoService.obtenerTodosLosOdontologos();
        }
    }

    public List<Odontologo> getLista() { return lista; }

    public Odontologo getOdontologoNuevo() { return odontologoNuevo; }
    public void setOdontologoNuevo(Odontologo odontologoNuevo) { this.odontologoNuevo = odontologoNuevo; }

    public Odontologo getOdontologoSeleccionado() { return odontologoSeleccionado; }
    public void setOdontologoSeleccionado(Odontologo odontologoSeleccionado) { this.odontologoSeleccionado = odontologoSeleccionado; }
}
