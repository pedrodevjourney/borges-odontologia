package com.odonto.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint público de liveness.
 *
 * <p>Serve ao health check do provedor e ao ping externo que evita a hibernação do
 * free tier. Não toca no banco de propósito: o compute do Postgres no plano
 * gratuito é cobrado por hora ligada, e acordá-lo a cada 10 minutos queimaria a
 * cota mesmo com a clínica fechada.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
