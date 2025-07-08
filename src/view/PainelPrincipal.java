/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.InputMismatchException;
import view.componentes.CompPonto;
import view.menus.MenuGerente;
import java.util.Scanner;
import models.Usuario;
import static models.enums.TipoUsuario.ATENDENTE;
import static models.enums.TipoUsuario.GERENTE;
import static models.enums.TipoUsuario.MECANICO;
import repository.UsuarioCRUD;
import service.AgendamentoService;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.PagamentoService;
import service.RegistroPontoService;
import service.RelatorioService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import util.UserSession;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompOSEspecializada;
import view.menus.MenuAtendente;
import view.menus.MenuMecanico;

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
    private ServicoService servicoService;
    private ElevadorService elevadorService;
    private AgendamentoService agendamentoService;
    private PagamentoService pagamentoService;
    private RelatorioService relatorioService;
    private Scanner scanner;
    private CompPonto compPonto;


    /**
     * Construtor do PainelPrincipal.
     * Recebe todas as dependências necessárias para suas operações.
     */
    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner,
                           OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService,
                           ItemEstoqueService itemEstoqueService, ServicoService servicoService,
                           ElevadorService elevadorService,
                           AgendamentoService agendamentoService, PagamentoService pagamentoService,
                           RelatorioService relatorioService) {
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService;
        this.elevadorService = elevadorService;
        this.agendamentoService = agendamentoService;
        this.pagamentoService = pagamentoService;
        this.relatorioService = relatorioService;
        
        this.usuarioLogado = util.UserSession.getInstance().getLoggedInUser();
        this.compPonto = new CompPonto(pontoService, scanner);
        
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Nenhum usuário logado. Encerrando.");
            System.exit(1);
        }
    }

    /**
     * Inicia o fluxo de exibição do painel principal para o usuário logado.
     * Tem um loop para o menu principal do Painel.
     */
    public void exibirPainel() {
        //primeiro o ponto
        compPonto.exibirMenuPonto(usuarioLogado);
        //depois a lista das OS
        exibirMenuOSEspecializadas();
        //e por fim o menu do usuario logado
        exibirMenuPerfil(usuarioLogado);
        
        UserSession.getInstance().logout(); 
        System.out.println("Saindo do Sistema. Até mais!");
    }

    public void exibirMenuOSEspecializadas() {
        CompOSEspecializada compOSEspecializada = new CompOSEspecializada(
            ordemServicoService, usuarioService, clienteService, veiculoService,
            itemEstoqueService, servicoService, elevadorService, scanner
        );
        compOSEspecializada.exibirMenu();
    }
    
    private void exibirMenuPerfil(Usuario usuarioLogado){
    switch (usuarioLogado.getTipo()) {
            case ATENDENTE:
                MenuAtendente menuAtendente = new MenuAtendente(
                    clienteService, itemEstoqueService, ordemServicoService, pontoService,
                    servicoService, usuarioService, veiculoService, scanner,
                    agendamentoService, pagamentoService, elevadorService
                );
                menuAtendente.exibirMenu();
                break;
            case MECANICO:
                MenuMecanico menuMecanico = new MenuMecanico(
                    itemEstoqueService, ordemServicoService, servicoService, usuarioService, scanner,
                    elevadorService, clienteService, veiculoService
                );
                menuMecanico.exibirMenu();
                break;
            case GERENTE:
                MenuGerente menuGerente = new MenuGerente(
                    usuarioCRUD, scanner, usuarioService, ordemServicoService, clienteService, veiculoService,
                    itemEstoqueService, servicoService, relatorioService, this.elevadorService
                );
                menuGerente.exibirMenu();
                break;
            default:
                System.out.println("Tipo de usuario nao reconhecido ou sem menu especifico. Encerrando.");
                break;
        }
    }
}