-- =============================================================
-- Odonto API - Setup do banco de dados PostgreSQL
-- Execute este script conectado como superusuário (postgres)
-- Exemplo: psql -U postgres -f setup.sql
-- =============================================================

-- 1. Criar o usuário
CREATE USER odonto_user WITH PASSWORD '123456';

-- 2. Criar o banco
CREATE DATABASE odonto_db
    OWNER = odonto_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'pt_BR.UTF-8'
    LC_CTYPE = 'pt_BR.UTF-8'
    TEMPLATE = template0;

-- 3. Conceder privilégios
GRANT ALL PRIVILEGES ON DATABASE odonto_db TO odonto_user;

-- =============================================================
-- Após criar o banco, conecte nele e rode:
-- \c odonto_db
-- GRANT ALL ON SCHEMA public TO odonto_user;
-- =============================================================
