package com.odonto.api.seeder;

import com.odonto.api.paciente.entity.Paciente;
import com.odonto.api.paciente.enums.EstadoCivil;
import com.odonto.api.paciente.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("dev")
@Order(2)
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final PacienteRepository pacienteRepository;

    public DemoDataSeeder(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public void run(String... args) {
        if (pacienteRepository.count() > 0) {
            log.info("Pacientes já cadastrados, pulando seed.");
            return;
        }

        log.info("Carregando dados de demonstração...");

        List<Paciente> pacientes = List.of(
                criarPaciente(1, "Maria Silva", "Centro", "Rua das Flores, 123 - Centro, São Paulo - SP",
                        "Dentista", LocalDate.of(1985, 3, 15), "Brasileira", "Dr. João",
                        LocalDate.of(2025, 1, 10), "(11) 99999-1111", "(11) 3333-1111",
                        EstadoCivil.CASADO, false),

                criarPaciente(2, "João Santos", "Jardim América", "Av. Brasil, 456 - Jardim América, São Paulo - SP",
                        "Engenheiro", LocalDate.of(1990, 7, 22), "Brasileira", "Maria Silva",
                        LocalDate.of(2025, 2, 5), "(11) 99999-2222", null,
                        EstadoCivil.SOLTEIRO, true),

                criarPaciente(3, "Ana Oliveira", "Vila Mariana", "Rua Domingos de Morais, 789 - Vila Mariana, São Paulo - SP",
                        "Professora", LocalDate.of(1978, 11, 3), "Brasileira", null,
                        LocalDate.of(2025, 3, 12), "(11) 99999-3333", "(11) 3333-3333",
                        EstadoCivil.DIVORCIADO, false),

                criarPaciente(4, "Carlos Pereira", "Mooca", "Rua da Mooca, 321 - Mooca, São Paulo - SP",
                        "Advogado", LocalDate.of(1995, 1, 30), "Brasileira", "Ana Oliveira",
                        LocalDate.of(2025, 4, 1), "(11) 99999-4444", null,
                        EstadoCivil.UNIAO_ESTAVEL, false),

                criarPaciente(5, "Fernanda Costa", "Pinheiros", "Rua dos Pinheiros, 654 - Pinheiros, São Paulo - SP",
                        "Médica", LocalDate.of(1988, 5, 18), "Brasileira", "Dr. João",
                        LocalDate.of(2025, 5, 20), "(11) 99999-5555", "(11) 3333-5555",
                        EstadoCivil.CASADO, true),

                criarPaciente(6, "Roberto Almeida", "Liberdade", "Rua Galvão Bueno, 100 - Liberdade, São Paulo - SP",
                        "Contador", LocalDate.of(1970, 9, 8), "Brasileira", null,
                        LocalDate.of(2025, 6, 15), "(11) 99999-6666", null,
                        EstadoCivil.VIUVO, false),

                criarPaciente(7, "Juliana Martins", "Consolação", "Rua Augusta, 200 - Consolação, São Paulo - SP",
                        "Designer", LocalDate.of(2000, 12, 25), "Brasileira", "Fernanda Costa",
                        LocalDate.of(2025, 7, 3), "(11) 99999-7777", null,
                        EstadoCivil.SOLTEIRO, false),

                criarPaciente(8, "Pedro Souza", "Bela Vista", "Rua Treze de Maio, 350 - Bela Vista, São Paulo - SP",
                        "Programador", LocalDate.of(1992, 4, 10), "Brasileira", null,
                        LocalDate.of(2025, 8, 10), "(11) 99999-8888", "(11) 3333-8888",
                        EstadoCivil.CASADO, true),

                criarPaciente(9, "Camila Rodrigues", "Santana", "Rua Voluntários da Pátria, 500 - Santana, São Paulo - SP",
                        "Enfermeira", LocalDate.of(1983, 6, 7), "Brasileira", "João Santos",
                        LocalDate.of(2025, 9, 22), "(11) 99999-9999", null,
                        EstadoCivil.DIVORCIADO, false),

                criarPaciente(10, "Lucas Ferreira", "Tatuapé", "Rua Tuiuti, 750 - Tatuapé, São Paulo - SP",
                        "Estudante", LocalDate.of(2003, 2, 14), "Brasileira", "Camila Rodrigues",
                        LocalDate.of(2025, 10, 5), "(11) 99999-0000", null,
                        EstadoCivil.SOLTEIRO, true)
        );

        pacienteRepository.saveAll(pacientes);
        log.info("{} pacientes de demonstração cadastrados.", pacientes.size());
    }

    private Paciente criarPaciente(Integer numero, String nome, String residencia, String enderecoCompleto,
                                   String profissao, LocalDate dataNascimento, String nacionalidade,
                                   String indicadoPor, LocalDate inicioTratamento, String telefone,
                                   String telefoneSecundario, EstadoCivil estadoCivil, Boolean dlne) {
        Paciente p = new Paciente();
        p.setNumero(numero);
        p.setNome(nome);
        p.setResidencia(residencia);
        p.setEnderecoCompleto(enderecoCompleto);
        p.setProfissao(profissao);
        p.setDataNascimento(dataNascimento);
        p.setNacionalidade(nacionalidade);
        p.setIndicadoPor(indicadoPor);
        p.setInicioTratamento(inicioTratamento);
        p.setTelefone(telefone);
        p.setTelefoneSecundario(telefoneSecundario);
        p.setEstadoCivil(estadoCivil);
        p.setDlne(dlne);
        return p;
    }
}
