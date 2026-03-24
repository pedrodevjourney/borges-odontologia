package com.odonto.api.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(2)
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    @Override
    public void run(String... args) {
        log.info("Carregando dados de demonstração...");
        // TODO: adicionar seeders de pacientes, consultas, etc. quando as entidades existirem
        log.info("Dados de demonstração carregados.");
    }
}
