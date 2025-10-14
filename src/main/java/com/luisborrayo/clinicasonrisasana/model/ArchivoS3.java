package com.luisborrayo.clinicasonrisasana.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.io.Serializable;

public class ArchivoS3 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String key;
    private long tamanio;
    private Instant ultimaModificacion;
    private boolean folder;
    private String contentType;

    public ArchivoS3() {}

    public ArchivoS3(String nombre, String key, long tamanio, Instant ultimaModificacion, boolean folder, String contentType) {
        this.nombre = nombre;
        this.key = key;
        this.tamanio = tamanio;
        this.ultimaModificacion = ultimaModificacion;
        this.folder = folder;
        this.contentType = contentType;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public long getTamanio() { return tamanio; }
    public void setTamanio(long tamanio) { this.tamanio = tamanio; }

    public Instant getUltimaModificacion() { return ultimaModificacion; }
    public void setUltimaModificacion(Instant ultimaModificacion) { this.ultimaModificacion = ultimaModificacion; }

    public boolean isFolder() { return folder; }
    public void setFolder(boolean folder) { this.folder = folder; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    // MÉTODO CORREGIDO: Devuelve Date en lugar de Instant
    public Date getFechaModificacion() {
        if (ultimaModificacion == null) {
            return null;
        }
        return Date.from(ultimaModificacion);
    }

    // Método helper para formatear el tamaño
    public String getTamanioFormateado() {
        if (folder) return "-";
        if (tamanio < 1024) return tamanio + " B";
        if (tamanio < 1024 * 1024) return String.format("%.1f KB", tamanio / 1024.0);
        if (tamanio < 1024 * 1024 * 1024) return String.format("%.1f MB", tamanio / (1024.0 * 1024.0));
        return String.format("%.1f GB", tamanio / (1024.0 * 1024.0 * 1024.0));
    }

    // Método para obtener la fecha como String formateado
    public String getFechaFormateada() {
        if (ultimaModificacion == null) {
            return "-";
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(ultimaModificacion, ZoneId.systemDefault());
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}