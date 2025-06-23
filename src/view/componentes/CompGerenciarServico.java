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

    private OrdemServico ordemDeServicoContexto; // A Ordem de Serviço que este componente está manipulando
    private ServicoService servicoService;       // Serviço para auxiliar na criação de instico de Servico
    private ItemEstoqueService itemEstoqueService; // Para seleção de peças
    private OrdemServicoService ordemServicoService; // Para adicionar/remover/atualizar na OS (persiste a OS)
    private Scanner scanner;

    /**
     * Construtor do CompGerenciarServico.
     * @param ordemDeServicoContexto A Ordem de Serviço na qual os serviços serão gerenciados.
     * @param servicoService O serviço de negócio para auxiliar na criação/atualização de Servico.
     * @param itemEstoqueService O serviço de itens de estoque (para selecionar peças).
     * @param ordemServicoService O serviço de ordens de serviço (para persistir mudanças na OS).
     * @param scanner O scanner para entrada do usuário.
     */
    public CompGerenciarServico(OrdemServico ordemDeServicoContexto, ServicoService servicoService,
                                ItemEstoqueService itemEstoqueService, OrdemServicoService ordemServicoService, Scanner scanner) {
        this.ordemDeServicoContexto = ordemDeServicoContexto;
        this.servicoService = servicoService;
        this.itemEstoqueService = itemEstoqueService;
        this.ordemServicoService = ordemServicoService;
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para gerenciar serviços da Ordem de Serviço em contexto.
     */
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

    // --- Métodos de Manipulação de Serviço na Ordem de Serviço Específica ---

    private void adicionarServicoAOrdem() {
        System.out.println("\n--- ADICIONAR SERVIÇO À OS " + ordemDeServicoContexto.getCodigo() + " ---");
        
        // NUNCA MAIS PERGUNTA "Serviço já existe", SEMPRE CRIA UM NOVO SERVIÇO PARA ESSA OS
        // A lógica de serviço não ter um "catálogo global" está embutida aqui.
        // O serviço é criado especificamente para esta OS.
        
        Optional<Servico> novoServicoOpt = solicitarDadosNovoServicoParaOS(); // Cria novo serviço (instância)
        if (novoServicoOpt.isEmpty()) {
            System.out.println("Criação do serviço falhou. Abortando adição à OS.");
            return;
        }
        Servico servicoAAcionar = novoServicoOpt.get();

        // Adicionar o Serviço à OS usando o OrdemServicoService
        try {
            ordemServicoService.adicionarServicoNaOrdem(ordemDeServicoContexto.getId(), servicoAAcionar);
            System.out.println("Serviço '" + servicoAAcionar.getObservacoes() + "' adicionado à OS " + ordemDeServicoContexto.getCodigo() + " com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao adicionar serviço à OS: " + e.getMessage());
        }
    }

    private void removerServicoDaOrdem() {
        System.out.println("\n--- REMOVER SERVIÇO DA OS " + ordemDeServicoContexto.getCodigo() + " ---");
        List<Servico> servicosNaOS = ordemDeServicoContexto.getServicos(); // Pega a lista de serviços da OS em contexto

        if (servicosNaOS.isEmpty()) {
            System.out.println("Esta Ordem de Serviço não possui serviços para remover.");
            return;
        }

        System.out.println("\nServiços atualmente nesta Ordem de Serviço:");
        for (int i = 0; i < servicosNaOS.size(); i++) {
            System.out.println((i + 1) + ". " + servicosNaOS.get(i).getObservacoes() + " (ID: " + servicosNaOS.get(i).getId() + ")");
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
            ordemServicoService.removerServicoDaOrdem(ordemDeServicoContexto.getId(), servicoARemover);
            System.out.println("Serviço '" + servicoARemover.getObservacoes() + "' removido da OS " + ordemDeServicoContexto.getCodigo() + " com sucesso!");
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

        System.out.println("\n--- ATUALIZAR DADOS DO SERVIÇO ID " + servicoAAtualizar.getId() + " ---");
        System.out.println("Deixe em branco para manter o valor atual.");
        System.out.println("Descrição (Atual: " + servicoAAtualizar.getObservacoes() + "): "); String novasObservacoes = scanner.nextLine();
        if (novasObservacoes.isEmpty()) { novasObservacoes = servicoAAtualizar.getObservacoes(); }

        System.out.print("Preço Mão de Obra (Atual: " + servicoAAtualizar.getPrecoMaoDeObra() + "): "); String precoStr = scanner.nextLine();
        BigDecimal novoPreco = precoStr.isEmpty() ? servicoAAtualizar.getPrecoMaoDeObra() : lerBigDecimalValido(precoStr); // Usa auxiliar para BigDecimal
        SetorServico novoSetor = solicitarSetorServico(servicoAAtualizar.getSetor());
        
        System.out.print("Código da Peça de Estoque (Atual: " + (servicoAAtualizar.getCodigoPeca() != null ? servicoAAtualizar.getCodigoPeca() : "Nenhuma") + ", deixe em branco para manter, '0' para remover): ");
        String novoCodigoPeca = scanner.nextLine();
        if (novoCodigoPeca.isEmpty()) { // Mantém o atual
            novoCodigoPeca = servicoAAtualizar.getCodigoPeca();
        } else if (novoCodigoPeca.equals("0")) { // Remove a peça
            novoCodigoPeca = null;
        }
        
        System.out.print("Quantidade da Peça (Atual: " + servicoAAtualizar.getQuantidadePeca() + "): ");
        String qtdPecaStr = scanner.nextLine();
        int novaQuantidadePeca = qtdPecaStr.isEmpty() ? servicoAAtualizar.getQuantidadePeca() : Integer.parseInt(qtdPecaStr);

        // Requer Prioridade é definido pelo Setor, não é perguntado ao usuário
        boolean novoRequerPrioridade = (novoSetor == SetorServico.PNEUS_RODAS); // Lógica do sistema

        try {
            // Chama o ServicoService para ATUALIZAR A INSTÂNCIA do serviço
            // O ServicoService vai validar e atualizar o objeto servicoAAtualizar (que é referência na OS)
            boolean sucessoServicoAtualizado = servicoService.atualizarInstanciaServico(
                servicoAAtualizar.getId(), novoPreco, novoSetor, novoCodigoPeca, novaQuantidadePeca, novasObservacoes
            );
            
            // Depois de atualizar o objeto Servico em memória, precisamos persistir a Ordem de Serviço
            // para que a mudança no Servico seja salva no JSON da OS.
            ordemServicoService.atualizarOrdemServico(ordemDeServicoContexto);
            
            if (sucessoServicoAtualizado) {
                 System.out.println("Serviço '" + novasObservacoes + "' atualizado com sucesso na OS " + ordemDeServicoContexto.getCodigo() + "!");
            } else {
                 System.out.println("Atenção: Serviço atualizado na OS, mas falha no serviçoService.atualizarInstanciaServico.");
            }
           
        } catch (IllegalArgumentException | IllegalStateException e) {
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
                if (servico.getCodigoPeca() != null && !servico.getCodigoPeca().isEmpty()) {
                    Optional<ItemEstoque> pecaOpt = itemEstoqueService.buscarItemPorCodigo(servico.getCodigoPeca());
                    pecaInfo = pecaOpt.isPresent() ? pecaOpt.get().getNome() : "Peça Desconhecida";
                }
                // Usando servico.getObservacoes() e servico.requerPrioridade()
                System.out.printf("    - [Serviço ID:%d] %s (Setor: %s) - R$ %.2f - Requer Prioridade: %b - Peça: %s%n",
                                servico.getId(), servico.getObservacoes(), servico.getSetor().getDescricao(),
                                servico.getPrecoMaoDeObra(), servico.requerPrioridade(), pecaInfo);
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

    private BigDecimal lerBigDecimalValido(String input) { // Recebe o input para tentar parsear
        while (true) {
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
                input = scanner.nextLine(); // Pede nova entrada se a primeira falhou
            }
        }
    }

    // --- Métodos Auxiliares para Coleta/Criação de Serviço (NOVO) ---
    // Este método cria uma nova instância de Serviço, sem adicioná-lo ao ServicoRepository.
    private Optional<Servico> solicitarDadosNovoServicoParaOS() {
        System.out.println("--- CRIAR NOVO SERVIÇO PARA ESTA ORDEM DE SERVIÇO ---");
        System.out.print("Descrição/Observações do Serviço: "); String observacoes = scanner.nextLine();
        System.out.print("Preço da Mão de Obra (Ex: 123.45): "); BigDecimal precoMaoDeObra = lerBigDecimalValido(scanner.nextLine());
        SetorServico setor = solicitarSetorServico(null); // Solicita setor, sem padrão inicial
        
        System.out.print("Código da Peça de Estoque associada (deixe em branco para nenhuma): "); String codigoPeca = scanner.nextLine();
        
        System.out.print("Quantidade da Peça usada: "); int quantidadePeca = lerInteiroValido();

        // O 'requerPrioridade' é definido automaticamente pelo setor
        boolean requerPrioridade = (setor == SetorServico.PNEUS_RODAS); 

        try {
            // O ServicoService.criarInstanciaServico APENAS CRIA E VALIDA, não persiste no repo de serviços.
            Servico novoServico = servicoService.criarInstanciaServico(precoMaoDeObra, setor, codigoPeca, quantidadePeca, observacoes);
            System.out.println("Serviço pronto para ser adicionado à OS.");
            return Optional.of(novoServico);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao criar serviço: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Métodos Auxiliares para Seleção de Setor de Serviço (EXISTENTES, mas movidos para cá)
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