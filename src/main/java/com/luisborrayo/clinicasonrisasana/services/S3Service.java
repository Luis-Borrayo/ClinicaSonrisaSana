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

    // ... (tus métodos existentes permanecen igual)

    public String uploadFile(InputStream fileStream, String fileName, String contentType) {
        try {
            String key = UUID.randomUUID().toString() + "_" + fileName;

            // Leer todo el stream para calcular el tamaño
            byte[] fileBytes = fileStream.readAllBytes();
            long fileSize = fileBytes.length;

            System.out.println("📊 Subiendo archivo: " + fileName +
                    " | Tamaño: " + fileSize + " bytes | Tipo: " + contentType);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(appConfig.getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

            System.out.println("✅ ARCHIVO SUBIDO - Key: " + key +
                    " | Bucket: " + appConfig.getBucketName() +
                    " | Tamaño: " + fileSize + " bytes");
            return key;

        } catch (Exception e) {
            System.err.println("❌ ERROR subiendo archivo: " + e.getMessage());
            throw new RuntimeException("Error subiendo archivo a S3: " + e.getMessage(), e);
        }
    }

    public boolean testConnection() {
        try {
            s3Client.listBuckets();
            try {
                s3Client.listObjectsV2(b -> b.bucket(appConfig.getBucketName()));
                System.out.println("✅ CONEXIÓN S3 EXITOSA - Bucket encontrado: " + appConfig.getBucketName());
                return true;
            } catch (S3Exception e) {
                System.err.println("❌ Bucket no encontrado: " + appConfig.getBucketName());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR DE CONEXIÓN S3: " + e.getMessage());
            return false;
        }
    }

    public String getFileUrl(String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                appConfig.getBucketName(),
                appConfig.getRegion(),
                fileKey);
    }

    public String getBucketName() {
        return appConfig.getBucketName();
    }

    // NUEVOS MÉTODOS PARA LISTAR ARCHIVOS

    public List<ArchivoS3> listarArchivos() {
        List<ArchivoS3> archivos = new ArrayList<>();

        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(appConfig.getBucketName())
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            for (S3Object s3Object : response.contents()) {
                String nombre = extraerNombreArchivo(s3Object.key());
                boolean esCarpeta = s3Object.key().endsWith("/");

                ArchivoS3 archivo = new ArchivoS3(
                        nombre,
                        s3Object.key(),
                        s3Object.size(),
                        s3Object.lastModified(),
                        esCarpeta,
                        null // El content type no viene en la lista
                );

                archivos.add(archivo);
            }

            System.out.println("✅ Listados " + archivos.size() + " archivos del bucket S3");

        } catch (Exception e) {
            System.err.println("❌ Error listando archivos S3: " + e.getMessage());
            throw new RuntimeException("Error listando archivos del bucket S3", e);
        }

        return archivos;
    }

    private String extraerNombreArchivo(String key) {
        if (key.contains("/")) {
            return key.substring(key.lastIndexOf("/") + 1);
        }
        return key;
    }

    public InputStream descargarArchivo(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(appConfig.getBucketName())
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            System.out.println("✅ Descargando archivo: " + key);
            return response;

        } catch (Exception e) {
            System.err.println("❌ Error descargando archivo: " + e.getMessage());
            throw new RuntimeException("Error descargando archivo desde S3", e);
        }
    }

    public boolean eliminarArchivo(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(appConfig.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("✅ Archivo eliminado: " + key);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error eliminando archivo: " + e.getMessage());
            return false;
        }
    }

    public String obtenerNombreArchivoDesdeKey(String key) {
        return extraerNombreArchivo(key);
    }
}