package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.*;
import com.luisborrayo.clinicasonrisasana.services.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Named("reportesBean")
@ViewScoped
public class ReportesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CitasService citasService;

    @Inject
    private PacienteService pacienteService;

    @Inject
    private FacturaService facturaService;

    @Inject
    private UserService userService;

    @Inject
    private OdontologoService odontologoService;

    // Filtros de fecha
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String periodoSeleccionado = "mes";

    // Datos de reportes
    private List<Citas> citasPorPeriodo;
    private List<Facturas> facturasPorPeriodo;
    private List<Pacientes> pacientesRegistrados;

    // Estadísticas generales
    private long totalCitas;
    private long totalPacientes;
    private long totalUsuarios;
    private BigDecimal ingresoTotal;
    private BigDecimal ingresoPendiente;

    // Estadísticas de citas
    private List<EstadisticaItem> citasPorEstado;
    private List<EstadisticaItem> citasPorOdontologo;
    private List<EstadisticaItem> citasPorMes;

    // Estadísticas de facturación
    private List<EstadisticaMonetaria> facturasPorEstado;
    private List<EstadisticaMonetaria> ingresosPorMes;
    private List<EstadisticaMonetaria> ingresosPorOdontologo;

    // Top datos
    private List<OdontologoStats> topOdontologos;
    private List<TratamientoStats> topTratamientos;

    @PostConstruct
    public void init() {
        LocalDate hoy = LocalDate.now();
        fechaInicio = hoy.withDayOfMonth(1);
        fechaFin = hoy.withDayOfMonth(hoy.lengthOfMonth());

        cargarReportes();
    }

    public void cargarReportes() {
        try {
            System.out.println("🔄 Cargando reportes...");
            cargarEstadisticasGenerales();
            cargarEstadisticasCitas();
            cargarEstadisticasFacturacion();
            cargarTopOdontologos();
            cargarTopTratamientos();
            System.out.println("✅ Reportes cargados exitosamente");
        } catch (Exception e) {
            System.err.println("❌ Error cargando reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEstadisticasGenerales() {
        // Total de pacientes
        pacientesRegistrados = pacienteService.obtenerTodosLosPacientes();
        totalPacientes = pacientesRegistrados != null ? pacientesRegistrados.size() : 0;

        // Total de usuarios
        List<User> usuarios = userService.listar();
        totalUsuarios = usuarios != null ? usuarios.size() : 0;

        // Citas en el período
        List<Citas> todasCitas = citasService.obtenerTodasLasCitas();
        citasPorPeriodo = filtrarCitasPorFecha(todasCitas);
        totalCitas = citasPorPeriodo.size();

        // Facturas en el período
        List<Facturas> todasFacturas = facturaService.findAll();
        facturasPorPeriodo = filtrarFacturasPorFecha(todasFacturas);

        // Ingresos
        ingresoTotal = facturasPorPeriodo.stream()
                .filter(f -> f.getEstadoPago() == Facturas.EstadoPago.CANCELADO)
                .map(Facturas::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ingresoPendiente = facturasPorPeriodo.stream()
                .filter(f -> f.getEstadoPago() == Facturas.EstadoPago.PENDIENTE)
                .map(Facturas::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("📊 Total Citas: " + totalCitas);
        System.out.println("👥 Total Pacientes: " + totalPacientes);
        System.out.println("💰 Ingreso Total: Q" + ingresoTotal);
    }

    private void cargarEstadisticasCitas() {
        // Citas por estado
        Map<String, Long> citasPorEstadoMap = citasPorPeriodo.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getEstado() != null ? c.getEstado().toString() : "SIN_ESTADO",
                        Collectors.counting()
                ));

        citasPorEstado = new ArrayList<>();
        for (Map.Entry<String, Long> entry : citasPorEstadoMap.entrySet()) {
            citasPorEstado.add(new EstadisticaItem(entry.getKey(), entry.getValue()));
        }

        // Citas por odontólogo
        Map<String, Long> citasPorOdontMap = citasPorPeriodo.stream()
                .filter(c -> c.getOdontologo() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getOdontologo().getNombreCompleto(),
                        Collectors.counting()
                ));

        citasPorOdontologo = new ArrayList<>();
        for (Map.Entry<String, Long> entry : citasPorOdontMap.entrySet()) {
            citasPorOdontologo.add(new EstadisticaItem(entry.getKey(), entry.getValue()));
        }

        // Citas por mes
        Map<String, Long> citasPorMesMap = citasPorPeriodo.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFechaCita().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        Collectors.counting()
                ));

        citasPorMes = new ArrayList<>();
        for (Map.Entry<String, Long> entry : citasPorMesMap.entrySet()) {
            citasPorMes.add(new EstadisticaItem(entry.getKey(), entry.getValue()));
        }
    }

    private void cargarEstadisticasFacturacion() {
        // Facturas por estado de pago
        Map<String, BigDecimal> facturasPorEstadoMap = facturasPorPeriodo.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getEstadoPago() != null ? f.getEstadoPago().toString() : "SIN_ESTADO",
                        Collectors.reducing(BigDecimal.ZERO, Facturas::getTotal, BigDecimal::add)
                ));

        facturasPorEstado = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : facturasPorEstadoMap.entrySet()) {
            facturasPorEstado.add(new EstadisticaMonetaria(entry.getKey(), entry.getValue()));
        }

        // Ingresos por mes
        Map<String, BigDecimal> ingresosPorMesMap = facturasPorPeriodo.stream()
                .filter(f -> f.getEstadoPago() == Facturas.EstadoPago.CANCELADO)
                .collect(Collectors.groupingBy(
                        f -> f.getFechaEmision().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        Collectors.reducing(BigDecimal.ZERO, Facturas::getTotal, BigDecimal::add)
                ));

        ingresosPorMes = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : ingresosPorMesMap.entrySet()) {
            ingresosPorMes.add(new EstadisticaMonetaria(entry.getKey(), entry.getValue()));
        }

        // Ingresos por odontólogo
        Map<String, BigDecimal> ingresosPorOdontMap = facturasPorPeriodo.stream()
                .filter(f -> f.getEstadoPago() == Facturas.EstadoPago.CANCELADO)
                .filter(f -> f.getOdontologo() != null)
                .collect(Collectors.groupingBy(
                        f -> f.getOdontologo().getNombreCompleto(),
                        Collectors.reducing(BigDecimal.ZERO, Facturas::getTotal, BigDecimal::add)
                ));

        ingresosPorOdontologo = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : ingresosPorOdontMap.entrySet()) {
            ingresosPorOdontologo.add(new EstadisticaMonetaria(entry.getKey(), entry.getValue()));
        }
    }

    private void cargarTopOdontologos() {
        topOdontologos = new ArrayList<>();

        Map<Long, Long> citasPorOdontId = citasPorPeriodo.stream()
                .filter(c -> c.getOdontologo() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getOdontologo().getId(),
                        Collectors.counting()
                ));

        Map<Long, BigDecimal> ingresosPorOdontId = facturasPorPeriodo.stream()
                .filter(f -> f.getOdontologo() != null)
                .filter(f -> f.getEstadoPago() == Facturas.EstadoPago.CANCELADO)
                .collect(Collectors.groupingBy(
                        f -> f.getOdontologo().getId(),
                        Collectors.reducing(BigDecimal.ZERO, Facturas::getTotal, BigDecimal::add)
                ));

        List<Odontologo> odontologos = odontologoService.obtenerTodosLosOdontologos();
        for (Odontologo od : odontologos) {
            Long citas = citasPorOdontId.getOrDefault(od.getId(), 0L);
            BigDecimal ingresos = ingresosPorOdontId.getOrDefault(od.getId(), BigDecimal.ZERO);

            if (citas > 0 || ingresos.compareTo(BigDecimal.ZERO) > 0) {
                OdontologoStats stats = new OdontologoStats();
                stats.setNombre(od.getNombreCompleto());
                stats.setEspecialidad(od.getEspecialidad() != null ? od.getEspecialidad().toString() : "");
                stats.setTotalCitas(citas);
                stats.setIngresoGenerado(ingresos);
                topOdontologos.add(stats);
            }
        }

        topOdontologos.sort(Comparator.comparing(OdontologoStats::getIngresoGenerado).reversed());
    }

    private void cargarTopTratamientos() {
        topTratamientos = new ArrayList<>();

        Map<String, Long> tratamientosCont = citasPorPeriodo.stream()
                .filter(c -> c.getTratamiento() != null)
                .filter(c -> c.getTratamiento().getDescripcion() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getTratamiento().getDescripcion(),
                        Collectors.counting()
                ));

        Map<String, BigDecimal> tratamientosIngresos = facturasPorPeriodo.stream()
                .filter(f -> f.getTratamiento() != null)
                .filter(f -> f.getTratamiento().getDescripcion() != null)
                .filter(f -> f.getEstadoPago() == Facturas.EstadoPago.CANCELADO)
                .collect(Collectors.groupingBy(
                        f -> f.getTratamiento().getDescripcion(),
                        Collectors.reducing(BigDecimal.ZERO, Facturas::getTotal, BigDecimal::add)
                ));

        for (String nombre : tratamientosCont.keySet()) {
            TratamientoStats stats = new TratamientoStats();
            stats.setNombre(nombre);
            stats.setVecesRealizado(tratamientosCont.getOrDefault(nombre, 0L));
            stats.setIngresoGenerado(tratamientosIngresos.getOrDefault(nombre, BigDecimal.ZERO));
            topTratamientos.add(stats);
        }

        topTratamientos.sort(Comparator.comparing(TratamientoStats::getIngresoGenerado).reversed());
    }

    public void cambiarPeriodo() {
        LocalDate hoy = LocalDate.now();

        switch (periodoSeleccionado) {
            case "mes":
                fechaInicio = hoy.withDayOfMonth(1);
                fechaFin = hoy.withDayOfMonth(hoy.lengthOfMonth());
                break;
            case "trimestre":
                int mesActual = hoy.getMonthValue();
                int inicioTrimestre = ((mesActual - 1) / 3) * 3 + 1;
                fechaInicio = LocalDate.of(hoy.getYear(), inicioTrimestre, 1);
                fechaFin = fechaInicio.plusMonths(2).withDayOfMonth(
                        fechaInicio.plusMonths(2).lengthOfMonth()
                );
                break;
            case "año":
                fechaInicio = LocalDate.of(hoy.getYear(), 1, 1);
                fechaFin = LocalDate.of(hoy.getYear(), 12, 31);
                break;
        }

        cargarReportes();
    }

    private List<Citas> filtrarCitasPorFecha(List<Citas> citas) {
        if (citas == null) return new ArrayList<>();

        return citas.stream()
                .filter(c -> c.getFechaCita() != null)
                .filter(c -> {
                    LocalDate fechaCita = c.getFechaCita().toLocalDate();
                    return !fechaCita.isBefore(fechaInicio) && !fechaCita.isAfter(fechaFin);
                })
                .collect(Collectors.toList());
    }

    private List<Facturas> filtrarFacturasPorFecha(List<Facturas> facturas) {
        if (facturas == null) return new ArrayList<>();

        return facturas.stream()
                .filter(f -> f.getFechaEmision() != null)
                .filter(f -> {
                    LocalDate fechaFactura = f.getFechaEmision().toLocalDate();
                    return !fechaFactura.isBefore(fechaInicio) && !fechaFactura.isAfter(fechaFin);
                })
                .collect(Collectors.toList());
    }

    // Clases internas para estadísticas
    public static class EstadisticaItem implements Serializable {
        private String nombre;
        private Long cantidad;

        public EstadisticaItem() {}

        public EstadisticaItem(String nombre, Long cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Long getCantidad() { return cantidad; }
        public void setCantidad(Long cantidad) { this.cantidad = cantidad; }
    }

    public static class EstadisticaMonetaria implements Serializable {
        private String nombre;
        private BigDecimal monto;

        public EstadisticaMonetaria() {}

        public EstadisticaMonetaria(String nombre, BigDecimal monto) {
            this.nombre = nombre;
            this.monto = monto;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
    }

    public static class OdontologoStats implements Serializable {
        private String nombre;
        private String especialidad;
        private Long totalCitas;
        private BigDecimal ingresoGenerado;

        public OdontologoStats() {}

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
        public Long getTotalCitas() { return totalCitas; }
        public void setTotalCitas(Long totalCitas) { this.totalCitas = totalCitas; }
        public BigDecimal getIngresoGenerado() { return ingresoGenerado; }
        public void setIngresoGenerado(BigDecimal ingresoGenerado) { this.ingresoGenerado = ingresoGenerado; }
    }

    public static class TratamientoStats implements Serializable {
        private String nombre;
        private Long vecesRealizado;
        private BigDecimal ingresoGenerado;

        public TratamientoStats() {}

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Long getVecesRealizado() { return vecesRealizado; }
        public void setVecesRealizado(Long vecesRealizado) { this.vecesRealizado = vecesRealizado; }
        public BigDecimal getIngresoGenerado() { return ingresoGenerado; }
        public void setIngresoGenerado(BigDecimal ingresoGenerado) { this.ingresoGenerado = ingresoGenerado; }
    }

    // Getters y Setters
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getPeriodoSeleccionado() { return periodoSeleccionado; }
    public void setPeriodoSeleccionado(String periodoSeleccionado) { this.periodoSeleccionado = periodoSeleccionado; }

    public long getTotalCitas() { return totalCitas; }
    public long getTotalPacientes() { return totalPacientes; }
    public long getTotalUsuarios() { return totalUsuarios; }
    public BigDecimal getIngresoTotal() { return ingresoTotal != null ? ingresoTotal : BigDecimal.ZERO; }
    public BigDecimal getIngresoPendiente() { return ingresoPendiente != null ? ingresoPendiente : BigDecimal.ZERO; }

    public List<EstadisticaItem> getCitasPorEstado() { return citasPorEstado != null ? citasPorEstado : new ArrayList<>(); }
    public List<EstadisticaItem> getCitasPorOdontologo() { return citasPorOdontologo != null ? citasPorOdontologo : new ArrayList<>(); }
    public List<EstadisticaItem> getCitasPorMes() { return citasPorMes != null ? citasPorMes : new ArrayList<>(); }

    public List<EstadisticaMonetaria> getFacturasPorEstado() { return facturasPorEstado != null ? facturasPorEstado : new ArrayList<>(); }
    public List<EstadisticaMonetaria> getIngresosPorMes() { return ingresosPorMes != null ? ingresosPorMes : new ArrayList<>(); }
    public List<EstadisticaMonetaria> getIngresosPorOdontologo() { return ingresosPorOdontologo != null ? ingresosPorOdontologo : new ArrayList<>(); }

    public List<OdontologoStats> getTopOdontologos() { return topOdontologos != null ? topOdontologos : new ArrayList<>(); }
    public List<TratamientoStats> getTopTratamientos() { return topTratamientos != null ? topTratamientos : new ArrayList<>(); }
    public List<Citas> getCitasPorPeriodo() { return citasPorPeriodo != null ? citasPorPeriodo : new ArrayList<>(); }
    public List<Facturas> getFacturasPorPeriodo() { return facturasPorPeriodo != null ? facturasPorPeriodo : new ArrayList<>(); }
}