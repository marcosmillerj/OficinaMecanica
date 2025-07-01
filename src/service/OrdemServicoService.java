/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import models.Cliente;
import models.Elevador;
import models.OrdemServico;
import models.Servico;
import models.Usuario;
import models.Veiculo;
import models.enums.StatusOrdem;
import repository.OrdemServicoRepository;
import repository.UsuarioCRUD;

/**
 *
 * @author marcos_miller
 */
public class OrdemServicoService {

    private OrdemServicoRepository ordemServicoRepository;
    private UsuarioCRUD usuarioCRUD;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private ServicoService servicoService;
    private ElevadorService elevadorService;

    /**
     * Construtor do OrdemServicoService.
     * @param ordemServicoRepository O repositório de Ordens de Serviço.
     * @param usuarioCRUD O CRUD de usuários (para acessar mecânicos).
     * @param clienteService O serviço de clientes.
     * @param veiculoService O serviço de veículos.
     * @param servicoService O serviço de serviços.
     * @param elevadorService O serviço de elevadores.
     * // REMOVIDO: @param scanner O scanner para interação com o usuário.
     */
    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               UsuarioCRUD usuarioCRUD,
                               ClienteService clienteService,
                               VeiculoService veiculoService,
                               ServicoService servicoService,
                               ElevadorService elevadorService) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository, "OrdemServicoRepository não pode ser nulo.");
        this.usuarioCRUD = Objects.requireNonNull(usuarioCRUD, "UsuarioCRUD não pode ser nulo.");
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService não pode ser nulo.");
        this.veiculoService = Objects.requireNonNull(veiculoService, "VeiculoService não pode ser nulo.");
        this.servicoService = Objects.requireNonNull(servicoService, "ServicoService não pode ser nulo.");
        this.elevadorService = Objects.requireNonNull(elevadorService, "ElevadorService não pode ser nulo.");
    }

    public OrdemServico criarNovaOrdemServico(int idCliente, int idVeiculo, int idMecanicoResponsavel)
                                              throws IllegalArgumentException {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) { throw new IllegalArgumentException("Cliente com ID " + idCliente + " não encontrado."); }
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) { throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado."); }
        Optional<Usuario> mecanicoOpt = usuarioCRUD.buscarUsuarioPorIdOptional(idMecanicoResponsavel);
        if (mecanicoOpt.isEmpty() || mecanicoOpt.get().getTipo() != models.enums.TipoUsuario.MECANICO) {
            throw new IllegalArgumentException("Mecânico com ID " + idMecanicoResponsavel + " não encontrado ou não é um mecânico válido.");
        }
        
        String codigoOS = "OS-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmmss")) + "-" + UUID.randomUUID().toString().substring(0, 4);

        OrdemServico novaOS = new OrdemServico(
            codigoOS,
            LocalDateTime.now(),
            idVeiculo,
            idCliente,
            idMecanicoResponsavel,
            StatusOrdem.AGUARDANDO_DIAGNOSTICO 
        );

        ordemServicoRepository.adicionarOrdemServico(novaOS);
        return novaOS;
    }

    public void atualizarOrdemServico(OrdemServico ordemParaAtualizar) {
        Objects.requireNonNull(ordemParaAtualizar, "Ordem de Serviço para atualizar não pode ser nula.");
        ordemServicoRepository.atualizarOrdemServico(ordemParaAtualizar);
    }

    /**
     * Altera o status de uma Ordem de Serviço.
     * Integra a lógica de alocação/liberação de elevador.
     *
     * @param idOs ID da Ordem de Serviço.
     * @param novoStatus O novo status a ser aplicado.
     * @param idElevadorParaAlocar Optional contendo o ID do elevador escolhido PELA VIEW (se for alocar).
     * @return A OrdemServico atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada.
     * @throws IllegalStateException Se a transição de status não for permitida ou se houver problemas com elevador.
     */
    public OrdemServico alterarStatusOrdemServico(int idOs, StatusOrdem novoStatus, Optional<Integer> idElevadorParaAlocar) {
        Objects.requireNonNull(idElevadorParaAlocar, "Optional de idElevadorParaAlocar não pode ser nulo.");
        Optional<OrdemServico> osOpt = ordemServicoRepository.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) { throw new IllegalArgumentException("Ordem de Serviço com ID " + idOs + " não encontrada."); }
        OrdemServico os = osOpt.get();

        Objects.requireNonNull(novoStatus, "Novo status não pode ser nulo.");

        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA) {
            throw new IllegalStateException("Não é possível alterar o status de uma OS " + os.getStatus().getDescricao() + " (já está finalizada ou cancelada).");
        }
        
        if ((novoStatus == StatusOrdem.EM_DIAGNOSTICO || novoStatus == StatusOrdem.EM_EXECUCAO)) {
            if (!(os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO)) {
                if (idElevadorParaAlocar.isEmpty()) {
                    throw new IllegalArgumentException("É necessário especificar um elevador para alocar a OS para o status " + novoStatus.getDescricao() + ".");
                }
                int elevadorId = idElevadorParaAlocar.get();
                
                try {
                    Elevador elevadorAlocado = elevadorService.alocarElevador(os.getId(), os.getIdVeiculo(), os.getServicos(), elevadorId);
                    os.setIdElevadorAtual(Optional.of(elevadorAlocado.getId()));
                } catch (IllegalArgumentException | IllegalStateException e) {
                    throw e;
                }
            }
        } 
        
        else if ((os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO) &&
                 (novoStatus == StatusOrdem.FINALIZADA || novoStatus == StatusOrdem.AGUARDANDO_PAGAMENTO ||
                  novoStatus == StatusOrdem.CANCELADA || novoStatus == StatusOrdem.AGUARDANDO_DIAGNOSTICO ||
                  novoStatus == StatusOrdem.AGUARDANDO_LIBERACAO )) { 
            
            
            if (os.getIdElevadorAtual().isPresent()) { 
                boolean liberado = elevadorService.liberarElevador(os.getIdElevadorAtual().get()); 
                if (liberado) {
                    os.setIdElevadorAtual(Optional.empty()); 
                } else {
                    System.out.println("Aviso: Falha ao liberar elevador ID " + os.getIdElevadorAtual().get() + " para OS " + os.getCodigo() + ". Pode já estar livre ou não encontrado.");
                }
            } else {
                System.out.println("Aviso: OS " + os.getCodigo() + " mudou de status de 'ocupação' mas não estava com elevador alocado. Nenhuma ação necessária.");
            }
        }

        os.alterarStatus(novoStatus);
        atualizarOrdemServico(os);
        return os;
    }
    
    /**
     * Adiciona um serviço a uma Ordem de Serviço existente e recalcula o total.
     * @param idOs ID da Ordem de Serviço.
     * @param servico O objeto Servico a ser adicionado.
     * @return A OrdemServico atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada ou o serviço for nulo.
     * @throws IllegalStateException Se a OS não estiver em um status que permite adição de serviços.
     */
    public OrdemServico adicionarServicoNaOrdem(int idOs, Servico servico)
                                               throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(servico, "Serviço a ser adicionado não pode ser nulo.");

        Optional<OrdemServico> osOpt = ordemServicoRepository.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) { throw new IllegalArgumentException("Ordem de Serviço com ID " + idOs + " não encontrada."); }
        OrdemServico os = osOpt.get();

        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA || os.getStatus() == StatusOrdem.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Não é possível adicionar serviços a uma OS com status " + os.getStatus().getDescricao() + ".");
        }
        os.adicionarServico(servico);
        atualizarOrdemServico(os);
        return os;
    }
    
    /**
     * Remove um serviço de uma Ordem de Serviço existente e recalcula o total.
     * @param idOs ID da Ordem de Serviço.
     * @param servico O objeto Servico a ser removido.
     * @return A OrdemServico atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada ou o serviço for nulo ou não estiver na OS.
     * @throws IllegalStateException Se a OS não estiver em um status que permite remoção de serviços.
     */
    public OrdemServico removerServicoDaOrdem(int idOs, Servico servico)
                                            throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(servico, "Serviço a ser removido não pode ser nulo.");

        Optional<OrdemServico> osOpt = ordemServicoRepository.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) { throw new IllegalArgumentException("Ordem de Serviço com ID " + idOs + " não encontrada."); }
        OrdemServico os = osOpt.get();

        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA || os.getStatus() == StatusOrdem.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Não é possível remover serviços de uma OS com status " + os.getStatus().getDescricao() + ".");
        }
        
        if (!os.removerServico(servico)) {
            throw new IllegalArgumentException("Serviço '" + servico.getObservacoes() + "' não encontrado na Ordem de Serviço " + os.getCodigo() + ".");
        }
        atualizarOrdemServico(os);
        return os;
    }

    /**
     * Busca uma Ordem de Serviço pelo ID.
     * @param idOs ID da Ordem de Serviço.
     * @return Um Optional contendo a Ordem de Serviço, ou vazio se não encontrada.
     */
    public Optional<OrdemServico> buscarOrdemServicoPorId(int idOs) {
        return ordemServicoRepository.buscarOrdemServicoPorId(idOs);
    }

    /**
     * Lista todas as Ordens de Serviço.
     * @return Uma lista de todas as Ordens de Serviço.
     */
    public List<OrdemServico> listarTodasOrdens() {
        return ordemServicoRepository.listarTodasOrdens();
    }
    
    /**
     * Lista ordens de serviço por ID do mecânico responsável.
     * @param idMecanico ID do mecânico.
     * @return Lista de ordens de serviço atribuídas ao mecânico.
     */
    public List<OrdemServico> listarOrdensPorMecanico(int idMecanico) {
        return ordemServicoRepository.listarOrdensPorMecanico(idMecanico);
    }

    /**
     * Lista ordens de serviço por ID do cliente.
     * @param idCliente ID do cliente.
     * @return Lista de ordens de serviço para o cliente.
     */
    public List<OrdemServico> listarOrdensPorCliente(int idCliente) {
        return ordemServicoRepository.listarOrdensPorCliente(idCliente);
    }
    
    /**
     * Calcula o preço total final de uma Ordem de Serviço, incluindo mão de obra e custo das peças.
     * Este método utiliza o ItemEstoqueService para buscar o preço das peças pelo código.
     * @param os A Ordem de Serviço para calcular o total.
     * @return O preço total final da OS.
     * @throws IllegalArgumentException Se uma peça não for encontrada no estoque.
     */
    public BigDecimal calcularPrecoTotalFinalOS(OrdemServico os) throws IllegalArgumentException {
        Objects.requireNonNull(os, "Ordem de Serviço não pode ser nula para calcular o total.");
        BigDecimal totalOS = BigDecimal.ZERO;

        for (Servico servico : os.getServicos()) {
            totalOS = totalOS.add(servicoService.calcularCustoTotalServico(servico));
        }
        return totalOS;
    }
}