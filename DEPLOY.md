# Plano de Produção — Sistema OdontoAPI (custo R$ 0,00)

**Data:** 2026-07-24
**Objetivo:** colocar o sistema em produção, gratuitamente, de forma que a dentista consiga
usar na clínica no dia a dia — sem perder dados, sem instalar nada, acessando pelo navegador.

Stack atual verificada no repositório:

| Camada | Tecnologia |
|---|---|
| API | Spring Boot 4.0.3, Java 25, Maven, JPA/Hibernate, Spring Security + JWT |
| Banco | PostgreSQL |
| Front | React 19 + Vite 8 + Tailwind 4 (SPA estática) |
| Arquivos | Radiografias gravadas em disco local (`uploads/`) |

---

## 1. Arquitetura recomendada (Plano A — sem cartão de crédito)

```
  Navegador da dentista
          │
          ├──── https://odonto.pages.dev ──────────► Cloudflare Pages
          │        (SPA React, build do Vite)        grátis, sem hibernação,
          │                                          banda ilimitada
          │
          └──── https://odonto-api.onrender.com ───► Render Web Service (Docker)
                   /auth, /pacientes, /consultas...   512 MB RAM, grátis
                              │
                              ├──────────► Neon Postgres (grátis, 0,5 GB, não expira)
                              │
                              └──────────► Supabase Storage (grátis, 1 GB)
                                           radiografias (bucket privado, API REST)

  cron-job.org (grátis) ──► GET /health a cada 10 min em horário comercial
                            (evita a hibernação de 15 min do Render)

  GitHub Actions (grátis) ──► pg_dump diário → backup no Storage
```

### Por que cada serviço (limites verificados em julho/2026)

| Serviço | O que entrega grátis | Pegadinha | Mitigação |
|---|---|---|---|
| **Cloudflare Pages** (front) | Build + CDN global, banda ilimitada, HTTPS, sem hibernação, 500 builds/mês | precisa de regra de rewrite para SPA (React Router) | arquivo `_redirects` com `/* /index.html 200` (já criado) |
| **Render** (API) | 512 MB RAM / 0.1 CPU, deploy por Docker, HTTPS, 750 h de instância por mês | **hiberna após 15 min sem tráfego**; cold start de JVM 30–60 s | ping externo em horário de atendimento (ver §4) |
| **Neon** (Postgres) | 0,5 GB por projeto, **não expira**, uso comercial permitido, 100 CU-h/mês | compute dorme após 5 min de ociosidade (religa em ~1 s) e o scale-to-zero **não pode ser desligado** no free | ver orçamento de CU-h abaixo |
| **Supabase Storage** (radiografias) | 1 GB de arquivos, API REST simples, sem cartão | projeto é **pausado após 7 dias sem requisição** | uso diário da clínica já evita |
| **cron-job.org** | agendamentos HTTP grátis, granularidade de 1 min | — | — |
| **GitHub Actions** | 2.000 min/mês em repo privado (ilimitado em público) | agendamento é impreciso e pode atrasar | backup diário tolera atraso |

### O que foi descartado, e por quê

- **Render Postgres grátis** — expira **30 dias após a criação** e é deletado depois de 14 dias de
  carência. Inviável para uma clínica real. É por isso que o banco fica no Neon.
- **Fly.io** — não há mais free tier para contas novas (só trial de 2 VM-horas / 7 dias).
- **Koyeb** — free tier fechado para novos usuários após a aquisição pela Mistral (início de 2026).
- **Heroku** — sem free tier desde 2022.

### Orçamento de horas — os dois limites que se somam

**Render: 750 h de instância por mês, por workspace.** Um mês tem ~744 h — ou seja, **cabe
exatamente um serviço ligado 24/7, e nada mais**. Não crie um segundo serviço grátis no mesmo
workspace.

**Neon: 100 CU-h por projeto por mês** = cerca de **400 h de compute 0,25 CU (~1 GB RAM)**
ligado. É esse o teto que aperta primeiro.

Por isso o keep-alive é restrito a 07h–21h, seg–sáb: 14 h × 26 dias ≈ **364 h**, dentro dos dois
orçamentos (Render sobra muito; Neon fica com ~9% de margem). O `/health` do keep-alive **não
toca no banco** justamente para não queimar CU-h do Neon quando ninguém está usando o sistema —
o compute só acorda com uso real. Se decidir deixar 24/7, o Neon estoura no meio do mês e o banco
**para** (não fica lento — para). Se precisar de mais janela, reduza para 08h–20h ou migre para
o Plano B.

### Por que Ohio e não São Paulo

O Neon **tem** região em São Paulo. O Render **não**: no free tier existem apenas Oregon,
Ohio, Virginia, Frankfurt e Singapura. Como a API tem que ficar no Render, o banco vai
para onde a API está, e não o contrário. O motivo é aritmético:

| Trecho | Quantas vezes por tela | Custo se ficar longe |
|---|---|---|
| Navegador → API | **1** (por requisição) | ~120 ms, uma vez |
| API → banco | **5 a 15** (uma por query da tela) | ~180 ms **× cada query** |

Banco em São Paulo + API em Ohio = cada query atravessa ~9.500 km. Uma tela do prontuário
que faz 10 queries pagaria ~1,8 s só de rede. Com os dois em Ohio, a conversa API↔banco cai
para ~1 ms e sobra apenas o trecho navegador→API (~120 ms, uma vez) — e o front nem sofre
isso, porque o Cloudflare Pages serve a SPA de um nó no Brasil.

**Se latência nacional for requisito**, a saída não é misturar regiões: é o Plano B. A Oracle
Cloud tem região `sa-saopaulo-1` no Always Free, então API + banco + arquivos ficariam todos
em São Paulo, com ~10 ms entre eles e sem cold start. O preço é o cartão para verificação e
a operação por sua conta (§2) — e vale saber que a capacidade de ARM (Ampere A1) em São Paulo
é notoriamente disputada: é comum receber "Out of capacity" e ter que tentar de novo por dias.
A região é escolhida no cadastro e **não pode ser trocada depois**.

---

## 2. Plano B — Oracle Cloud "Always Free" (mais profissional, exige cartão para verificação)

Uma VM ARM sempre ligada, rodando tudo em Docker Compose:

```
VM Ampere A1 (2 OCPU / 12 GB RAM / até 200 GB de disco) — Always Free
 ├── Caddy        → HTTPS automático (Let's Encrypt) + serve o front estático
 ├── odonto-api   → Spring Boot
 ├── postgres     → volume persistente, sem limite de 0,5 GB
 └── volume       → uploads/radiografias (disco de verdade, sem object storage)
```

**Ganhos:** zero cold start, sem limite de 0,5 GB de banco, radiografias em disco persistente
(nenhuma mudança de código nos uploads), backup local com `pg_dump` + `cron`.

**Custos escondidos:** exige cartão para verificação de identidade (não é cobrado); a Oracle
**reduziu o Always Free de 4 OCPU/24 GB para 2 OCPU/12 GB em 15/06/2026**, sem aviso — pode
reduzir de novo; instâncias ociosas podem ser reclamadas; e você passa a ser o responsável por
patch de SO, HTTPS, firewall e backup.

**Recomendação:** comece pelo Plano A (nada a perder, sem cartão, 1 dia de trabalho). Migre para
o Plano B quando o cold start ou o limite de 0,5 GB incomodar — a API já estará containerizada,
então a migração é só apontar variáveis de ambiente.

---

## 3. Bloqueios de código — TODOS APLICADOS ✅

Estado em 24/07/2026: as sete correções abaixo estão implementadas e verificadas
localmente (build, migration em banco limpo, boot com perfil `prod`, smoke test dos
endpoints). O que sobra é a configuração nos painéis dos provedores — §5.

O B4 tem duas camadas de verificação: 3 testes automatizados contra um servidor HTTP
stub (`SupabaseFileStorageTest` — método, `Authorization`, `x-upsert`, `Content-Length`,
streaming e 404) **e** um teste manual ponta a ponta contra o Supabase real, incluindo
simulação de redeploy com disco vazio — detalhes no Passo 3 do §5.

| # | O que era | Como ficou |
|---|---|---|
| B1 | CORS fixo em `localhost:5173` | `app.cors.allowed-origins` por env; recusa boot com curinga (`SecurityConfig.java`) |
| B2 | Segredo do JWT com fallback no repo | `${JWT_SECRET}` sem default — boot falha se faltar (verificado) |
| B3 | `ddl-auto=update` | Flyway com `V1__baseline.sql` + `ddl-auto=validate` |
| B4 | Radiografias em disco efêmero | `FileStorage` com implementações `local` e `supabase` (REST via HttpClient do JDK, sem SDK de S3 — heap de 512 MB) |
| B5 | Nenhum endpoint público de health | `GET /health`, liberado, sem tocar no banco |
| B6 | Sem Dockerfile | Multi-stage Java 25, usuário sem privilégio, flags de heap para 512 MB |
| B7 | Front sem env de produção | `.env.production` + `public/_redirects` (SPA) |

Extras que apareceram durante a implementação:

- **O build do front estava quebrado** (`npm run build` falhava com 11 erros de
  TypeScript pré-existentes) — o Cloudflare Pages simplesmente recusaria o deploy.
  Causa raiz: o wrapper `FormField` do shadcn (`components/ui/form.tsx`) não
  repassava o genérico `TTransformedValues` do react-hook-form, o que quebra
  qualquer schema com `z.coerce`. Corrigido no wrapper + tipagem dos dois
  formulários afetados. Build passa.
- **Spring Boot 4 modularizou as autoconfigurações**: só `flyway-core` no classpath
  não ativa nada; é preciso o módulo `org.springframework.boot:spring-boot-flyway`.
- **`spring.main.lazy-initialization` foi deliberadamente descartada** em produção:
  ela pode adiar o initializer do Flyway para depois da validação do Hibernate e o
  boot quebra com "missing table".
- Os arquivos `src/main/resources/db/*.sql` (schema_reference, setup) continuam no
  repo como documentação histórica, mas **não são mais a referência** — o schema é
  o que está em `db/migration/`.

### Detalhamento (o que cada bloqueio era)

- [x] **B1 — CORS fixo em `localhost:5173`**
      `odonto_api/src/main/java/com/odonto/api/config/SecurityConfig.java:52`
      Hoje só aceita o front local; em produção toda chamada é bloqueada pelo navegador.
      Vira `app.cors.allowed-origins` por variável de ambiente.
      Atenção: com `allowCredentials(true)` **não é permitido** usar `*`.

- [x] **B2 — Segredo do JWT com fallback embutido no repositório**
      `odonto_api/src/main/resources/application.properties:11`
      `${JWT_SECRET:vLrsWGqt...}` — se a variável não for configurada em produção, a aplicação
      sobe silenciosamente com um segredo que está versionado no Git. Qualquer pessoa com acesso
      ao código forja um token e lê prontuários. Trocar por `${JWT_SECRET}` (sem default), para
      falhar no boot se estiver ausente.

- [x] **B3 — `spring.jpa.hibernate.ddl-auto=update` em banco com prontuário**
      `application.properties:6`. O Hibernate alterando schema sozinho num banco de produção é
      pedido de perda de dados (e ele nunca remove/renomeia direito). Baseline do
      `src/main/resources/db/schema_reference.sql` no **Flyway** e `ddl-auto=validate` em prod.

- [x] **B4 — Radiografias gravadas em disco efêmero** ⚠️ *o mais grave*
      `odonto_api/src/main/java/com/odonto/api/paciente/service/PacienteService.java:184`
      grava em `uploads/radiografias/<id>/`. No Render (e em qualquer container de free tier) o
      filesystem é **efêmero**: a cada deploy ou hibernação, **todas as radiografias são
      apagadas** — e o registro no banco continua apontando para um arquivo que não existe.
      Solução no Plano A: subir para o Supabase Storage (S3-compatível) e guardar a chave do
      objeto em `caminhoArquivo`. Escopo real dessa mudança: adicionar a dependência
      `software.amazon.awssdk:s3` ao `pom.xml` (o projeto não tem SDK de S3 hoje — e o SDK não é
      leve num heap de 512 MB; a alternativa mais enxuta é o cliente MinIO), trocar
      `Files.copy`/`Files.deleteIfExists` por `putObject`/`deleteObject` e gerar **URL assinada de
      curta duração** para o download, já que o bucket é privado. Os `caminhoArquivo` que já estão
      no banco de desenvolvimento viram chaves inválidas depois da migração — sem problema, são
      dados de demonstração. No Plano B: montar volume persistente (sem mudança de código).

- [x] **B5 — Não existe endpoint público de health**
      Só `/auth/login` e `/error` são `permitAll`. O ping do keep-alive e o health check do
      Render precisam de um `GET /health` liberado (leve, sem tocar no banco).

- [x] **B6 — Não existe `Dockerfile`**
      Nenhum arquivo de deploy no repo. Como o projeto está em Java 25 / Spring Boot 4.0.3
      (muito recente), **não confie na detecção automática de build** do provedor — o
      Dockerfile multi-stage abaixo é a rota segura.

- [x] **B7 — Front sem `.env.production`**
      `VITE_API_BASE_URL` é lido em build time (`src/core/api.ts:17`). Sem ele, o build de
      produção aponta para a raiz do próprio site e nada funciona.

### Dockerfile sugerido (`odonto_api/Dockerfile`)

```dockerfile
# ---- build ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

O arquivo real está em `odonto_api/Dockerfile` (com usuário sem privilégio e camada de
dependências separada). Spring Boot em 512 MB é apertado, mas passa com
`MaxRAMPercentage=70` e `SerialGC` — o jar gerado tem 61 MB e sobe em ~3,7 s na máquina
local. As duas tags usadas (`maven:3.9-eclipse-temurin-25` e `eclipse-temurin:25-jre`) foram
verificadas e existem no Docker Hub. Não use `-XX:TieredStopAtLevel=1`: melhora o cold start,
mas trava o JIT em C1 e derruba a performance em regime — troca errada num serviço que fica
acordado 14 h por dia.

---

## 4. Variáveis de ambiente

### API (Render → Environment)

| Variável | Exemplo / origem |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://ep-xxx.us-east-2.aws.neon.tech/odonto?sslmode=require` |
| `DB_USER` / `DB_PASSWORD` | credenciais do Neon |
| `JWT_SECRET` | novo, gerado com `openssl rand -base64 64` (**não reusar o do repo**) |
| `ADMIN_NAME` / `ADMIN_EMAIL` / `ADMIN_PASSWORD` | conta inicial da dentista |
| `APP_CORS_ALLOWED_ORIGINS` | `https://odonto.pages.dev` (várias origens separadas por vírgula; `*` é recusado) |
| `STORAGE_TYPE` | `supabase` (já é o padrão do perfil prod; use `local` só na VM do Plano B) |
| `STORAGE_URL` | `https://whxvlsjcseeysxqwsjms.supabase.co` |
| `STORAGE_BUCKET` | `Radiografias` (com **R** maiúsculo — é o nome real do bucket) |
| `STORAGE_SERVICE_KEY` | *service_role key* do Supabase (**nunca** vai para o front) |

O `DemoDataSeeder` já está protegido por `@Profile("dev")` — os pacientes fictícios (Maria Silva
etc.) **não** vão para produção. O `AdminSeeder` roda em qualquer profile e usa as variáveis
acima, então a senha da dentista nunca fica no código.

### Front (Cloudflare Pages → build variables)

| Variável | Valor |
|---|---|
| `VITE_API_BASE_URL` | `https://odonto-api.onrender.com` |
| `VITE_GOOGLE_CLIENT_ID` | client ID do Google Calendar (adicionar o domínio de produção nas *Authorized JavaScript origins*) |

Build command: `npm run build` · Output: `dist`

---

## 5. Passo a passo manual (o que só você pode fazer)

Nenhum destes passos dá para automatizar daqui: todos exigem criar conta, aceitar termos
ou copiar credencial de painel.

### Passo 1 — Higiene dos repositórios (dois repos separados)

O projeto **já está no GitHub**, em dois repositórios independentes:

| Pasta | Repositório | Deploy em |
|---|---|---|
| `odonto_api` | `pedrodevjourney/borges-odontologia` | Render |
| `odonto_app` | `pedrodevjourney/odonto_app` | Cloudflare Pages |

Como cada pasta é a raiz do seu próprio repo, no Render e no Cloudflare o campo
**Root Directory fica vazio** (não `odonto_api`/`odonto_app`), e os workflows moram em
`odonto_api/.github/workflows/` e `odonto_app/.github/workflows/`.

Três pendências antes de publicar:

1. **Os dois repos são PÚBLICOS.** Recomendo trocar para privado
   (*Settings → General → Danger Zone → Change visibility*). O código de um sistema de
   prontuário exposto não é uma catástrofe, mas amplia a superfície de quem estuda como
   atacar o seu ambiente — e o segredo JWT antigo está no histórico.
2. **Havia uma radiografia de paciente versionada** no repo público
   (`uploads/radiografias/3/…webp`). Já removi do índice junto com os 94 arquivos de
   `target/` que estavam sendo rastreados, e criei o `.gitignore` do `odonto_api` (que
   não existia). O arquivo **continua no histórico do Git**: se for imagem de paciente
   real, o caminho seguro é tornar o repo privado e reescrever o histórico
   (`git filter-repo --path uploads --invert-paths`) ou recriar o repositório.
3. **O `JWT_SECRET` antigo está no histórico** e deve ser considerado vazado — por isso
   o Passo 4 gera um novo. Nunca reaproveite aquele valor.

```bash
cd ~/Documents/faculdade/extensao/odonto_api
git add -A && git commit -m "feat: preparar deploy de produção (CORS, JWT, Flyway, storage, Docker)"
git push

cd ../odonto_app
git add -A && git commit -m "feat: build de produção + correções de tipo"
git push
```

### Passo 2 — Neon (banco) — ✅ FEITO E VERIFICADO EM 24/07/2026

Projeto criado em `us-east-2` (Ohio), Postgres **18.4**, banco `neondb`, host
`ep-quiet-breeze-axswdppb.c-4.us-east-2.aws.neon.tech`.

Já rodei a API local apontando para esse banco: o Flyway criou as **11 tabelas**
(`flyway_schema_history` = `1 | baseline | success`) e o `ddl-auto=validate` passou —
ou seja, o schema real bate com as entidades. O admin temporário usado no teste foi
**removido**, então a tabela `admins` está vazia e o primeiro deploy no Render vai criar
a conta da dentista a partir das variáveis `ADMIN_*`.

Instruções originais (para referência, ou se precisar recriar o projeto):

1. neon.com → *Sign up* com o GitHub → **Create project**
2. Região: **AWS US East (Ohio)** — a mesma que você vai escolher no Render (§1)
3. Postgres: o projeto criado em 24/07/2026 saiu com **18.4** (padrão atual do Neon).
   A versão do servidor manda na versão do cliente: o `pg_dump` **recusa** dumpar um
   servidor mais novo que ele, então o workflow de backup e o CI usam Postgres **18**.
   Se algum dia recriar o projeto numa versão diferente, alinhe os dois arquivos.
4. Copie a connection string. Ela vem no formato
   `postgresql://user:senha@ep-xxx.us-east-2.aws.neon.tech/odonto?sslmode=require` —
   e precisa ser **quebrada em três variáveis** para o Spring:
   - `DB_URL` = `jdbc:postgresql://ep-xxx.us-east-2.aws.neon.tech/odonto?sslmode=require`
     (troque `postgresql://` por `jdbc:postgresql://` e **remova usuário e senha da URL**)
   - `DB_USER` = o usuário
   - `DB_PASSWORD` = a senha

Não rode nada no banco na mão: o Flyway cria as 11 tabelas no primeiro boot da API.

### Passo 3 — Supabase — ✅ FEITO E VERIFICADO EM 24/07/2026

Projeto `whxvlsjcseeysxqwsjms`, buckets **`Radiografias`** e **`Backups`**, ambos privados.

Atenção ao **maiúsculo**: nome de bucket no Supabase é *case-sensitive*, e os buckets
saíram capitalizados. O padrão do código e o caminho no workflow de backup foram
ajustados para `Radiografias`/`Backups` — se um dia recriar os buckets em minúsculas,
ajuste `STORAGE_BUCKET` e o `backup-banco.yml`.

Verificado contra o Supabase real (não mais contra stub):

| Teste | Resultado |
|---|---|
| Contrato REST direto (POST/GET/DELETE + `x-upsert`) | 200 em todos; segundo POST na mesma chave não dá 409 |
| Bucket privado sem token | negado |
| Upload de radiografia pela API | objeto aparece no bucket, 32 bytes |
| Download pela API | conteúdo byte-a-byte idêntico ao enviado |
| **Teste do redeploy** | app derrubado e religado com `UPLOAD_DIR` apontando para um diretório **vazio** → radiografia abriu normalmente |
| Exclusão pela API | objeto removido do bucket |

O último item é o que prova o B4: com disco local vazio (o diretório nem foi criado), o
arquivo continuou acessível. Dados de teste apagados: bucket com 0 objetos, banco com
0 pacientes / 0 radiografias / 0 admins.

Instruções originais (referência):

1. supabase.com → novo projeto (região **East US** para ficar perto do resto)
2. *Storage* → **New bucket** → nome `radiografias` → **Public: OFF** (privado)
3. *Storage* → **New bucket** → nome `backups` → **Public: OFF**
4. *Project Settings → API*: copie a **URL do projeto** e a chave **`service_role`**
   (é a chave que ignora RLS — só no servidor, nunca no front)

### Passo 4 — Render (API)

1. render.com → *New* → **Web Service** → conecte o repo
2. Repositório `borges-odontologia` · **Root Directory:** *vazio* (o repo já é a API) ·
   **Language/Runtime:** `Docker` · **Region:** `Ohio`
3. **Instance Type:** `Free`
4. **Health Check Path:** `/health`
5. *Environment* → adicione as variáveis da §4. Para o `JWT_SECRET`:
   ```bash
   openssl rand -base64 64 | tr -d '\n'
   ```
   Nunca reaproveite o segredo antigo que estava versionado no repositório.
6. Deploy. No log você deve ver `Successfully applied 1 migration ... now at version v1`
   e `Default admin created: <e-mail da dentista>`.
7. Guarde a URL final (algo como `https://odonto-api.onrender.com`).

**Armadilha da porta (aconteceu no primeiro deploy, 24/07/2026):** o Render injeta a
porta em `PORT` (10000 por padrão) e roteia o tráfego só para ela. O Spring, por padrão,
sobe na 8080 — resultado: o contêiner sobe, conecta no banco, cria o admin, e ainda assim
**nenhuma requisição responde**; o TLS conecta e a requisição fica pendurada até o
timeout. Corrigido com `server.port=${PORT:8080}` no `application-prod.properties`.
Sintoma característico: `curl` conectando em ~30 ms e depois travando, sem 502 nem 404.

### Passo 5 — Cloudflare Pages (front)

1. dash.cloudflare.com → *Workers & Pages* → **Create** → *Pages* → conecte o repo
2. Repositório `odonto_app` · **Root directory:** *vazio* · **Build command:**
   `npm run build` · **Output:** `dist`
3. *Settings → Environment variables* (Production):
   - `VITE_API_BASE_URL` = a URL do Render do passo 4
   - `VITE_GOOGLE_CLIENT_ID` = seu client ID do Google (ou deixe vazio para desligar o
     Google Calendar)
4. Deploy. Anote o domínio `https://<projeto>.pages.dev`.
5. **Volte ao Render** e ajuste `APP_CORS_ALLOWED_ORIGINS` para esse domínio exato
   (com `https://`, sem barra no final). Sem isso o navegador bloqueia toda chamada.
6. Se usar Google Calendar: no Google Cloud Console, adicione o domínio do Pages em
   *Authorized JavaScript origins*.

O `public/_redirects` já está no repo — é ele que evita 404 ao recarregar uma rota interna.

### Passo 6 — Keep-alive (cron-job.org)

Crie conta em cron-job.org e um job:

- URL: `https://odonto-api.onrender.com/health` · método `GET`
- Execução: a cada **10 minutos**
- *Advanced → Execution schedule*: das **07:00 às 21:00**, seg–sáb, fuso `America/Sao_Paulo`

Esse recorte não é economia de estimação: é o que mantém o consumo dentro das 100 CU-h do
Neon (§1). Não coloque 24/7.

### Passo 7 — Backup diário — ✅ FUNCIONANDO E CONFERIDO (24/07/2026)

Secrets cadastrados no repo `borges-odontologia` e workflow executado com sucesso.
Dump baixado do bucket e inspecionado: **3.736 bytes**, 11 tabelas (`admins`,
`anotacoes`, `consultas`, `dados_dentes`, `fichas_clinicas`, `flyway_schema_history`,
`lancamentos`, `pacientes`, `planos_tratamento`, `radiografias`, `revoked_tokens`),
17 índices e 7 foreign keys. O `flyway_schema_history` vem no dump, então um restore
volta com o histórico de migrations coerente.

**Dois bugs que a primeira execução revelou** — vale conhecer porque são clássicos:

1. O runner do GitHub já traz `pg_dump` **16** no `PATH`; o pacote 18 instala em
   `/usr/lib/postgresql/18/bin/`. O dump abortava com `server version mismatch`.
2. Em `pg_dump | gzip > arquivo`, o código de saída do pipe é o do **gzip**. Com o dump
   abortando, o gzip gerava 20 bytes de nada e o step ficava **verde** — backup-fantasma
   perfeito: "rodando" há meses e vazio no dia do desastre.

Corrigido com caminho completo do cliente 18, `set -euo pipefail` e duas travas: o
arquivo precisa passar de 1 KB **e** conter `CREATE TABLE public.pacientes`.

### ⚠️ Como restaurar (leia antes de precisar)

O teste de restauração revelou uma armadilha séria: o `pg_dump` 18 escreve
**`\restrict <token>`** na quinta linha do arquivo — meta-comando que só o **psql 18+**
entende. Restaurando com psql 16, ele aborta ali e cria **ZERO tabelas**. O arquivo está
perfeito; a ferramenta é que era velha. Num dia de emergência, isso pareceria "backup
corrompido".

Restauração correta:

```bash
# cliente 18 — obrigatório
brew install postgresql@18          # macOS
export PATH="/opt/homebrew/opt/postgresql@18/bin:$PATH"

# 1. baixar o backup do bucket
curl -s "https://whxvlsjcseeysxqwsjms.supabase.co/storage/v1/object/Backups/odonto-AAAA-MM-DD.sql.gz" \
  -H "Authorization: Bearer <service_role>" -o backup.sql.gz

# 2. restaurar (aqui num banco novo, para não sobrescrever o de produção)
psql "postgresql://neondb_owner:<senha>@<host>/neondb" -c "CREATE DATABASE restauracao;"
gunzip -c backup.sql.gz | psql "postgresql://neondb_owner:<senha>@<host>/restauracao" -v ON_ERROR_STOP=1

# 3. conferir
psql ".../restauracao" -c "\dt"
```

O `-v ON_ERROR_STOP=1` não é opcional: sem ele, o psql segue em frente depois de erros e
entrega um banco pela metade parecendo sucesso.

### Verificação automática mensal

`.github/workflows/verificar-backup.yml` faz esse ciclo inteiro sozinho no dia 1 de cada
mês (e sob demanda): baixa o backup mais recente, restaura num banco descartável do
próprio projeto Neon, confere que as 11 tabelas voltaram e que `pacientes`, `consultas`,
`fichas_clinicas`, `radiografias`, `lancamentos` e `admins` estão consultáveis, e apaga o
banco de teste no final (inclusive se algo falhar). O `neondb` de produção nunca é tocado.

Instruções originais (referência):

O workflow já está em `.github/workflows/backup-banco.yml`. Só falta cadastrar os secrets
em *Settings → Secrets and variables → Actions*:

| Secret | Valor |
|---|---|
| `DATABASE_URL` | a connection string `postgresql://...` completa do Neon (formato original, não o JDBC) |
| `STORAGE_URL` | a URL do projeto Supabase |
| `STORAGE_SERVICE_KEY` | a chave `service_role` |

Depois rode uma vez na mão (*Actions → Backup diário do banco → Run workflow*) e confirme
que o arquivo apareceu no bucket `backups`. Backup que nunca foi testado não é backup.

### Passo 8 — Teste de aceitação — ✅ VALIDADO EM PRODUÇÃO (24/07/2026)

Sistema publicado e funcionando:

- API: `https://borges-odontologia.onrender.com` (Render, Ohio, Docker, free)
- Front: `https://odonto-app.pages.dev` (Cloudflare Pages)
- Banco: Neon `us-east-2`, Postgres 18.4 · Arquivos: Supabase Storage, bucket `Radiografias`

Verificado de fora: `/health` 200 em 0,2 s · endpoints protegidos devolvem 401 ·
CORS libera só `https://odonto-app.pages.dev` (origem estranha → 403) · rota interna do
front devolve o index (o `_redirects` funciona) · bundle publicado aponta para a API real.

**E o teste que importava: radiografia subida em produção permaneceu acessível.** O
problema do disco efêmero está resolvido no ambiente real, não só em teste.

Roteiro original, para repetir a cada mudança grande:

1. Login com o e-mail/senha do `ADMIN_*`, e **troque a senha inicial**
2. Cadastrar paciente → agendar consulta → lançar financeiro → subir uma radiografia
3. **Force um redeploy no Render** ("Manual Deploy → Deploy latest commit")
4. Abra a mesma radiografia de novo

O passo 4 é o teste que importa: é ele que prova que os arquivos não vivem mais no disco
efêmero do container. Se a imagem abrir depois do redeploy, o B4 está resolvido de verdade.

### O que já foi verificado aqui (não precisa refazer)

- `./mvnw -DskipTests package` → jar de 61 MB gerado
- Migration aplicada em banco Postgres 16 vazio → 11 tabelas, `ddl-auto=validate` passa
- Boot com perfil `prod`: falha na hora com mensagem clara se faltar `JWT_SECRET`,
  `STORAGE_URL` ou `STORAGE_SERVICE_KEY`
- `AdminSeeder` cria a conta a partir das envs; **zero** pacientes de demonstração em prod
- CORS: origem autorizada responde 200, origem estranha recebe 403
- Fluxo completo de radiografia (upload → download → exclusão) pela implementação de storage
- `npm run build` do front passando (estava quebrado antes)
- Suíte de testes verde (`./mvnw test`, 4 testes) — antes o `ApplicationTests` nem
  subia, porque não havia configuração de teste nenhuma
- `SupabaseFileStorage` exercitado contra servidor stub (upload, download, exclusão,
  404) — mas **não** contra o Supabase real; ver ressalva no §3

### CI (opcional, mas recomendado)

Um workflow em cada repositório, já que são repos separados:

- `odonto_api/.github/workflows/ci.yml` — sobe um Postgres 16 como service container,
  aplica as migrations **do zero**, roda os testes, empacota o jar e faz o
  `docker build` do Dockerfile de produção.
- `odonto_app/.github/workflows/ci.yml` — roda `npm run build`, o mesmo comando do
  Cloudflare Pages, então erro de tipo aparece no GitHub e não no deploy. O lint fica
  informativo: existem 10 apontamentos antigos (`any` e `react-refresh`) que não
  impedem o build.
- `odonto_api/.github/workflows/backup-banco.yml` — o backup diário (§5, Passo 7).

---

## 6. Ressalva honesta (LGPD)

Prontuário e radiografia são **dado pessoal sensível de saúde**. Free tier não dá backup,
SLA nem contrato de tratamento de dados, e os dados ficam fora do Brasil (o que é permitido,
mas precisa estar claro para a dentista). O mínimo que resolve de graça:

- backup diário automatizado (passo 8) — free tier **não** tem backup próprio;
- acesso só por HTTPS, com JWT expirando em 24 h (já implementado);
- bucket de radiografias **privado**: o arquivo nunca tem URL pública — a API lê o objeto
  com a service key e devolve o conteúdo na resposta já autenticada por JWT;
- um aviso escrito à dentista de que o ambiente é gratuito e sem SLA.

Quando o consultório depender do sistema para faturar, o upgrade mínimo pago (~US$ 7/mês no
Render + US$ 19 no Neon, ou uma VPS nacional de ~R$ 30/mês) compra sempre-ligado, backup
gerenciado e dados no Brasil. Vale colocar isso na conversa desde já.
