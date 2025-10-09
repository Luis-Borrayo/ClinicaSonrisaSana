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

import java.io.IOException;
import java.io.Serializable;

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

    // ... (el resto de tus métodos permanecen igual)
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

                System.out.println("🎯 Archivo subido - Key: " + fileKey);
                System.out.println("🌐 URL: " + uploadedFileUrl);

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

            System.out.println("🎯 Subida AJAX - Key: " + fileKey);

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

    // Getters y Setters
    public UploadedFile getFile() { return file; }
    public void setFile(UploadedFile file) { this.file = file; }
    public UploadedFiles getFiles() { return files; }
    public void setFiles(UploadedFiles files) { this.files = files; }
    public String getUploadedFileUrl() { return uploadedFileUrl; }
    public String getMessage() { return message; }
}