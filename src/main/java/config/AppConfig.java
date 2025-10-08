package config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@ApplicationScoped
public class AppConfig {

    private Properties properties;

    public AppConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("✅ config.properties cargado correctamente");

                // DEBUG: Verificar contenido
                System.out.println("🔍 Propiedades cargadas:");
                System.out.println("📍 aws.region: " + getRegion());
                System.out.println("📍 aws.s3.bucket: " + getBucketName());
                System.out.println("📍 aws.access.key: " + (getAccessKey() != null ? "***PRESENTE***" : "NULL"));
                System.out.println("📍 aws.secret.key: " + (getSecretKey() != null ? "***PRESENTE***" : "NULL"));

            } else {
                System.err.println("❌ config.properties NO encontrado");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    @Produces
    @ApplicationScoped
    public S3Client s3Client() {
        try {
            String accessKey = getAccessKey();
            String secretKey = getSecretKey();
            String region = getRegion();

            System.out.println("🔑 Creando S3Client con:");
            System.out.println("📍 Region: " + region);
            System.out.println("📍 Bucket: " + getBucketName());
            System.out.println("📍 Access Key: " + (accessKey != null ? "***" + accessKey.substring(accessKey.length() - 4) : "NULL"));
            System.out.println("📍 Secret Key: " + (secretKey != null && !secretKey.isEmpty() ? "***PRESENTE***" : "NULL"));

            if (accessKey == null || accessKey.trim().isEmpty()) {
                throw new RuntimeException("Access Key está vacía o no existe");
            }
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new RuntimeException("Secret Key está vacía o no existe");
            }

            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

            S3Client client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();

            System.out.println("✅ S3Client creado exitosamente");
            return client;

        } catch (Exception e) {
            System.err.println("❌ Error creando S3Client: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creando S3Client: " + e.getMessage(), e);
        }
    }

    public String getRegion() {
        return properties.getProperty("aws.region", "us-east-2");
    }

    public String getBucketName() {
        return properties.getProperty("aws.s3.bucket", "project-mgmt-files-cabre-2025");
    }

    // ✅ CORREGIDO: Buscar por el nombre de la propiedad, no por el valor
    public String getAccessKey() {
        return properties.getProperty("aws.access.key");  // ✅ Busca la propiedad "aws.access.key"
    }

    // ✅ CORREGIDO: Buscar por el nombre de la propiedad, no por el valor
    public String getSecretKey() {
        return properties.getProperty("aws.secret.key");  // ✅ Busca la propiedad "aws.secret.key"
    }
}