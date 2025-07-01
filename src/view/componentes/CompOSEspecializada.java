/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import models.Cliente;
import models.Elevador;
import models.OrdemServico;
import models.Servico;
import models.Usuario;
import models.Veiculo;
import models.enums.StatusOrdem;
import models.enums.TipoUsuario;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;

/**
 *
 * @author marcos_miller
 */
public class CompOSEspecializada {

    private OrdemServicoService ordemServicoService;
    private UsuarioService usuarioService;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private ItemEstoqueService itemEstoqueService;
    private ServicoService servicoService;
    private ElevadorService elevadorService;
    private Scanner scanner;
    private Usuario usuarioLogado;

    public CompOSEspecializada(OrdemServicoService ordemServicoService, UsuarioService usuarioService,
                               ClienteService clienteService, VeiculoService veiculoService,
                               ItemEstoqueService itemEstoqueService, ServicoService servicoService,
                               ElevadorService elevadorService, Scanner scanner) {
        this.ordemServicoService = ordemServicoService;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService;
        this.elevadorService = elevadorService;
        this.scanner = scanner;
        this.usuarioLogado = util.UserSession.getInstance().getLoggedInUser();
    }


    public void exibirMenu() {
        int opcao;
        exibirOrdens();

        do {
            System.out.println("\n--- Opções de Ordens de Serviço ---");
            System.out.println("1. Atualizar Status de uma Ordem de Serviço");
            System.out.println("0. Voltar ao Painel Principal");
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
                atualizarStatusOrdemServico();
                break;
            case 0:
                System.out.println("Saindo da visualização de Ordens de Serviço.");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }


    /**
     * Exibe a lista de Ordens de Serviço relevantes para o usuário logado, com detalhes completos.
     * Esta lista é filtrada com base no tipo de usuário.
     */
    private void exibirOrdens() {
        System.out.println("\n--- LISTA DE ORDENS DE SERVIÇO ATRIBUÍDAS / EM ABERTO ---");

        List<OrdemServico> ordensFiltradas = new ArrayList<>();
        TipoUsuario tipo = usuarioLogado.getTipo();

        switch (tipo) {
            case ATENDENTE:
                ordensFiltradas = ordemServicoService.listarTodasOrdens().stream()
                    .filter(os -> os.getStatus() == StatusOrdem.AGUARDANDO_LIBERACAO || 
                                   os.getStatus() == StatusOrdem.AGUARDANDO_PAGAMENTO ||
                                   os.getStatus() == StatusOrdem.AGUARDANDO_DIAGNOSTICO)
                    .collect(Collectors.toList());
                System.out.println("Visão: Ordens aguardando sua ação (Atendente)");
                break;
            case MECANICO:
                ordensFiltradas = ordemServicoService.listarOrdensPorMecanico(usuarioLogado.getId()).stream()
                    .filter(os -> os.getStatus() == StatusOrdem.EM_DIAGNOSTICO ||
                                   os.getStatus() == StatusOrdem.EM_EXECUCAO ||
                                   os.getStatus() == StatusOrdem.AGUARDANDO_DIAGNOSTICO)
                    .collect(Collectors.toList());
                System.out.println("Visão: Suas Ordens de Serviço em Diagnóstico/Execução (Mecânico)");
                break;
            case GERENTE:
                ordensFiltradas = ordemServicoService.listarTodasOrdens();
                System.out.println("Visão: Todas as Ordens de Serviço (Gerente)");
                break;
            default:
                System.out.println("Nenhuma visão de Ordem de Serviço definida para este tipo de usuário.");
                break;
        }

        if (ordensFiltradas.isEmpty()) {
            System.out.println("Nenhuma ordem de serviço relevante no momento.");
        } else {
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s | %-15s | %-25s | %-20s | %-20s | %-20s | %-20s | %-10s%n",
                              "ID", "CÓDIGO", "CLIENTE", "VEÍCULO (PLACA)", "MECÂNICO", "STATUS", "PREÇO TOTAL (M.O. + PEÇAS)", "QTD SERVIÇOS");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (OrdemServico os : ordensFiltradas) {
                String nomeCliente = clienteService.buscarClientePorId(os.getIdCliente()).map(Cliente::getNome).orElse("Desconhecido");
                String placaVeiculo = veiculoService.buscarVeiculoPorId(os.getIdVeiculo()).map(Veiculo::getPlaca).orElse("Desconhecida");
                String nomeMecanico = usuarioService.buscarUsuarioPorId(os.getIdMecanicoResponsavel()).map(Usuario::getNome).orElse("Desconhecido");
                
                BigDecimal precoTotalFinal = ordemServicoService.calcularPrecoTotalFinalOS(os);

                System.out.printf("%-5d | %-15s | %-25s | %-20s | %-20s | %-20s | %-20.2f | %-10d%n",
                                  os.getId(),
                                  os.getCodigo(),
                                  nomeCliente,
                                  placaVeiculo,
                                  nomeMecanico,
                                  os.getStatus().getDescricao(),
                                  precoTotalFinal,
                                  os.getServicos().size());
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     * Permite ao usuário atualizar o status de uma Ordem de Serviço.
     * Esta é a funcionalidade que integra a escolha do elevador.
     */
    private void atualizarStatusOrdemServico() {
        System.out.println("\n--- ATUALIZAR STATUS DE ORDEM DE SERVIÇO ---");
        System.out.print("Digite o ID da Ordem de Serviço para alterar o status: ");
        int idOs = -1;
        try {
            idOs = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o ID.");
            scanner.nextLine();
            return;
        }

        Optional<OrdemServico> osOpt = ordemServicoService.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            System.out.println("Ordem de Serviço com ID " + idOs + " não encontrada.");
            return;
        }
        OrdemServico os = osOpt.get();
        System.out.println("OS Selecionada: " + os.getCodigo() + " - Status Atual: " + os.getStatus().getDescricao());

        System.out.println("Selecione o novo status:");
        StatusOrdem[] statuses = StatusOrdem.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i].getDescricao());
        }
        System.out.print("Opção do Status: ");
        int statusOpcao = -1;
        try {
            statusOpcao = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o status.");
            scanner.nextLine();
            return;
        }

        StatusOrdem novoStatus = null;
        try {
            novoStatus = statuses[statusOpcao - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Opção de status inválida.");
            return;
        }

        Optional<Integer> idElevadorParaAlocar = Optional.empty();

        if ((novoStatus == StatusOrdem.EM_DIAGNOSTICO || novoStatus == StatusOrdem.EM_EXECUCAO) && 
            !(os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO)) {
            
            System.out.println("\n[SISTEMA ELEVADOR] Alocação necessária para OS " + os.getCodigo() + "...");
            
            boolean osRequerAlinhamento = os.getServicos().stream()
                                            .anyMatch(Servico::requerPrioridade);

            List<Elevador> elevadoresDisponiveis = elevadorService.listarElevadoresDisponiveis();

            if (elevadoresDisponiveis.isEmpty()) {
                System.err.println("Nenhum elevador disponível no momento. Não será possível alocar.");
                return;
            }

            List<Elevador> elevadoresFiltrados;

            if (osRequerAlinhamento) {
                elevadoresFiltrados = elevadoresDisponiveis.stream()
                                                            .filter(Elevador::temCapacidadeAlinhamento)
                                                            .collect(Collectors.toList());
                System.out.println("OS requer elevador de Alinhamento. Elevadores disponíveis para Alinhamento:");
            } else {
                elevadoresFiltrados = elevadoresDisponiveis.stream()
                                                    .filter(e -> !e.temCapacidadeAlinhamento())
                                                    .collect(Collectors.toList());
                if(elevadoresFiltrados.isEmpty()){
                    elevadoresFiltrados.addAll(elevadoresDisponiveis);
                    System.out.println("Nenhum elevador geral disponível. Usando elevador de alinhamento se disponível.");
                } else {
                    System.out.println("OS não requer elevador de Alinhamento. Elevadores Gerais disponíveis:");
                }
            }
            
            for (int i = 0; i < elevadoresFiltrados.size(); i++) {
                System.out.println((i + 1) + ". " + elevadoresFiltrados.get(i).toString());
            }
            int escolha = -1;
            try {
                escolha = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Abortando alocação.");
                scanner.nextLine();
                return;
            }

            if (escolha > 0 && escolha <= elevadoresFiltrados.size()) {
                idElevadorParaAlocar = Optional.of(elevadoresFiltrados.get(escolha - 1).getId());
            } else {
                System.err.println("Opção de elevador inválida. Abortando alocação.");
                return;
            }
        }

        try {
            ordemServicoService.alterarStatusOrdemServico(os.getId(), novoStatus, idElevadorParaAlocar);
            System.out.println("Status da OS " + os.getCodigo() + " atualizado para " + novoStatus.getDescricao() + " com sucesso!");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
        }
    }

    // --- Métodos Auxiliares de Leitura de Input ---
    
    private int lerInteiroValido(String prompt) {
        while (true) {
            System.out.print(prompt);
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
            System.out.print(prompt);
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
            }
        }
    }
}