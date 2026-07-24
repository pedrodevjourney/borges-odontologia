package com.odonto.api.storage;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exercita o {@link SupabaseFileStorage} contra um servidor HTTP local que imita o
 * endpoint /storage/v1/object do Supabase. Cobre o que só aparece em execução:
 * método, cabeçalhos, Content-Length do corpo enviado e tratamento de erro.
 */
class SupabaseFileStorageTest {

    private HttpServer server;
    private SupabaseFileStorage storage;

    /** Requisições recebidas, para inspeção nas asserções. */
    private final List<String> chamadas = new ArrayList<>();
    private final Map<String, byte[]> objetos = new ConcurrentHashMap<>();
    private final Map<String, String> cabecalhos = new ConcurrentHashMap<>();

    @BeforeEach
    void iniciarServidor() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/storage/v1/object/", exchange -> {
            String chave = exchange.getRequestURI().getPath()
                    .replace("/storage/v1/object/radiografias/", "");
            chamadas.add(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            cabecalhos.put("authorization", String.valueOf(exchange.getRequestHeaders().getFirst("Authorization")));
            cabecalhos.put("x-upsert", String.valueOf(exchange.getRequestHeaders().getFirst("x-upsert")));
            cabecalhos.put("content-length", String.valueOf(exchange.getRequestHeaders().getFirst("Content-Length")));

            switch (exchange.getRequestMethod()) {
                case "POST" -> {
                    try (InputStream in = exchange.getRequestBody()) {
                        objetos.put(chave, in.readAllBytes());
                    }
                    responder(exchange, 200, "{\"Key\":\"" + chave + "\"}");
                }
                case "GET" -> {
                    byte[] conteudo = objetos.get(chave);
                    if (conteudo == null) {
                        responder(exchange, 404, "{\"error\":\"not_found\"}");
                    } else {
                        exchange.getResponseHeaders().add("Content-Type", "image/png");
                        exchange.sendResponseHeaders(200, conteudo.length);
                        exchange.getResponseBody().write(conteudo);
                        exchange.close();
                    }
                }
                case "DELETE" -> {
                    objetos.remove(chave);
                    responder(exchange, 200, "{}");
                }
                default -> responder(exchange, 405, "");
            }
        });
        server.start();

        storage = new SupabaseFileStorage(
                "http://127.0.0.1:" + server.getAddress().getPort(), "radiografias", "chave-de-teste");
    }

    @AfterEach
    void pararServidor() {
        server.stop(0);
    }

    private static void responder(HttpExchange exchange, int status, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    @Test
    void enviaLeEExcluiUmArquivo() throws IOException {
        byte[] conteudo = "conteudo-da-radiografia".getBytes(StandardCharsets.UTF_8);
        var arquivo = new MockMultipartFile("arquivo", "raio.png", "image/png", conteudo);

        storage.store("radiografias/7/abc.png", arquivo);

        assertThat(objetos).containsKey("radiografias/7/abc.png");
        assertThat(objetos.get("radiografias/7/abc.png")).isEqualTo(conteudo);
        assertThat(cabecalhos.get("authorization")).isEqualTo("Bearer chave-de-teste");
        assertThat(cabecalhos.get("x-upsert")).isEqualTo("true");
        // Content-Length correto: o corpo não é enviado em chunked.
        assertThat(cabecalhos.get("content-length")).isEqualTo(String.valueOf(conteudo.length));

        StoredFile lido = storage.read("radiografias/7/abc.png");
        assertThat(lido.contentType()).isEqualTo("image/png");
        try (InputStream in = lido.resource().getInputStream()) {
            assertThat(in.readAllBytes()).isEqualTo(conteudo);
        }

        storage.delete("radiografias/7/abc.png");
        assertThat(objetos).doesNotContainKey("radiografias/7/abc.png");

        assertThat(chamadas).containsExactly(
                "POST /storage/v1/object/radiografias/radiografias/7/abc.png",
                "GET /storage/v1/object/radiografias/radiografias/7/abc.png",
                "DELETE /storage/v1/object/radiografias/radiografias/7/abc.png");
    }

    @Test
    void lanzaExcecaoQuandoObjetoNaoExiste() {
        assertThatThrownBy(() -> storage.read("radiografias/7/inexistente.png"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void exigeUrlEChaveDeServico() {
        assertThatThrownBy(() -> new SupabaseFileStorage("", "radiografias", "chave"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("STORAGE_URL");
    }
}
