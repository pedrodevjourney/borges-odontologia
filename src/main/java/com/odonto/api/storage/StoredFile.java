package com.odonto.api.storage;

import org.springframework.core.io.Resource;

/**
 * Arquivo recuperado do storage, pronto para ser devolvido na resposta HTTP.
 *
 * @param resource    conteúdo do arquivo
 * @param contentType tipo declarado no upload, ou {@code application/octet-stream}
 */
public record StoredFile(Resource resource, String contentType) {
}
