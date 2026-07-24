package com.odonto.api.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstração de armazenamento de arquivos (radiografias).
 *
 * <p>Em desenvolvimento a implementação grava em disco local. Em produção o
 * filesystem do container é efêmero — todo deploy o apaga — então a implementação
 * de produção envia para um bucket externo. A chave devolvida por
 * {@link #store} é o que fica persistido em {@code Radiografia.caminhoArquivo}.
 */
public interface FileStorage {

    /**
     * Grava o arquivo sob a chave informada.
     *
     * @param key    caminho lógico dentro do storage (ex.: {@code radiografias/7/uuid.png})
     * @param file   arquivo recebido na requisição
     */
    void store(String key, MultipartFile file);

    /** Recupera o arquivo. Lança {@link StorageException} se não existir. */
    StoredFile read(String key);

    /** Remove o arquivo, ignorando silenciosamente se ele já não existir. */
    void delete(String key);
}
