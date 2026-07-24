package com.odonto.api.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Armazenamento no Supabase Storage via API REST.
 *
 * <p>Usa o {@link HttpClient} do próprio JDK de propósito: um SDK de S3 acrescenta
 * dezenas de MB de dependências e pressão de heap, e o container de produção roda
 * com 512 MB. O bucket é privado — o arquivo nunca é exposto por URL pública; a
 * API lê o objeto com a service key e repassa o conteúdo na resposta autenticada.
 */
public class SupabaseFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(SupabaseFileStorage.class);

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl;
    private final String bucket;
    private final String serviceKey;

    public SupabaseFileStorage(String url, String bucket, String serviceKey) {
        if (url == null || url.isBlank() || serviceKey == null || serviceKey.isBlank()) {
            throw new IllegalStateException(
                    "Storage do Supabase exige as variáveis STORAGE_URL e STORAGE_SERVICE_KEY");
        }
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        this.bucket = bucket;
        this.serviceKey = serviceKey;
    }

    @Override
    public void store(String key, MultipartFile file) {
        String contentType = file.getContentType() != null
                ? file.getContentType() : "application/octet-stream";
        try (InputStream in = file.getInputStream()) {
            HttpRequest request = authenticated(key)
                    .header("Content-Type", contentType)
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.fromPublisher(
                            HttpRequest.BodyPublishers.ofInputStream(() -> in), file.getSize()))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new StorageException("Supabase Storage recusou o upload (HTTP "
                        + response.statusCode() + "): " + response.body());
            }
        } catch (IOException e) {
            throw new StorageException("Falha ao enviar arquivo para o storage: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StorageException("Upload interrompido", e);
        }
    }

    @Override
    public StoredFile read(String key) {
        try {
            HttpResponse<InputStream> response = http.send(
                    authenticated(key).GET().build(),
                    HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 404) {
                throw new StorageException("Arquivo não encontrado no storage: " + key);
            }
            if (response.statusCode() >= 300) {
                throw new StorageException("Supabase Storage recusou a leitura (HTTP "
                        + response.statusCode() + ")");
            }

            String contentType = response.headers().firstValue("content-type")
                    .orElse("application/octet-stream");
            return new StoredFile(new InputStreamResource(response.body()), contentType);
        } catch (IOException e) {
            throw new StorageException("Falha ao ler arquivo do storage: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StorageException("Leitura interrompida", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            HttpResponse<Void> response = http.send(
                    authenticated(key).DELETE().build(),
                    HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() >= 300 && response.statusCode() != 404) {
                log.warn("Storage não removeu o objeto {} (HTTP {})", key, response.statusCode());
            }
        } catch (IOException e) {
            // best-effort: o registro no banco já foi removido
            log.warn("Falha ao remover {} do storage: {}", key, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private HttpRequest.Builder authenticated(String key) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/storage/v1/object/" + bucket + "/" + encodePath(key)))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + serviceKey);
    }

    /** Codifica cada segmento do caminho, preservando as barras. */
    private static String encodePath(String key) {
        return Arrays.stream(key.replace('\\', '/').split("/"))
                .map(segmento -> URLEncoder.encode(segmento, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }
}
