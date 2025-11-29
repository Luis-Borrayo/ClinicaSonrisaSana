package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.config.AppConfig;
import com.luisborrayo.clinicasonrisasana.model.ArchivoS3;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3Client s3Client;

    @Inject
    private AppConfig appConfig;

    /**
     * Sube un archivo a S3
     */
    public String uploadFile(InputStream fileStream, String fileName, String contentType) {
        try {
            String key = UUID.randomUUID().toString() + "_" + fileName;
            byte[] fileBytes = fileStream.readAllBytes();
            long fileSize = fileBytes.length;

            System.out.println("📊 Subiendo: " + fileName + " (" + fileSize + " bytes)");

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(appConfig.getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

            System.out.println("✅ Archivo subido: " + key);
            return key;

        } catch (Exception e) {
            System.err.println("❌ Error subiendo archivo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error subiendo archivo a S3: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos los archivos del bucket S3 - VERSIÓN OPTIMIZADA
     */
    public List<ArchivoS3> listarArchivos() {
        List<ArchivoS3> archivos = new ArrayList<>();

        try {
            String bucketName = appConfig.getBucketName();
            System.out.println("📂 S3Service - Listando archivos del bucket: " + bucketName);

            // Crear petición
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            // Ejecutar petición
            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            // Verificar si hay contenido
            if (response.contents() == null || response.contents().isEmpty()) {
                System.out.println("ℹ️ El bucket '" + bucketName + "' está vacío");
                return archivos;
            }

            System.out.println("📊 Encontrados " + response.contents().size() + " objetos en S3");

            // Procesar cada objeto
            for (S3Object s3Object : response.contents()) {
                String nombre = extraerNombreArchivo(s3Object.key());
                boolean esCarpeta = s3Object.key().endsWith("/");

                // Omitir "carpetas" vacías de S3
                if (esCarpeta && s3Object.size() == 0) {
                    continue;
                }

                ArchivoS3 archivo = new ArchivoS3(
                        nombre,
                        s3Object.key(),
                        s3Object.size(),
                        s3Object.lastModified(),
                        esCarpeta,
                        null
                );

                archivos.add(archivo);
                System.out.println("   📄 " + nombre + " (" + formatSize(s3Object.size()) + ")");
            }

            System.out.println("✅ S3Service - Lista completada: " + archivos.size() + " archivos");

        } catch (S3Exception e) {
            System.err.println("❌ Error S3Exception:");
            System.err.println("   Código: " + e.awsErrorDetails().errorCode());
            System.err.println("   Mensaje: " + e.awsErrorDetails().errorMessage());
            System.err.println("   Status: " + e.statusCode());

            // Relanzar para que el bean pueda manejarlo
            throw new RuntimeException("Error S3: " + e.awsErrorDetails().errorMessage(), e);

        } catch (Exception e) {
            System.err.println("❌ Error listando archivos:");
            System.err.println("   Tipo: " + e.getClass().getName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error listando archivos: " + e.getMessage(), e);
        }

        return archivos;
    }

    /**
     * Extrae nombre del archivo desde la key
     */
    private String extraerNombreArchivo(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        if (key.contains("/")) {
            return key.substring(key.lastIndexOf("/") + 1);
        }
        return key;
    }

    /**
     * Formatea el tamaño del archivo
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * Descarga un archivo desde S3
     */
    public InputStream descargarArchivo(String key) {
        try {
            System.out.println("📥 Descargando: " + key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(appConfig.getBucketName())
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            System.out.println("✅ Descarga exitosa");
            return response;

        } catch (Exception e) {
            System.err.println("❌ Error descargando: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error descargando archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un archivo de S3
     */
    public boolean eliminarArchivo(String key) {
        try {
            System.out.println("🗑️ Eliminando: " + key);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(appConfig.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("✅ Eliminado correctamente");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error eliminando: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene URL pública del archivo
     */
    public String getFileUrl(String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                appConfig.getBucketName(),
                appConfig.getRegion(),
                fileKey);
    }

    /**
     * Prueba conexión con S3
     */
    public boolean testConnection() {
        try {
            System.out.println("🔍 Probando conexión S3...");

            s3Client.listBuckets();
            System.out.println("✅ Conexión exitosa");

            try {
                String bucketName = appConfig.getBucketName();
                ListObjectsV2Request request = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .maxKeys(1)
                        .build();

                s3Client.listObjectsV2(request);
                System.out.println("✅ Bucket accesible: " + bucketName);
                return true;

            } catch (S3Exception e) {
                System.err.println("❌ Error accediendo al bucket: " + e.awsErrorDetails().errorMessage());
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getBucketName() {
        return appConfig.getBucketName();
    }
}