/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.Scanner;
import models.Usuario;
import repository.UsuarioCRUD;
import service.RegistroPontoService;
import util.UserSession;

/**
 *
 * @author marcos_miller
 */
public class PainelPrincipal {

    private Usuario usuarioLogado;
    private UsuarioCRUD usuarioCRUD;
    private RegistroPontoService pontoService;
    private Scanner scanner;

    private ComponentePonto componentePonto;
    // Futuramente: ComponenteOrdemServicoEspecializada componenteOSEspecializada;

    /**
     * Construtor do PainelPrincipal.
     * Recebe as dependências necessárias para suas operações.
     * @param usuarioCRUD O CRUD de usuários.
     * @param pontoService O serviço de negócio para o registro de ponto.
     * @param scanner O scanner para entrada do usuário.
     */
    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner) {
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        
        // Recupera o usuário logado da sessão, que já foi definido no Main após o login
        this.usuarioLogado = UserSession.getInstance().getLoggedInUser();
        
        // Inicializa os componentes visuais que fazem parte do painel
        this.componentePonto = new ComponentePonto(pontoService, scanner);
        // Futuramente: this.componenteOSEspecializada = new ComponenteOrdemServicoEspecializada(osService, scanner);

        // Verificação de segurança: se por algum motivo o usuário não estiver logado, encerra.
        // Isso não deveria acontecer se o login na Main for bem-sucedido.
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Tentativa de exibir PainelPrincipal sem usuário logado. Encerrando.");
            System.exit(1);
        }
    }

    /**
     * Inicia e exibe o loop principal do painel.
     * Gerencia a interação com o usuário através dos componentes e menus específicos.
     */
    public void exibirPainel() {
        int opcao;
        do {
            System.out.println("\n=============================================");
            System.out.println("--- PAINEL PRINCIPAL: " + usuarioLogado.getTipo().getDescricao().toUpperCase() + " ---");
            System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            System.out.println("---------------------------------------------");

            // 1. Exibir e Processar o Componente de Ponto (visível para todos os tipos de usuário)
            int opcaoPonto = componentePonto.exibirStatusEPedirAcao();
            if (opcaoPonto == 8 || opcaoPonto == 9) { // Se a opção for de ponto
                componentePonto.processarAcaoPonto(opcaoPonto, usuarioLogado);
            } else if (opcaoPonto != -1) { // Se não é 8, 9, nem -1 (erro de input)
                System.out.println("Opção de ponto não reconhecida. Prosseguindo para o menu principal...");
            }
            System.out.println("---------------------------------------------"); // Separador

            // 2. Exibir a Lista de O.S. Especializadas (visível para todos, mas conteúdo dinâmico)
            exibirOrdensDeServicoEspecializadas(); // Lógica para filtrar por TipoUsuario estará aqui
            System.out.println("---------------------------------------------"); // Separador

            // 3. Exibir o Menu de Opções Específico para o Tipo de Usuário
            exibirMenuOpcoesPorTipo();

            System.out.print("Escolha uma opção do menu principal ou '0' para sair: ");
            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consome a nova linha
            } catch (java.util.InputMismatchException e) {
                System.err.println("Entrada inválida para a opção do menu. Por favor, digite um número.");
                scanner.nextLine(); // Limpa o buffer
                opcao = -1; // Opção inválida para repetir o loop
            }

            // 4. Processar a Opção Escolhida no Menu Principal
            processarOpcaoMenu(opcao);

        } while (opcao != 0); // Loop continua até o usuário escolher sair (opção 0)
        
        // Ao sair do PainelPrincipal (opção 0), faz logout da sessão
        UserSession.getInstance().logout();
        System.out.println("Saindo do Painel " + usuarioLogado.getTipo().getDescricao() + ". Até mais!");
    }

    // --- Métodos Privados para Orquestração dos Componentes ---

    private void exibirOrdensDeServicoEspecializadas() {
        // A lógica de filtragem da lista de OSs será baseada no usuarioLogado.getTipo()
        // Por enquanto, apenas uma mensagem
        System.out.println("\n[ORDENS DE SERVIÇO ATRIBUÍDAS / EM ABERTO]");
        System.out.println("Nenhuma Ordem de Serviço exibida ainda (Lógica a ser implementada futuramente).");
    }

    private void exibirMenuOpcoesPorTipo() {
        System.out.println("\n[MENU DE OPÇÕES]");
        switch (usuarioLogado.getTipo()) { // O switch que define quais opções exibir
            case ATENDENTE:
                System.out.println("1. Gerenciar Agendamentos");
                System.out.println("2. Gerar Nova Ordem de Serviço");
                System.out.println("3. Processar Pagamento");
                break;
            case MECANICO:
                System.out.println("1. Visualizar Minhas Ordens de Serviço");
                System.out.println("2. Registrar Diagnóstico");
                System.out.println("3. Registrar Execução de Serviço");
                break;
            case GERENTE:
                System.out.println("1. Gerenciar Usuários");
                System.out.println("2. Gerenciar Estoque");
                System.out.println("3. Acessar Relatórios Financeiros");
                break;
            default:
                System.out.println("Nenhuma opção disponível para este tipo de usuário.");
                break;
        }
        System.out.println("0. Sair do Painel"); // Opção comum a todos os painéis
    }

    private void processarOpcaoMenu(int opcao) {
        // Se a opção for 0, o loop principal termina
        if (opcao == 0) {
            return; // Retorna do método, e o loop do-while em exibirPainel() encerra
        }

        switch (usuarioLogado.getTipo()) { // O switch que decide qual menu/lógica de processamento chamar
            case ATENDENTE:
                // Chamaria o MenuAtendente (futuramente)
                // Exemplo: new MenuAtendente(usuarioCRUD, scanner, agendamentoService, osService).exibirMenu();
                processarMenuAtendente(opcao); // Por enquanto, usa o método interno para placeholders
                break;
            case MECANICO:
                // Chamaria o MenuMecanico (futuramente)
                // Exemplo: new MenuMecanico(usuarioCRUD, scanner, osService).exibirMenu();
                processarMenuMecanico(opcao); // Por enquanto, usa o método interno para placeholders
                break;
            case GERENTE:
                // Chama o MenuGerente real que já existe e tem seu próprio loop
                MenuGerente menuGerente = new MenuGerente(usuarioCRUD, scanner);
                menuGerente.exibirMenu(); // Entra no loop do MenuGerente
                break; // Após sair do MenuGerente, retorna ao loop do PainelPrincipal
            default:
                System.out.println("Opção inválida para este tipo de usuário.");
                break;
        }
    }

    // --- MÉTODOS PARA PROCESSAMENTO TEMPORÁRIO DE MENUS (REMOVER QUANDO CRIAR CLASSES DE MENU DEDICADAS) ---
    // Estes métodos serão movidos para as respectivas classes MenuAtendente, MenuMecanico, etc.

    private void processarMenuAtendente(int opcao) {
        switch (opcao) {
            case 1: System.out.println("Funcionalidade 'Gerenciar Agendamentos' (Atendente) ainda não implementada."); break;
            case 2: System.out.println("Funcionalidade 'Gerar Nova Ordem de Serviço' (Atendente) ainda não implementada."); break;
            case 3: System.out.println("Funcionalidade 'Processar Pagamento' (Atendente) ainda não implementada."); break;
            default: System.out.println("Opção inválida para Atendente."); break;
        }
    }

    private void processarMenuMecanico(int opcao) {
        switch (opcao) {
            case 1: System.out.println("Funcionalidade 'Visualizar Minhas Ordens de Serviço' (Mecânico) ainda não implementada."); break;
            case 2: System.out.println("Funcionalidade 'Registrar Diagnóstico' (Mecânico) ainda não implementada."); break;
            case 3: System.out.println("Funcionalidade 'Registrar Execução de Serviço' (Mecânico) ainda não implementada."); break;
            default: System.out.println("Opção inválida para Mecânico."); break;
        }
    }

    // O método processarMenuGerente original foi substituído por uma chamada direta a MenuGerente
    // no processarOpcaoMenu, então este método específico pode ser removido ou não existiria mais.
    // Não listamos ele aqui, pois a chamada direta o substituiu.
}