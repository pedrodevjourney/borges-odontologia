package com.odonto.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me() {
        return ResponseEntity.ok(Map.of("status", "autenticado com sucesso"));
    }
}
