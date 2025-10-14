package com.luisborrayo.clinicasonrisasana.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import com.luisborrayo.clinicasonrisasana.services.S3Service;
import com.luisborrayo.clinicasonrisasana.model.ArchivoS3;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class FileUploadView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private S3Service s3Service;

    private UploadedFile file;
    private UploadedFiles files;
    private String uploadedFileUrl;
    private String message;
    private List<ArchivoS3> archivosS3;

    // MÉTODO PARA LISTAR ARCHIVOS
    public void listarArchivosS3() {
        try {
            this.archivosS3 = s3Service.listarArchivos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Lista Actualizada",
                            "Se listaron " + archivosS3.size() + " archivos del bucket S3"));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudieron listar los archivos: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // MÉTODO PARA DESCARGAR ARCHIVO
    public void descargarArchivo(ArchivoS3 archivo) {
        try {
            // Aquí podrías implementar la descarga directa
            // Por ahora, mostramos la URL
            String url = s3Service.getFileUrl(archivo.getKey());

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Descargar Archivo",
                            "URL para descargar: " + url));

            // Para descarga automática, necesitaríamos un método diferente
            System.out.println("📥 Descargar archivo: " + archivo.getNombre() + " | URL: " + url);

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudo preparar la descarga: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // MÉTODO PARA ELIMINAR ARCHIVO
    public void eliminarArchivo(ArchivoS3 archivo) {
        try {
            boolean eliminado = s3Service.eliminarArchivo(archivo.getKey());

            if (eliminado) {
                // Remover de la lista local
                archivosS3.remove(archivo);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo Eliminado",
                                "Archivo '" + archivo.getNombre() + "' eliminado correctamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No se pudo eliminar el archivo '" + archivo.getNombre() + "'"));
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error eliminando archivo: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // GETTER para la lista de archivos
    public List<ArchivoS3> getArchivosS3() {
        if (archivosS3 == null) {
            listarArchivosS3(); // Cargar automáticamente al acceder
        }
        return archivosS3;
    }

    // GETTER para el total de archivos
    public int getTotalArchivos() {
        return archivosS3 != null ? archivosS3.size() : 0;
    }

    // ... (tus métodos existentes upload, handleFileUpload, testS3Connection permanecen igual)

    public void upload() {
        if (file != null && file.getSize() > 0) {
            try {
                String fileKey = s3Service.uploadFile(
                        file.getInputStream(),
                        file.getFileName(),
                        file.getContentType()
                );

                this.uploadedFileUrl = s3Service.getFileUrl(fileKey);
                this.message = "✅ Archivo '" + file.getFileName() + "' subido exitosamente a S3";

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Éxito!",
                                "Archivo subido correctamente a AWS S3"));

                // Actualizar la lista de archivos después de subir
                listarArchivosS3();

            } catch (IOException e) {
                this.message = "❌ Error leyendo el archivo: " + e.getMessage();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No se pudo leer el archivo"));
                e.printStackTrace();
            } catch (Exception e) {
                this.message = "❌ Error subiendo a S3: " + e.getMessage();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error S3",
                                "Error conectando con AWS S3"));
                e.printStackTrace();
            }
        } else {
            this.message = "⚠️ Por favor selecciona un archivo";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia",
                            "Selecciona un archivo para subir"));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();

        try {
            String fileKey = s3Service.uploadFile(
                    uploadedFile.getInputStream(),
                    uploadedFile.getFileName(),
                    uploadedFile.getContentType()
            );

            this.uploadedFileUrl = s3Service.getFileUrl(fileKey);
            this.message = "✅ '" + uploadedFile.getFileName() + "' subido via AJAX";

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Subida AJAX exitosa!",
                            "Archivo subido inmediatamente a S3"));

            // Actualizar la lista de archivos después de subir
            listarArchivosS3();

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error leyendo archivo para subida AJAX"));
            e.printStackTrace();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error S3",
                            "Error subiendo a AWS S3 via AJAX"));
            e.printStackTrace();
        }
    }

    public void testS3Connection() {
        try {
            boolean connected = s3Service.testConnection();
            if (connected) {
                this.message = "✅ CONEXIÓN S3 EXITOSA<br/>" +
                        "• Bucket: " + s3Service.getBucketName() + "<br/>" +
                        "• Región: us-east-2<br/>" +
                        "• Estado: Conectado correctamente";

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Conexión Exitosa",
                                "AWS S3 conectado correctamente. Bucket: " + s3Service.getBucketName()));
            } else {
                this.message = "❌ ERROR: No se pudo conectar con S3";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Conexión",
                                "No se pudo conectar con AWS S3"));
            }
        } catch (Exception e) {
            this.message = "❌ ERROR DE CONEXIÓN:<br/>" + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Inesperado",
                            "Error probando conexión S3: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // Getters y Setters existentes
    public UploadedFile getFile() { return file; }
    public void setFile(UploadedFile file) { this.file = file; }
    public UploadedFiles getFiles() { return files; }
    public void setFiles(UploadedFiles files) { this.files = files; }
    public String getUploadedFileUrl() { return uploadedFileUrl; }
    public String getMessage() { return message; }
}