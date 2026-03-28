-- =============================================================
-- Script de referencia - Sistema Odontologico (PostgreSQL)
-- Hibernate cria as tabelas automaticamente com ddl-auto=update
-- Este script serve como documentacao e para deploys manuais
-- =============================================================

CREATE TABLE IF NOT EXISTS pacientes (
    id                      BIGSERIAL       PRIMARY KEY,
    numero                  INTEGER         UNIQUE,
    nome                    VARCHAR(255)    NOT NULL,
    residencia              VARCHAR(255),
    endereco_completo       VARCHAR(255),
    profissao               VARCHAR(255),
    data_nascimento         DATE,
    nacionalidade           VARCHAR(255),
    indicado_por            VARCHAR(255),
    inicio_tratamento       DATE,
    termino_tratamento      DATE,
    interrupcao_tratamento  DATE,
    telefone                VARCHAR(255),
    telefone_secundario     VARCHAR(255),
    estado_civil            VARCHAR(50),
    dlne                    BOOLEAN,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_pacientes_nome ON pacientes(nome);
CREATE INDEX IF NOT EXISTS idx_pacientes_numero ON pacientes(numero);

-- -----------------------------------------------------------

CREATE TABLE IF NOT EXISTS dados_dentes (
    id              BIGSERIAL       PRIMARY KEY,
    paciente_id     BIGINT          NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    numero_dente    INTEGER         NOT NULL,
    cor             VARCHAR(255),
    escurecimento   VARCHAR(255),
    forma           VARCHAR(255),
    observacoes     VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_dados_dentes_paciente_id ON dados_dentes(paciente_id);

-- -----------------------------------------------------------

CREATE TABLE IF NOT EXISTS anotacoes (
    id              BIGSERIAL       PRIMARY KEY,
    paciente_id     BIGINT          NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    data_anotacao   DATE,
    conteudo        TEXT            NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_anotacoes_paciente_id ON anotacoes(paciente_id);
CREATE INDEX IF NOT EXISTS idx_anotacoes_data ON anotacoes(data_anotacao);

-- -----------------------------------------------------------

CREATE TABLE IF NOT EXISTS radiografias (
    id                  BIGSERIAL       PRIMARY KEY,
    paciente_id         BIGINT          NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    data_realizacao     DATE,
    descricao           VARCHAR(255),
    caminho_arquivo     VARCHAR(255),
    tipo_radiografia    VARCHAR(50),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_radiografias_paciente_id ON radiografias(paciente_id);

-- -----------------------------------------------------------

CREATE TABLE IF NOT EXISTS fichas_clinicas (
    id                      BIGSERIAL       PRIMARY KEY,
    paciente_id             BIGINT          NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    data                    DATE            NOT NULL,
    numero_dente            INTEGER,
    observacoes_clinicas    TEXT,
    historico               TEXT,
    deve                    NUMERIC(10, 2),
    haver                   NUMERIC(10, 2),
    saldo                   NUMERIC(10, 2),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_fichas_clinicas_paciente_id ON fichas_clinicas(paciente_id);
CREATE INDEX IF NOT EXISTS idx_fichas_clinicas_data ON fichas_clinicas(data);
