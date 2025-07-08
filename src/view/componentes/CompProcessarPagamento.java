/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.OrdemServico;
import models.Pagamento;
import models.enums.TipoPagamento;
import service.OrdemServicoService;
import service.PagamentoService;

/**
 *
 * @author marcos_miller
 */
public class CompProcessarPagamento {

    private PagamentoService pagamentoService;
    private OrdemServicoService ordemServicoService; // Para buscar a OS e o valor
    private Scanner scanner;

    public CompProcessarPagamento(PagamentoService pagamentoService, OrdemServicoService ordemServicoService, Scanner scanner) {
        this.pagamentoService = pagamentoService;
        this.ordemServicoService = ordemServicoService;
        this.scanner = scanner;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Processar Pagamentos =====");
            System.out.println("1. Iniciar Novo Pagamento");
            System.out.println("2. Finalizar Pagamento Pendente"); 
            System.out.println("3. Listar Todos os Pagamentos");
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
            case 1: iniciarNovoPagamento(); break;
            case 2: finalizarPagamentoPendente(); break;
            case 3: listarTodosPagamentos(); break;
            case 0: System.out.println("Saindo do Processamento de Pagamentos."); break;
            default: System.out.println("Opção inválida. Tente novamente."); break;
        }
    }

    private void iniciarNovoPagamento() {
        System.out.println("\n--- INICIAR NOVO PROCESSO DE PAGAMENTO ---");
        System.out.print("Digite o ID da Ordem de Serviço para iniciar o pagamento: ");
        int idOs = -1;
        try {
            idOs = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número.");
            scanner.nextLine();
            return;
        }

        try {
            // Busca a OS para exibir o valor e verificar o status
            Optional<OrdemServico> osOpt = ordemServicoService.buscarOrdemServicoPorId(idOs);
            if (osOpt.isEmpty()) {
                System.out.println("Ordem de Serviço com ID " + idOs + " não encontrada.");
                return;
            }
            OrdemServico os = osOpt.get();
            BigDecimal valorTotalOS = ordemServicoService.calcularPrecoTotalFinalOS(os); // Calcula o valor total real da OS

            System.out.println("OS " + os.getCodigo() + " - Status: " + os.getStatus().getDescricao());
            System.out.println("Valor total da OS: R$ " + String.format("%.2f", valorTotalOS));
            
            Pagamento pagamentoIniciado = pagamentoService.iniciarProcessoPagamento(idOs);
            System.out.println("Processo de pagamento para OS " + os.getCodigo() + " iniciado (ID: " + pagamentoIniciado.getId() + ").");
            System.out.println("Por favor, finalize este pagamento na opção 'Finalizar Pagamento Pendente'.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao iniciar pagamento: " + e.getMessage());
        }
    }

    private void finalizarPagamentoPendente() {
        System.out.println("\n--- FINALIZAR PAGAMENTO PENDENTE ---");
        System.out.print("Digite o ID do Pagamento a ser finalizado: ");
        int idPagamento = -1;
        try {
            idPagamento = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número.");
            scanner.nextLine();
            return;
        }

        Optional<Pagamento> pagamentoOpt = pagamentoService.buscarPagamentoPorId(idPagamento);
        if (pagamentoOpt.isEmpty()) {
            System.out.println("Pagamento com ID " + idPagamento + " não encontrado.");
            return;
        }
        Pagamento pagamento = pagamentoOpt.get();
        
        if (pagamento.getDataHora() != null) { // Já foi finalizado
            System.out.println("Pagamento ID " + idPagamento + " já foi finalizado em " + pagamento.getDataHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ".");
            return;
        }

        // Recupera a OS para exibir o valor total
        Optional<OrdemServico> osOpt = pagamento.getIdOrdemServico().isPresent() ?
                                        ordemServicoService.buscarOrdemServicoPorId(pagamento.getIdOrdemServico().get()) : Optional.empty();
        BigDecimal valorTotalDevido = BigDecimal.ZERO;
        if (osOpt.isPresent()) {
            valorTotalDevido = ordemServicoService.calcularPrecoTotalFinalOS(osOpt.get());
            System.out.println("Valor total devido para OS " + osOpt.get().getCodigo() + ": R$ " + String.format("%.2f", valorTotalDevido));
        } else {
            System.out.println("Aviso: Ordem de Serviço relacionada não encontrada ou não definida. Valor total desconhecido.");
        }


        System.out.print("Digite o valor final pago: R$ ");
        BigDecimal valorPago = lerBigDecimalValido("");

        System.out.println("Selecione o tipo de pagamento:");
        TipoPagamento[] tipos = TipoPagamento.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i].getPagamento());
        }
        System.out.print("Opção do Tipo: ");
        int tipoOpcao = -1;
        try {
            tipoOpcao = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o tipo.");
            scanner.nextLine();
            return;
        }

        TipoPagamento tipoFinal = null;
        try {
            tipoFinal = tipos[tipoOpcao - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Opção de tipo inválida.");
            return;
        }

        try {
            if (tipoFinal == TipoPagamento.DINHEIRO) {
                if (valorPago.compareTo(valorTotalDevido) < 0) {
                     throw new IllegalArgumentException("Valor pago em dinheiro (R$ " + String.format("%.2f", valorPago) + ") é menor que o devido (R$ " + String.format("%.2f", valorTotalDevido) + ").");
                }
                BigDecimal troco = pagamentoService.calcularTroco(valorPago, valorTotalDevido);
                System.out.println("Troco: R$ " + String.format("%.2f", troco));
            } else {
                 if (valorPago.compareTo(valorTotalDevido) != 0) {
                    System.out.println("Aviso: Para pagamentos que não são em dinheiro, o valor pago (R$ " + String.format("%.2f", valorPago) + ") deveria ser igual ao devido (R$ " + String.format("%.2f", valorTotalDevido) + ").");
                 }
            }

            Pagamento pagamentoFinalizado = pagamentoService.finalizarPagamento(idPagamento, valorPago, tipoFinal);
            System.out.println("Pagamento ID " + pagamentoFinalizado.getId() + " finalizado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao finalizar pagamento: " + e.getMessage());
        }
    }

    private void listarTodosPagamentos() {
        System.out.println("\n--- LISTA DE PAGAMENTOS ---");
        List<Pagamento> pagamentos = pagamentoService.listarTodosPagamentos();
        if (pagamentos.isEmpty()) {
            System.out.println("Nenhum pagamento registrado.");
        } else {
            pagamentos.forEach(System.out::println);
        }
    }

    // --- Métodos Auxiliares de Leitura de Input ---
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
            System.out.print(prompt);
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
                // NÃO CHAME scanner.nextLine() AQUI! O input já foi lido no prompt.
            }
        }
    }
}