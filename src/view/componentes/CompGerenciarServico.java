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
import models.ItemEstoque;
import models.OrdemServico;
import models.Servico;
import models.enums.SetorServico;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.ServicoService;

/**
 *
 * @author marcos_miller
 */
public class CompGerenciarServico {

    private OrdemServico ordemDeServicoContexto;
    private ServicoService servicoService;
    private ItemEstoqueService itemEstoqueService;
    private OrdemServicoService ordemServicoService;
    private Scanner scanner;

    public CompGerenciarServico(OrdemServico ordemDeServicoContexto, ServicoService servicoService,
                                ItemEstoqueService itemEstoqueService, OrdemServicoService ordemServicoService, Scanner scanner) {
        this.ordemDeServicoContexto = ordemDeServicoContexto;
        this.servicoService = servicoService;
        this.itemEstoqueService = itemEstoqueService;
        this.ordemServicoService = ordemServicoService;
        this.scanner = scanner;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Serviços para OS: " + ordemDeServicoContexto.getCodigo() + " =====");
            System.out.println("1. Adicionar Serviço a esta OS");
            System.out.println("2. Remover Serviço desta OS");
            System.out.println("3. Atualizar Serviço nesta OS");
            System.out.println("4. Ver Detalhes dos Serviços desta OS");
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
            case 1: adicionarServicoAOrdem(); break;
            case 2: removerServicoDaOrdem(); break;
            case 3: atualizarServicoNestaOrdem(); break;
            case 4: verDetalhesServicosDaOrdem(); break;
            case 0: System.out.println("Saindo do Gerenciamento de Serviços da OS."); break;
            default: System.out.println("Opção inválida. Tente novamente."); break;
        }
    }

    private void adicionarServicoAOrdem() {
        System.out.println("\n--- ADICIONAR SERVIÇO À OS " + ordemDeServicoContexto.getCodigo() + " ---");
        
        System.out.print("O serviço já existe no sistema (pronto para usar)? (S/N): ");
        String respServicoExistente = scanner.nextLine().trim().toUpperCase();
        Servico servicoAAcionar = null;

        if (respServicoExistente.equals("S")) {
            Optional<Servico> servicoOpt = solicitarServicoExistenteDoSistema();
            if (servicoOpt.isPresent()) {
                servicoAAcionar = servicoOpt.get();
            } else {
                System.out.println("Serviço não encontrado no sistema. Por favor, tente novamente ou cadastre um novo serviço.");
                return;
            }
        } else if (respServicoExistente.equals("N")) {
            Optional<Servico> novoServicoOpt = solicitarDadosNovoServicoParaSistema();
            if (novoServicoOpt.isPresent()) {
                servicoAAcionar = novoServicoOpt.get();
            } else {
                System.out.println("Cadastro de serviço falhou. Abortando adição à OS.");
                return;
            }
        } else {
            System.out.println("Resposta inválida. Abortando adição de serviço à OS.");
            return;
        }

        try {
            // Este método NO ORDEMSERVICOSERVICE JÁ CHAMA ordemServicoRepository.atualizarOrdemServico(os)
            ordemServicoService.adicionarServicoNaOrdem(ordemDeServicoContexto.getId(), servicoAAcionar);
            System.out.println("Serviço '" + servicoAAcionar.getDescricao() + "' adicionado à OS " + ordemDeServicoContexto.getCodigo() + " com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao adicionar serviço à OS: " + e.getMessage());
        }
    }

    private void removerServicoDaOrdem() {
        System.out.println("\n--- REMOVER SERVIÇO DA OS " + ordemDeServicoContexto.getCodigo() + " ---");
        List<Servico> servicosNaOS = ordemDeServicoContexto.getServicos();

        if (servicosNaOS.isEmpty()) {
            System.out.println("Esta Ordem de Serviço não possui serviços para remover.");
            return;
        }

        System.out.println("\nServiços atualmente nesta Ordem de Serviço:");
        for (int i = 0; i < servicosNaOS.size(); i++) {
            System.out.println((i + 1) + ". " + servicosNaOS.get(i).getDescricao() + " (Código: " + servicosNaOS.get(i).getCodigo() + ")");
        }
        System.out.print("Digite o NÚMERO do serviço a ser removido (da lista acima): ");
        int opcaoServico = lerInteiroValido();

        Servico servicoARemover = null;
        try {
            servicoARemover = servicosNaOS.get(opcaoServico - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Opção de serviço inválida.");
            return;
        }

        try {
            // Este método NO ORDEMSERVICOSERVICE JÁ CHAMA ordemServicoRepository.atualizarOrdemServico(os)
            ordemServicoService.removerServicoDaOrdem(ordemDeServicoContexto.getId(), servicoARemover);
            System.out.println("Serviço '" + servicoARemover.getDescricao() + "' removido da OS " + ordemDeServicoContexto.getCodigo() + " com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao remover serviço da OS: " + e.getMessage());
        }
    }
    
    private void atualizarServicoNestaOrdem() {
        System.out.println("\n--- ATUALIZAR SERVIÇO NA OS " + ordemDeServicoContexto.getCodigo() + " ---");
        List<Servico> servicosNaOS = ordemDeServicoContexto.getServicos();

        if (servicosNaOS.isEmpty()) {
            System.out.println("Esta Ordem de Serviço não possui serviços para atualizar.");
            return;
        }

        System.out.println("\nServiços atualmente nesta Ordem de Serviço:");
        for (int i = 0; i < servicosNaOS.size(); i++) {
            System.out.println((i + 1) + ". " + servicosNaOS.get(i).toString());
        }
        System.out.print("Digite o NÚMERO do serviço a ser atualizado (da lista acima): ");
        int opcaoServico = lerInteiroValido();

        Servico servicoAAtualizar = null;
        try {
            servicoAAtualizar = servicosNaOS.get(opcaoServico - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Opção de serviço inválida.");
            return;
        }

        System.out.println("\n--- ATUALIZAR DADOS DO SERVIÇO '" + servicoAAtualizar.getDescricao() + "' ---");
        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Novo Código (" + servicoAAtualizar.getCodigo() + "): "); String novoCodigo = scanner.nextLine();
        if (novoCodigo.isEmpty()) { novoCodigo = servicoAAtualizar.getCodigo(); }
        System.out.print("Nova Descrição (" + servicoAAtualizar.getDescricao() + "): "); String novaDescricao = scanner.nextLine();
        if (novaDescricao.isEmpty()) { novaDescricao = servicoAAtualizar.getDescricao(); }
        System.out.print("Novo Preço (" + servicoAAtualizar.getPreco() + "): "); String precoStr = scanner.nextLine();
        BigDecimal novoPreco = precoStr.isEmpty() ? servicoAAtualizar.getPreco() : new BigDecimal(precoStr);
        SetorServico novoSetor = solicitarSetorServico(servicoAAtualizar.getSetor());
        System.out.print("Novo ID da Peça de Estoque (Atual: " + servicoAAtualizar.getIdItemEstoquePeca() + ", 0 para nenhuma): "); int novoIdPeca = lerInteiroValido();
        System.out.print("Novo Requer Elevador de Alinhamento (Atual: " + servicoAAtualizar.requerElevadorAlinhamento() + ", true/false)? ");
        String requerAlinhamentoStr = scanner.nextLine();
        boolean novoRequerAlinhamento = requerAlinhamentoStr.isEmpty() ? servicoAAtualizar.requerElevadorAlinhamento() : requerAlinhamentoStr.equalsIgnoreCase("true");

        try {
            // Chama o ServicoService para atualizar o serviço no sistema (se ele for persistido globalmente)
            boolean sucessoServicoSistema = servicoService.atualizarServico(servicoAAtualizar.getId(), novoCodigo, novaDescricao, novoPreco, novoSetor, novoIdPeca, novoRequerAlinhamento);
            
            // REMOVIDO: A chamada redundante a ordemServicoService.atualizarOrdemServico(ordemDeServicoContexto);
            // pois o objeto Servico está sendo atualizado por referência e o OrdemServicoService
            // já chama o repositório em suas operações de adicionar/remover/etc.
            
            if (sucessoServicoSistema) {
                 System.out.println("Serviço '" + servicoAAtualizar.getDescricao() + "' atualizado com sucesso na OS " + ordemDeServicoContexto.getCodigo() + "!");
            } else {
                 System.out.println("Atenção: Serviço atualizado na OS, mas falha ao atualizar no registro global de serviços (se houver).");
            }
           
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao atualizar serviço na OS: " + e.getMessage());
        }
    }

    private void verDetalhesServicosDaOrdem() {
        System.out.println("\n--- DETALHES DOS SERVIÇOS NA OS " + ordemDeServicoContexto.getCodigo() + " ---");
        List<Servico> servicosNaOS = ordemDeServicoContexto.getServicos();
        if (servicosNaOS.isEmpty()) {
            System.out.println("Esta Ordem de Serviço não possui serviços adicionados.");
        } else {
            for (Servico servico : servicosNaOS) {
                String pecaInfo = "Sem peça";
                if (servico.getIdItemEstoquePeca() > 0) {
                    Optional<ItemEstoque> pecaOpt = itemEstoqueService.buscarItemPorId(servico.getIdItemEstoquePeca());
                    pecaInfo = pecaOpt.isPresent() ? pecaOpt.get().getNome() : "Peça Desconhecida";
                }
                System.out.printf("    - [Serviço ID:%d] %s (Setor: %s) - R$ %.2f - Requer Alinhamento: %b - Peça: %s%n",
                                servico.getId(), servico.getDescricao(), servico.getSetor().getDescricao(),
                                servico.getPreco(), servico.requerElevadorAlinhamento(), pecaInfo);
            }
        }
        System.out.println("---------------------------------------------");
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

    private BigDecimal lerBigDecimalValido() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
            }
        }
    }

    // --- Métodos Auxiliares para Coleta/Busca de Serviço (para sistema global) ---
    private Optional<Servico> solicitarServicoExistenteDoSistema() {
        System.out.print("Digite o Código do Serviço existente no sistema: ");
        String codigo = scanner.nextLine();
        return servicoService.buscarServicoPorCodigo(codigo);
    }

    private Optional<Servico> solicitarDadosNovoServicoParaSistema() {
        System.out.println("--- NOVO CADASTRO DE SERVIÇO PARA O SISTEMA ---");
        System.out.print("Código do Serviço: "); String codigo = scanner.nextLine();
        System.out.print("Descrição do Serviço: "); String descricao = scanner.nextLine();
        System.out.print("Preço do Serviço (Ex: 123.45): "); BigDecimal preco = lerBigDecimalValido();
        SetorServico setor = solicitarSetorServico(null);
        
        System.out.print("ID da Peça de Estoque associada (0 para nenhuma): "); int idItemEstoquePeca = lerInteiroValido();
        
        System.out.print("Requer Elevador de Alinhamento (true/false)? "); boolean requerAlinhamento = scanner.nextLine().equalsIgnoreCase("true");

        try {
            Servico novoServico = servicoService.adicionarServico(codigo, descricao, preco, setor, idItemEstoquePeca, requerAlinhamento);
            System.out.println("Serviço '" + novoServico.getDescricao() + "' cadastrado no sistema com sucesso!");
            return Optional.of(novoServico);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao cadastrar serviço: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Métodos Auxiliares para Seleção de Setor de Serviço
    private SetorServico solicitarSetorServico(SetorServico setorAtual) {
        System.out.println("Selecione o Setor do Serviço" + (setorAtual != null ? " (Atual: " + setorAtual.getDescricao() + ")" : "") + ":");
        SetorServico[] setores = SetorServico.values();
        for (int i = 0; i < setores.length; i++) {
            System.out.println((i + 1) + ". " + setores[i].getDescricao());
        }
        System.out.print("Opção do Setor: ");
        String input = scanner.nextLine();
        if (input.isEmpty() && setorAtual != null) {
            return setorAtual;
        }

        try {
            int opcaoSetor = Integer.parseInt(input);
            if (opcaoSetor > 0 && opcaoSetor <= setores.length) {
                return setores[opcaoSetor - 1];
            } else {
                System.err.println("Opção de setor inválida. Retornando 'Outros Serviços'.");
                return SetorServico.OUTROS;
            }
        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida para o setor. Retornando 'Outros Serviços'.");
            return SetorServico.OUTROS;
        }
    }
}