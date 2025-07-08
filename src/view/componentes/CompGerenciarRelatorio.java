/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Relatorio;
import service.RelatorioService;

/**
 *
 * @author marcos_miller
 */
public class CompGerenciarRelatorio {

    private RelatorioService relatorioService;
    private Scanner scanner;

    public CompGerenciarRelatorio(RelatorioService relatorioService, Scanner scanner) {
        this.relatorioService = relatorioService;
        this.scanner = scanner;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Relatórios =====");
            System.out.println("1. Gerar Relatório Diário (e Salvar)");
            System.out.println("2. Gerar Relatório Mensal (e Salvar)");
            // System.out.println("3. Gerar Relatório Semanal (e Salvar)");
            System.out.println("4. Listar Relatórios Salvos");
            System.out.println("5. Ver Detalhes de um Relatório Salvo");
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
            case 1: gerarRelatorioDiario(); break;
            case 2: gerarRelatorioMensal(); break;
            // case 3: gerarRelatorioSemanal(); break;
            case 4: listarRelatoriosSalvos(); break;
            case 5: verDetalhesRelatorioSalvo(); break;
            case 0: System.out.println("Saindo do Gerenciamento de Relatórios."); break;
            default: System.out.println("Opção inválida. Tente novamente."); break;
        }
    }

    private void gerarRelatorioDiario() {
        System.out.println("\n--- GERAR RELATÓRIO DIÁRIO ---");
        try {
            int usuarioGeradorId = util.UserSession.getInstance().getLoggedInUser().getId();
            
            Relatorio relatorio = relatorioService.gerarESalvarRelatorioDiario(usuarioGeradorId);
            System.out.println("Relatório diário gerado e salvo com sucesso! ID: " + relatorio.getId());
            System.out.println("\n--- CONTEÚDO DO RELATÓRIO ---");
            System.out.println(relatorio.getConteudo());
            System.out.println("-----------------------------");
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório diário: " + e.getMessage());
        }
    }

    private void gerarRelatorioMensal() {
        System.out.println("\n--- GERAR RELATÓRIO MENSAL ---");
        try {
            int usuarioGeradorId = util.UserSession.getInstance().getLoggedInUser().getId();
            
            Relatorio relatorio = relatorioService.gerarESalvarRelatorioMensal(usuarioGeradorId);
            System.out.println("Relatório mensal gerado e salvo com sucesso! ID: " + relatorio.getId());
            System.out.println("\n--- CONTEÚDO DO RELATÓRIO ---");
            System.out.println(relatorio.getConteudo());
            System.out.println("-----------------------------");
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório mensal: " + e.getMessage());
        }
    }

    private void listarRelatoriosSalvos() {
        System.out.println("\n--- LISTA DE RELATÓRIOS SALVOS ---");
        List<Relatorio> relatorios = relatorioService.listarTodosRelatorios();
        if (relatorios.isEmpty()) {
            System.out.println("Nenhum relatório salvo.");
        } else {
            relatorios.forEach(System.out::println);
        }
    }

    private void verDetalhesRelatorioSalvo() {
        System.out.println("\n--- VER DETALHES DE UM RELATÓRIO SALVO ---");
        System.out.print("Digite o ID do relatório para ver detalhes: ");
        int idRelatorio = -1;
        try {
            idRelatorio = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o ID.");
            scanner.nextLine();
            return;
        }

        Optional<Relatorio> relatorioOpt = relatorioService.buscarRelatorioPorId(idRelatorio);
        if (relatorioOpt.isEmpty()) {
            System.out.println("Relatório com ID " + idRelatorio + " não encontrado.");
        } else {
            Relatorio relatorio = relatorioOpt.get();
            System.out.println("\n--- DETALHES DO RELATÓRIO ID " + relatorio.getId() + " ---");
            System.out.println("Título: " + relatorio.getTitulo());
            System.out.println("Tipo: " + relatorio.getTipo().getDescricao());
            System.out.println("Data de Geração: " + relatorio.getDataGeracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            System.out.println(relatorio.getConteudo());
            System.out.println("------------------------------------------");
        }
    }

    // --- Métodos Auxiliares de Leitura de Input ---
    // Repetição dos métodos lerInteiroValido e lerBigDecimalValido
    // idealmente viriam de uma classe InputHelper ou similar para evitar duplicação.
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
}