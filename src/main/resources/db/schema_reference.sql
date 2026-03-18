-- =============================================================
-- Odonto API - Schema de referência
-- Gerado automaticamente pelo Hibernate (ddl-auto=update).
-- Este arquivo é apenas para documentação e referência.
-- =============================================================

-- Tabela de administradores
CREATE TABLE IF NOT EXISTS admins (
    id         BIGSERIAL    PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela de tokens revogados (blacklist de logout)
CREATE TABLE IF NOT EXISTS revoked_tokens (
    id         BIGSERIAL    PRIMARY KEY,
    jti        VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_revoked_tokens_jti        ON revoked_tokens (jti);
CREATE INDEX IF NOT EXISTS idx_revoked_tokens_expires_at ON revoked_tokens (expires_at);
