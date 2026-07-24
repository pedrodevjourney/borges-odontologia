package com.odonto.api.storage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Armazenamento em disco local. Usado em desenvolvimento e no cenário de VM com
 * volume persistente. Não use em container de free tier: o disco é efêmero.
 */
public class LocalFileStorage implements FileStorage {

    private final Path baseDir;

    public LocalFileStorage(String uploadDir) {
        this.baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void store(String key, MultipartFile file) {
        Path destino = resolve(key);
        try {
            Files.createDirectories(destino.getParent());
            try (var in = file.getInputStream()) {
                Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Falha ao salvar arquivo: " + e.getMessage(), e);
        }
    }

    @Override
    public StoredFile read(String key) {
        Path arquivo = resolve(key);
        if (!Files.isReadable(arquivo)) {
            throw new StorageException("Arquivo não encontrado no storage: " + key);
        }
        String contentType = "application/octet-stream";
        try {
            String detectado = Files.probeContentType(arquivo);
            if (detectado != null) {
                contentType = detectado;
            }
        } catch (IOException ignored) {
            // mantém o tipo genérico
        }
        return new StoredFile(new FileSystemResource(arquivo), contentType);
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException ignored) {
            // remoção do arquivo é best-effort; o registro no banco é a fonte da verdade
        }
    }

    /** Resolve a chave sob o diretório base, barrando path traversal. */
    private Path resolve(String key) {
        Path resolvido = baseDir.resolve(key).normalize();
        if (!resolvido.startsWith(baseDir)) {
            throw new StorageException("Chave de arquivo inválida: " + key);
        }
        return resolvido;
    }
}
