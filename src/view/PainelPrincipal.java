/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.List;
import java.util.Optional;
import view.componentes.CompPonto;
import view.menus.MenuGerente;
import java.util.Scanner;
import models.Cliente;
import models.OrdemServico;
import models.Usuario;
import models.Veiculo;
import models.enums.StatusOrdem;
import repository.UsuarioCRUD;
import service.ClienteService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RegistroPontoService;
import service.UsuarioService;
import service.VeiculoService;
import util.UserSession;
import view.menus.MenuMecanico;
import view.componentes.CompGerenciarOS; // Adicionar import para CompGerenciarOS

/**
 *
 * @author marcos_miller
 */
public class PainelPrincipal {

    private Usuario usuarioLogado;
    private UsuarioCRUD usuarioCRUD;
    private RegistroPontoService pontoService;
    private OrdemServicoService ordemServicoService;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private UsuarioService usuarioService;
    private ItemEstoqueService itemEstoqueService;
    private Scanner scanner;

    private CompPonto componentePonto;
    private CompGerenciarOS compGerenciarOS; // Adicionar o componente aqui

    /**
     * Construtor do PainelPrincipal. Recebe todas as dependências necessárias
     * para suas operações.
     *
     * @param usuarioCRUD O CRUD de usuários.
     * @param pontoService O serviço de negócio para o registro de ponto.
     * @param scanner O scanner para entrada do usuário.
     * @param ordemServicoService O serviço de ordens de serviço.
     * @param clienteService O serviço de clientes.
     * @param veiculoService O serviço de veículos.
     * @param usuarioService O serviço de usuários.
     * @param itemEstoqueService O serviço de itens de estoque.
     */
    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner,
            OrdemServicoService ordemServicoService, ClienteService clienteService,
            VeiculoService veiculoService, UsuarioService usuarioService,
            ItemEstoqueService itemEstoqueService) {
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.itemEstoqueService = itemEstoqueService;

        this.usuarioLogado = UserSession.getInstance().getLoggedInUser();
        this.componentePonto = new CompPonto(pontoService, scanner);
        // Inicialize o CompGerenciarOS aqui, pois ele é uma dependência do MenuMecanico
        this.compGerenciarOS = new CompGerenciarOS(
                this.ordemServicoService,
                this.clienteService,
                this.veiculoService,
                this.usuarioService,
                this.scanner
        );
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Tentativa de exibir PainelPrincipal sem usuário logado. Encerrando.");
            System.exit(1);
        }
    }

    public void exibirPainel() {
        int opcao;
        do {
            System.out.println("\n=============================================");
            System.out.println("--- PAINEL PRINCIPAL: " + usuarioLogado.getTipo().getDescricao().toUpperCase() + " ---");
            System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            System.out.println("---------------------------------------------");

            // 1. Exibir e Processar o Componente de Ponto
            int opcaoPonto = componentePonto.exibirStatusEPedirAcao();
            if (opcaoPonto == 8 || opcaoPonto == 9) { // Assumindo 8 e 9 são as opções para registrar ponto
                componentePonto.processarAcaoPonto(opcaoPonto, usuarioLogado);
            } else if (opcaoPonto != -1) { // -1 significa que não houve entrada válida ou o usuário não escolheu uma ação de ponto
                // Aqui você pode decidir o que fazer se o usuário não escolher uma opção de ponto válida
                // Por enquanto, vamos apenas prosseguir para o menu principal
                // System.out.println("Opção de ponto não reconhecida. Prosseguindo para o menu principal...");
            }
            System.out.println("---------------------------------------------");

            // 2. Exibir a Lista de O.S. Especializadas (Mantido como um placeholder)
            exibirOrdensDeServicoEspecializadas();
            System.out.println("---------------------------------------------");

            // 3. O Painel Principal apenas redireciona para o menu específico do usuário logado
            // Não deve exibir as opções detalhadas de cada menu aqui, apenas a opção de "entrar" nele.
            // O loop do menu específico (MenuMecanico, MenuGerente) controlará suas próprias opções.
            // Não pedimos opção aqui, apenas chamamos o menu apropriado.
            // O "voltar" de cada menu específico retornará o controle para o PainelPrincipal.
            processarMenuPorTipoUsuario();
            opcao = 0;

        } while (opcao != 0); // O loop agora depende de como o `processarMenuPorTipoUsuario` interage com ele.
        // Uma forma simples é deixar o "0" para "Sair do Painel Principal" ser capturado aqui
        // mas o que acontece é que o menu específico já tem seu próprio loop.
        // Para uma estrutura mais robusta, cada menu pode retornar um boolean (true para continuar, false para sair).
        // Por enquanto, vamos presumir que ao sair do menu específico, voltamos ao loop do PainelPrincipal
        // e o usuário teria que digitar '0' no painel principal para sair de tudo.

        UserSession.getInstance().logout();
        System.out.println("Saindo do Painel " + usuarioLogado.getTipo().getDescricao() + ". Até mais!");
    }

    private void exibirOrdensDeServicoEspecializadas() {
        System.out.println("\n[ORDENS DE SERVIÇO ATRIBUÍDAS / EM ABERTO]");
        // Este é o lugar ideal para o mecânico ver as OSs que lhe interessam sem ter que entrar no menu.
        // Você pode listar as OSs com status "AGUARDANDO_DIAGNOSTICO" ou "EM_DIAGNOSTICO"
        // que estão atribuídas a ele, como um "painel de tarefas".
        List<OrdemServico> osDoMecanico = ordemServicoService.buscarPorMecanicoEStatus(
                usuarioLogado.getId(), StatusOrdem.AGUARDANDO_DIAGNOSTICO);
        osDoMecanico.addAll(ordemServicoService.buscarPorMecanicoEStatus(
                usuarioLogado.getId(), StatusOrdem.EM_DIAGNOSTICO));
        osDoMecanico.addAll(ordemServicoService.buscarPorMecanicoEStatus(
                usuarioLogado.getId(), StatusOrdem.EM_EXECUCAO));

        if (osDoMecanico.isEmpty()) {
            System.out.println("  Nenhuma Ordem de Serviço em aberto atribuída a você no momento.");
        } else {
            System.out.println("  Ordens de Serviço em andamento para você:");
            osDoMecanico.forEach(os -> {
                Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(os.getIdCliente());
                Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(os.getIdVeiculo());
                System.out.printf("  - OS-%d | Código: %s | Status: %s | Cliente: %s | Veículo: %s (%s)%n",
                        os.getId(),
                        os.getCodigo(),
                        os.getStatus().getDescricao(),
                        clienteOpt.map(Cliente::getNome).orElse("Desconhecido"),
                        veiculoOpt.map(Veiculo::getModelo).orElse("Desconhecido"),
                        veiculoOpt.map(Veiculo::getPlaca).orElse("Desconhecido"));
            });
        }
    }

    // REMOVA exibirMenuOpcoesPorTipo() daqui, pois os menus específicos já cuidam disso.
    private void processarMenuPorTipoUsuario() {
        switch (usuarioLogado.getTipo()) {
            case ATENDENTE:
                // Instancie e chame o MenuAtendente aqui
                System.out.println("Funcionalidade de Atendente ainda não implementada. Digite 0 para sair.");
                scanner.nextLine(); // Pausa
                break;
            case MECANICO:
                MenuMecanico menuMecanico = new MenuMecanico(
                        scanner,
                        compGerenciarOS // Apenas o CompGerenciarOS é passado agora
                );
                menuMecanico.exibirMenu();
                break;
            case GERENTE:
                MenuGerente menuGerente = new MenuGerente(
                        usuarioCRUD, scanner, usuarioService, ordemServicoService, clienteService, veiculoService,
                        itemEstoqueService
                );
                menuGerente.exibirMenu(); // Chama o menu do gerente
                break;
            default:
                System.out.println("Nenhuma opção disponível para este tipo de usuário.");
                break;
        }
    }
    // Remova os métodos processarMenuAtendente e processarMenuMecanico se ainda estiverem aqui.
}
