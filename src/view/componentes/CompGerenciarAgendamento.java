/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Agendamento;
import models.Cliente;
import models.Servico;
import models.Usuario;
import models.Veiculo;
import service.AgendamentoService;
import service.ClienteService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;

/**
 *
 * @author marcos_miller
 */
public class CompGerenciarAgendamento {

    private AgendamentoService agendamentoService;
    private ClienteService clienteService; // Ainda pode ser útil para buscar detalhes do cliente para exibição
    private VeiculoService veiculoService; // Ainda pode ser útil para buscar detalhes do veículo para exibição
    private Scanner scanner;

    // NOVAS DEPENDÊNCIAS PARA DELEGAR A LÓGICA DE SELEÇÃO/CRIAÇÃO ON-DEMAND
    private CompGerenciarCliente compGerenciarCliente;
    private CompGerenciarVeiculo compGerenciarVeiculo;


    // Construtor atualizado
    public CompGerenciarAgendamento(AgendamentoService agendamentoService,
                                    ClienteService clienteService,
                                    VeiculoService veiculoService,
                                    Scanner scanner,
                                    CompGerenciarCliente compGerenciarCliente, // <<< NOVO PARÂMETRO
                                    CompGerenciarVeiculo compGerenciarVeiculo) { // <<< NOVO PARÂMETRO
        this.agendamentoService = agendamentoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.scanner = scanner;
        this.compGerenciarCliente = compGerenciarCliente;
        this.compGerenciarVeiculo = compGerenciarVeiculo;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Agendamentos =====");
            System.out.println("1. Agendar Novo Serviço");
            System.out.println("2. Listar Todos os Agendamentos");
            System.out.println("3. Reagendar Agendamento");
            System.out.println("4. Cancelar Agendamento");
            System.out.println("0. Voltar ao Menu Anterior");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }

            processarOpcao(opcao);

        } while (opcao != 0);
    }

    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                agendarNovoServico();
                break;
            case 2:
                listarTodosAgendamentos();
                break;
            case 3:
                reagendarAgendamento();
                break;
            case 4:
                cancelarAgendamento();
                break;
            case 0:
                System.out.println("Saindo do Gerenciamento de Agendamentos.");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }

    private void agendarNovoServico() {
        System.out.println("\n--- AGENDAR NOVO SERVIÇO ---");

        // 1. Selecionar ou Criar Cliente usando CompGerenciarCliente
        Optional<Cliente> clienteOpt = compGerenciarCliente.selecionarOuCriarCliente();
        if (clienteOpt.isEmpty()) {
            System.out.println("Agendamento cancelado: Cliente não selecionado ou criado.");
            return;
        }
        Cliente clienteSelecionado = clienteOpt.get();

        // 2. Selecionar ou Criar Veículo do Cliente usando CompGerenciarVeiculo
        Optional<Veiculo> veiculoOpt = compGerenciarVeiculo.selecionarOuCriarVeiculo(clienteSelecionado.getId());
        if (veiculoOpt.isEmpty()) {
            System.out.println("Agendamento cancelado: Veículo não selecionado ou criado.");
            return;
        }
        Veiculo veiculoSelecionado = veiculoOpt.get();

        // 3. Inserir Data e Hora
        LocalDateTime dataHoraAgendamento = solicitarDataHora();
        if (dataHoraAgendamento == null) {
            System.out.println("Agendamento cancelado: Data e hora inválidas.");
            return;
        }

        // 4. Inserir Valor do Agendamento (Valor Fixo / Taxa)
        BigDecimal valorAgendamento = lerBigDecimalValido("Digite o valor do agendamento (taxa/estimado): ");
        if (valorAgendamento == null) { // Se a leitura falhar
            System.out.println("Agendamento cancelado: Valor inválido.");
            return;
        }

        try {
            agendamentoService.criarAgendamento(
                    dataHoraAgendamento,
                    clienteSelecionado.getId(),
                    veiculoSelecionado.getId(),
                    valorAgendamento
            );
            System.out.println("Agendamento realizado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao agendar serviço: " + e.getMessage());
        }
    }

    private void listarTodosAgendamentos() {
        System.out.println("\n--- LISTA DE AGENDAMENTOS ---");
        List<Agendamento> agendamentos = agendamentoService.listarTodosAgendamentos();

        if (agendamentos.isEmpty()) {
            System.out.println("Nenhum agendamento cadastrado.");
        } else {
            // Para uma exibição mais completa, você pode buscar os nomes de cliente e placa do veículo
            for (Agendamento agendamento : agendamentos) {
                Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(agendamento.getIdCliente());
                Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(agendamento.getIdVeiculo());

                String clienteNome = clienteOpt.map(Cliente::getNome).orElse("Desconhecido");
                String veiculoPlaca = veiculoOpt.map(Veiculo::getPlaca).orElse("Desconhecido");

                System.out.printf("ID: %d | Data/Hora: %s | Cliente: %s | Veículo: %s | Status: %s%n",
                        agendamento.getId(),
                        agendamento.getDataHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        clienteNome,
                        veiculoPlaca,
                        
                        agendamento.getStatus().getDescricao());
            }
        }
    }

    private void reagendarAgendamento() {
        System.out.println("\n--- REAGENDAR AGENDAMENTO ---");
        System.out.print("Digite o ID do agendamento a ser reagendado: ");
        int idAgendamento = lerInteiroValido(); // Usar auxiliar

        try {
            LocalDateTime novaDataHora = solicitarDataHora();
            if (novaDataHora == null) return;

            boolean sucesso = agendamentoService.reagendarAgendamento(idAgendamento, novaDataHora);
            if (sucesso) {
                System.out.println("Agendamento reagendado com sucesso!");
            } else {
                System.out.println("Falha ao reagendar agendamento. Verifique o ID e o status.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao reagendar agendamento: " + e.getMessage());
        }
    }

    private void cancelarAgendamento() {
        System.out.println("\n--- CANCELAR AGENDAMENTO ---");
        System.out.print("Digite o ID do agendamento a ser cancelado: ");
        int idAgendamento = lerInteiroValido(); // Usar auxiliar

        try {
            BigDecimal valorRetido = agendamentoService.cancelarAgendamento(idAgendamento);
            if (valorRetido != null) { // Se o cancelamento for bem-sucedido e retornar valor
                System.out.println("Agendamento cancelado com sucesso! Valor retido: R$ " + String.format("%.2f", valorRetido));
            } else {
                System.out.println("Falha ao cancelar agendamento. Verifique o ID e o status.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }

    // --- Métodos Auxiliares de Leitura e Seleção (MANTIDOS SE FOREM GERAIS OU REMOVIDOS SE ESPECÍFICOS) ---

    private int lerInteiroValido() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número inteiro.");
                scanner.nextLine();
            }
        }
    }

    private BigDecimal lerBigDecimalValido(String prompt) {
        while (true) {
            System.out.print(prompt); // Adicionado prompt
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
            }
        }
    }

    private LocalDateTime solicitarDataHora() {
        System.out.print("Digite a data e hora do agendamento (formato YYYY-MM-dd HH:mm): ");
        String dataHoraStr = scanner.nextLine();
        try {
            // Adicionado :00 para segundos para corresponder ao formato completo
            return LocalDateTime.parse(dataHoraStr + ":00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            System.err.println("Formato de data e hora inválido. Use YYYY-MM-dd HH:mm.");
            return null;
        }
    }

    // --- MÉTODOS REMOVIDOS: solicitarClienteExistente() e solicitarVeiculoExistente() ---
    // A lógica desses métodos foi movida para CompGerenciarCliente e CompGerenciarVeiculo.
    // CompGerenciarAgendamento agora os utiliza através das instâncias injetadas.
}