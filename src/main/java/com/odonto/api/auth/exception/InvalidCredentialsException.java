package com.odonto.api.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciais inválidas. Verifique seu email e senha.");
    }
}
