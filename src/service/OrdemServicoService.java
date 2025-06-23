/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import models.Cliente;
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
    private ServicoService servicoService; // Atributo que armazena o ServicoService

    /**
     * Construtor do OrdemServicoService.
     * @param ordemServicoRepository O repositório de Ordens de Serviço.
     * @param usuarioCRUD O CRUD de usuários (para acessar mecânicos).
     * @param clienteService O serviço de clientes (para validar existência de cliente por ID).
     * @param veiculoService O serviço de veículos (para validar existência de veículo por ID).
     * @param servicoService O serviço de serviços (para auxiliar na manipulação de instâncias de Servico).
     */
    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               UsuarioCRUD usuarioCRUD,
                               ClienteService clienteService,
                               VeiculoService veiculoService,
                               ServicoService servicoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.usuarioCRUD = usuarioCRUD;
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService não pode ser nulo.");
        this.veiculoService = Objects.requireNonNull(veiculoService, "VeiculoService não pode ser nulo.");
        this.servicoService = Objects.requireNonNull(servicoService, "ServicoService não pode ser nulo.");
    }

    /**
     * Cria uma nova Ordem de Serviço no sistema.
     * Realiza validações de existência de cliente, veículo e mecânico.
     * @param idCliente ID do cliente proprietário do veículo.
     * @param idVeiculo ID do veículo.
     * @param idMecanicoResponsavel ID do mecânico responsável inicial pela OS.
     * @return A OrdemServico criada.
     * @throws IllegalArgumentException Se cliente, veículo ou mecânico não forem encontrados.
     */
    public OrdemServico criarNovaOrdemServico(int idCliente, int idVeiculo, int idMecanicoResponsavel)
                                              throws IllegalArgumentException {
        // --- Validações de Negócio ---
        // 1. Verificar se Cliente existe
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) {
            throw new IllegalArgumentException("Cliente com ID " + idCliente + " não encontrado.");
        }

        // 2. Verificar se Veículo existe
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado.");
        }

        // 3. Verificar se Mecânico existe e é do tipo MECANICO
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
        System.out.println("Nova Ordem de Serviço criada: " + novaOS.getCodigo());
        return novaOS;
    }

    /**
     * NOVO MÉTODO: Persiste as alterações em uma Ordem de Serviço já modificada em memória.
     * Este método é chamado quando o objeto OrdemServico é atualizado diretamente,
     * por exemplo, ao adicionar/remover/atualizar um serviço em sua lista interna.
     * @param ordemParaAtualizar O objeto OrdemServico que foi modificado em memória.
     */
    public void atualizarOrdemServico(OrdemServico ordemParaAtualizar) {
        Objects.requireNonNull(ordemParaAtualizar, "Ordem de Serviço para atualizar não pode ser nula.");
        ordemServicoRepository.atualizarOrdemServico(ordemParaAtualizar);
    }

    /**
     * Altera o status de uma Ordem de Serviço.
     * @param idOs ID da Ordem de Serviço.
     * @param novoStatus O novo status a ser aplicado.
     * @return A OrdemServico atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada.
     * @throws IllegalStateException Se a transição de status não for permitida.
     */
    public OrdemServico alterarStatusOrdemServico(int idOs, StatusOrdem novoStatus)
                                                 throws IllegalArgumentException, IllegalStateException {
        Optional<OrdemServico> osOpt = ordemServicoRepository.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            throw new IllegalArgumentException("Ordem de Serviço com ID " + idOs + " não encontrada.");
        }
        OrdemServico os = osOpt.get();

        Objects.requireNonNull(novoStatus, "Novo status não pode ser nulo.");

        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA) {
            throw new IllegalStateException("Não é possível alterar o status de uma OS " + os.getStatus().getDescricao() + " (já está finalizada ou cancelada).");
        }
        
        os.alterarStatus(novoStatus);
        // Persiste a mudança chamando o método auxiliar de atualização da própria classe
        atualizarOrdemServico(os); 
        return os;
    }
    
    /**
     * Adiciona um serviço a uma Ordem de Serviço existente e recalcula o total.
     * @param idOs ID da Ordem de Serviço.
     * @param servico O objeto Servico a ser adicionado.
     * @return A Ordem de Serviço atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada ou o serviço for nulo.
     * @throws IllegalStateException Se a OS não estiver em um status que permite adição de serviços.
     */
    public OrdemServico adicionarServicoNaOrdem(int idOs, Servico servico)
                                               throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(servico, "Serviço a ser adicionado não pode ser nulo.");

        Optional<OrdemServico> osOpt = ordemServicoRepository.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            throw new IllegalArgumentException("Ordem de Serviço com ID " + idOs + " não encontrada.");
        }
        OrdemServico os = osOpt.get();

        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA || os.getStatus() == StatusOrdem.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Não é possível adicionar serviços a uma OS com status " + os.getStatus().getDescricao() + ".");
        }
        // O método adicionarServico na OrdemServico já recalcula o total.
        os.adicionarServico(servico);
        // Persiste a mudança chamando o método auxiliar de atualização da própria classe
        atualizarOrdemServico(os); 
        return os;
    }
    
    /**
     * Remove um serviço de uma Ordem de Serviço existente e recalcula o total.
     * @param idOs ID da Ordem de Serviço.
     * @param servico O objeto Servico a ser removido.
     * @return A Ordem de Serviço atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada ou o serviço for nulo ou não estiver na OS.
     * @throws IllegalStateException Se a OS não estiver em um status que permite remoção de serviços.
     */
    public OrdemServico removerServicoDaOrdem(int idOs, Servico servico)
                                            throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(servico, "Serviço a ser removido não pode ser nulo.");

        Optional<OrdemServico> osOpt = ordemServicoRepository.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            throw new IllegalArgumentException("Ordem de Serviço com ID " + idOs + " não encontrada.");
        }
        OrdemServico os = osOpt.get();

        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA || os.getStatus() == StatusOrdem.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Não é possível remover serviços de uma OS com status " + os.getStatus().getDescricao() + ".");
        }
        
        if (!os.removerServico(servico)) { // Remove o serviço (e recalcula o total)
            throw new IllegalArgumentException("Serviço '" + servico.getObservacoes() + "' não encontrado na Ordem de Serviço " + os.getCodigo() + "."); // Usar getObservacoes()
        }
        // Persiste a mudança chamando o método auxiliar de atualização da própria classe
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
            totalOS = totalOS.add(servicoService.calcularCustoTotalServico(servico)); // Usa o método do ServicoService
        }
        return totalOS;
    }
}