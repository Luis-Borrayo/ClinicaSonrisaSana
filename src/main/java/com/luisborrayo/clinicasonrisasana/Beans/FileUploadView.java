package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.ArchivoS3;
import com.luisborrayo.clinicasonrisasana.services.S3Service;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class FileUploadView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private S3Service s3Service;

    private UploadedFile file;
    private UploadedFiles files;
    private String uploadedFileUrl;
    private String message;
    private List<ArchivoS3> archivosS3;
    private ArchivoS3 archivoSeleccionado;

    @PostConstruct
    public void init() {
        System.out.println("🔵 FileUploadView inicializado - SessionScoped");
        archivosS3 = new ArrayList<>();
        // Cargar archivos automáticamente al iniciar
        listarArchivosS3();
    }

    public void listarArchivosS3() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("📂 FileUploadView - listarArchivosS3() ejecutado");

        try {
            // Verificar que el servicio esté inyectado
            if (s3Service == null) {
                System.err.println("❌ S3Service es NULL - Verificar inyección CDI");
                this.archivosS3 = new ArrayList<>();
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Servicio S3 no disponible");
                return;
            }

            System.out.println("✅ S3Service disponible, llamando a listar archivos...");
            List<ArchivoS3> listaTemp = s3Service.listarArchivos();

            if (listaTemp == null) {
                System.out.println("⚠️ El servicio retornó NULL, inicializando lista vacía");
                this.archivosS3 = new ArrayList<>();
            } else {
                this.archivosS3 = listaTemp;
                System.out.println("✅ Archivos obtenidos: " + archivosS3.size());

                // DEBUG: Mostrar nombres de archivos
                for (ArchivoS3 archivo : archivosS3) {
                    System.out.println("   📄 " + archivo.getNombre() +
                            " - Tamaño: " + archivo.getTamanioFormateado() +
                            " - Key: " + archivo.getKey());
                }
            }

            addMessage(FacesMessage.SEVERITY_INFO,
                    "Lista Actualizada",
                    "Se encontraron " + archivosS3.size() + " archivos");

        } catch (Exception e) {
            System.err.println("❌ Error crítico en listarArchivosS3:");
            System.err.println("   Tipo: " + e.getClass().getName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();

            this.archivosS3 = new ArrayList<>();

            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al listar",
                    "No se pudieron cargar los archivos: " + e.getMessage());
        }

        System.out.println("═══════════════════════════════════════════");
    }

    public void upload() {
        if (file != null && file.getSize() > 0) {
            try {
                String fileKey = s3Service.uploadFile(
                        file.getInputStream(),
                        file.getFileName(),
                        file.getContentType()
                );

                this.uploadedFileUrl = s3Service.getFileUrl(fileKey);
                this.message = "✅ Archivo '" + file.getFileName() + "' subido exitosamente";

                addMessage(FacesMessage.SEVERITY_INFO,
                        "¡Éxito!",
                        "Archivo subido correctamente a AWS S3");

                // Actualizar la lista después de subir
                listarArchivosS3();

            } catch (IOException e) {
                this.message = "❌ Error leyendo el archivo: " + e.getMessage();
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error",
                        "No se pudo leer el archivo");
                e.printStackTrace();
            } catch (Exception e) {
                this.message = "❌ Error subiendo a S3: " + e.getMessage();
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error S3",
                        "Error conectando con AWS S3");
                e.printStackTrace();
            }
        } else {
            this.message = "⚠️ Por favor selecciona un archivo";
            addMessage(FacesMessage.SEVERITY_WARN,
                    "Advertencia",
                    "Selecciona un archivo para subir");
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

            addMessage(FacesMessage.SEVERITY_INFO,
                    "¡Subida exitosa!",
                    "Archivo subido inmediatamente a S3");

            listarArchivosS3();

        } catch (IOException e) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "Error leyendo archivo para subida AJAX");
            e.printStackTrace();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error S3",
                    "Error subiendo a AWS S3 via AJAX");
            e.printStackTrace();
        }
    }

    public void descargarArchivo(ArchivoS3 archivo) {
        try {
            if (archivo == null || archivo.getKey() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Archivo no válido para descargar");
                return;
            }

            System.out.println("📥 Iniciando descarga: " + archivo.getKey());
            InputStream fileStream = s3Service.descargarArchivo(archivo.getKey());

            FacesContext facesContext = FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpServletResponse response =
                    (jakarta.servlet.http.HttpServletResponse) facesContext.getExternalContext().getResponse();

            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + archivo.getNombre() + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            try (InputStream input = fileStream;
                 java.io.OutputStream output = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.flush();
            }

            facesContext.responseComplete();
            System.out.println("✅ Descarga completada: " + archivo.getNombre());

        } catch (Exception e) {
            System.err.println("❌ Error en descarga: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "No se pudo descargar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarArchivo() {
        if (archivoSeleccionado != null) {
            try {
                System.out.println("🗑️ Eliminando archivo: " + archivoSeleccionado.getKey());
                boolean eliminado = s3Service.eliminarArchivo(archivoSeleccionado.getKey());

                if (eliminado) {
                    archivosS3.remove(archivoSeleccionado);
                    addMessage(FacesMessage.SEVERITY_INFO,
                            "Archivo Eliminado",
                            "Archivo '" + archivoSeleccionado.getNombre() + "' eliminado correctamente");
                    archivoSeleccionado = null;
                    System.out.println("✅ Archivo eliminado de la lista");
                } else {
                    addMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No se pudo eliminar el archivo del bucket S3");
                }

            } catch (Exception e) {
                System.err.println("❌ Error eliminando archivo: " + e.getMessage());
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error",
                        "Error eliminando archivo: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            addMessage(FacesMessage.SEVERITY_WARN,
                    "Advertencia",
                    "No hay archivo seleccionado para eliminar");
        }
    }

    public void abrirArchivo(ArchivoS3 archivo) {
        try {
            if (archivo == null || archivo.getKey() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Archivo no válido");
                return;
            }

            String url = s3Service.getFileUrl(archivo.getKey());
            FacesContext context = FacesContext.getCurrentInstance();

            String script = "window.open('" + url + "', '_blank');";
            context.getPartialViewContext().getEvalScripts().add(script);

            System.out.println("🔗 Abriendo archivo: " + archivo.getNombre() + " - URL: " + url);

        } catch (Exception e) {
            System.err.println("❌ Error abriendo archivo: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "No se pudo abrir el archivo: " + e.getMessage());
        }
    }


    public void testS3Connection() {
        try {
            System.out.println("🔍 Probando conexión S3 desde Bean...");
            boolean connected = s3Service.testConnection();
            if (connected) {
                this.message = "✅ CONEXIÓN S3 EXITOSA<br/>" +
                        "• Bucket: " + s3Service.getBucketName() + "<br/>" +
                        "• Estado: Conectado correctamente";

                addMessage(FacesMessage.SEVERITY_INFO,
                        "Conexión Exitosa",
                        "AWS S3 conectado. Bucket: " + s3Service.getBucketName());
            } else {
                this.message = "❌ ERROR: No se pudo conectar con S3";
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error de Conexión",
                        "No se pudo conectar con AWS S3");
            }
        } catch (Exception e) {
            this.message = "❌ ERROR DE CONEXIÓN:<br/>" + e.getMessage();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error Inesperado",
                    "Error probando conexión S3: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para agregar mensajes
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public S3Service getS3Service() {
        return s3Service;
    }

    public void setS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public UploadedFiles getFiles() {
        return files;
    }

    public void setFiles(UploadedFiles files) {
        this.files = files;
    }

    public String getUploadedFileUrl() {
        return uploadedFileUrl;
    }

    public String getMessage() {
        return message;
    }

    public List<ArchivoS3> getArchivosS3() {
        // IMPORTANTE: Siempre retornar una lista, nunca null
        if (archivosS3 == null) {
            archivosS3 = new ArrayList<>();
        }
        return archivosS3;
    }

    public ArchivoS3 getArchivoSeleccionado() {
        return archivoSeleccionado;
    }

    public void setArchivoSeleccionado(ArchivoS3 archivoSeleccionado) {
        this.archivoSeleccionado = archivoSeleccionado;
    }

    public int getTotalArchivos() {
        return archivosS3 != null ? archivosS3.size() : 0;
    }
}