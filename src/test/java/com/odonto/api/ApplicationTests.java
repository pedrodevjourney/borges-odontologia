package com.odonto.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Sobe o contexto completo contra um Postgres real: valida de uma vez as migrations
 * do Flyway, o {@code ddl-auto=validate} do Hibernate e a resolução de todas as
 * variáveis de configuração.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
