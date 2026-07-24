package com.odonto.api.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Escolhe a implementação de storage pela propriedade {@code app.storage.type}.
 * Um valor desconhecido derruba o boot em vez de cair silenciosamente no disco
 * efêmero do container.
 */
@Configuration
public class StorageConfig {

    private static final Logger log = LoggerFactory.getLogger(StorageConfig.class);

    @Value("${app.storage.type:local}")
    private String tipo;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.storage.supabase.url:}")
    private String supabaseUrl;

    @Value("${app.storage.supabase.bucket:radiografias}")
    private String supabaseBucket;

    @Value("${app.storage.supabase.service-key:}")
    private String supabaseServiceKey;

    @Bean
    public FileStorage fileStorage() {
        return switch (tipo.toLowerCase()) {
            case "local" -> {
                log.info("Storage de arquivos: disco local em '{}'", uploadDir);
                yield new LocalFileStorage(uploadDir);
            }
            case "supabase" -> {
                log.info("Storage de arquivos: Supabase Storage, bucket '{}'", supabaseBucket);
                yield new SupabaseFileStorage(supabaseUrl, supabaseBucket, supabaseServiceKey);
            }
            default -> throw new IllegalStateException(
                    "app.storage.type inválido: '" + tipo + "'. Use 'local' ou 'supabase'.");
        };
    }
}
