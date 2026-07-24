package com.odonto.api.storage;

/** Falha ao gravar, ler ou remover um arquivo do storage. */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
