package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.UUID;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3Client s3Client;

    @Inject
    private AppConfig appConfig;

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

            // ✅ CORREGIDO: Usar RequestBody.fromBytes con el tamaño correcto
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
            // Intenta listar buckets para verificar conexión
            s3Client.listBuckets();

            // Verifica específicamente que nuestro bucket existe
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

    // ✅ MÉTODO NUEVO - ESTE ES EL QUE FALTABA
    public String getBucketName() {
        return appConfig.getBucketName();
    }
}