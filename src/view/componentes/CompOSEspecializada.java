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

    // chamado dentro de uma função para prezar o encapsulamento, senao teria q deixar o método publico.
    public void exibirMenu() {
        exibirOrdens();
    }

    /**
     * Exibe a lista de Ordens de Serviço relevantes para o usuário logado, com detalhes completos.
     * Esta lista é filtrada com base no tipo de usuário.
     */
    private void exibirOrdens() {
        System.out.println("\n--- ORDENS DE SERVIÇO EM ABERTO ---");

        List<OrdemServico> ordensFiltradas = new ArrayList<>();
        TipoUsuario tipo = usuarioLogado.getTipo();

        switch (tipo) {
            case ATENDENTE:
                ordensFiltradas = ordemServicoService.listarTodasOrdens().stream()
                    .filter(os -> os.getStatus() == StatusOrdem.AGUARDANDO_LIBERACAO || 
                                   os.getStatus() == StatusOrdem.AGUARDANDO_PAGAMENTO ||
                                   os.getStatus() == StatusOrdem.AGUARDANDO_DIAGNOSTICO)
                    .collect(Collectors.toList());
                System.out.println("Usuário: ATENDENTE");
                System.out.println("INTERESSE: Aguardando Liberação, Pagamento e Diagnóstico");
                break;
            case MECANICO:
                ordensFiltradas = ordemServicoService.listarOrdensPorMecanico(usuarioLogado.getId()).stream()
                    .filter(os -> os.getStatus() == StatusOrdem.EM_DIAGNOSTICO ||
                                   os.getStatus() == StatusOrdem.EM_EXECUCAO ||
                                   os.getStatus() == StatusOrdem.AGUARDANDO_DIAGNOSTICO)
                    .collect(Collectors.toList());
                System.out.println("Usuário: MECÂNICO");
                System.out.println("INTERESSE: Aguardando Diagnóstico, Em Diagnóstico, Execução");

                break;
            case GERENTE:
                ordensFiltradas = ordemServicoService.listarTodasOrdens();
                System.out.println("Usuário: GERENTE");
                System.out.println("INTERESSE: Todas as OS");

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