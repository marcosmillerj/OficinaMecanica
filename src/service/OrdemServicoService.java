/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

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

    private OrdemServicoRepository ordemServicoRepository; // Repositório de OS
    private UsuarioCRUD usuarioCRUD; // Para buscar mecânicos (ou seria UsuarioService)
    private ClienteService clienteService; // ATRIBUTO NOVO!
    private VeiculoService veiculoService;
    private ServicoService servicoService;// ATRIBUTO NOVO!
    // private ServicoService servicoService; // Futura dependência se tiver um service para Servico

    /**
     * Construtor do OrdemServicoService.
     * @param ordemServicoRepository O repositório de Ordens de Serviço.
     * @param usuarioCRUD O CRUD de usuários (para acessar mecânicos).
     * @param clienteService O serviço de clientes (para validar existência de cliente por ID).
     * @param veiculoService O serviço de veículos (para validar existência de veículo por ID).
     * // Futuramente: @param servicoService O serviço de serviços.
     */
    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               UsuarioCRUD usuarioCRUD,
                               ClienteService clienteService,
                               VeiculoService veiculoService,
                               ServicoService servicoService) { // <<--- NOVO PARÂMETRO!
        this.ordemServicoRepository = ordemServicoRepository;
        this.usuarioCRUD = usuarioCRUD;
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService não pode ser nulo.");
        this.veiculoService = Objects.requireNonNull(veiculoService, "VeiculoService não pode ser nulo.");
        this.servicoService = Objects.requireNonNull(servicoService, "ServicoService não pode ser nulo."); // <<--- INICIALIZA!
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
        // 1. Verificar se Cliente existe (AGORA USA clienteService)
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(idCliente); // USANDO O SERVICE!
        if (clienteOpt.isEmpty()) {
            throw new IllegalArgumentException("Cliente com ID " + idCliente + " não encontrado.");
        }

        // 2. Verificar se Veículo existe (AGORA USA veiculoService)
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(idVeiculo); // USANDO O SERVICE!
        if (veiculoOpt.isEmpty()) {
            throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado.");
        }

        // 3. Verificar se Mecânico existe e é do tipo MECANICO
        Optional<Usuario> mecanicoOpt = usuarioCRUD.buscarUsuarioPorIdOptional(idMecanicoResponsavel);
        if (mecanicoOpt.isEmpty() || mecanicoOpt.get().getTipo() != models.enums.TipoUsuario.MECANICO) {
            throw new IllegalArgumentException("Mecânico com ID " + idMecanicoResponsavel + " não encontrado ou não é um mecânico válido.");
        }
        
        // --- Geração do Código da OS ---
        String codigoOS = "OS-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmmss")) + "-" + UUID.randomUUID().toString().substring(0, 4);

        // --- Criação do Objeto OrdemServico ---
        OrdemServico novaOS = new OrdemServico(
            codigoOS,
            LocalDateTime.now(), // Data de abertura
            idVeiculo,
            idCliente,
            idMecanicoResponsavel,
            StatusOrdem.AGUARDANDO_DIAGNOSTICO // Status inicial da OS
        );

        // --- Persistência ---
        ordemServicoRepository.adicionarOrdemServico(novaOS);
        System.out.println("Nova Ordem de Serviço criada: " + novaOS.getCodigo());
        return novaOS;
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

        // --- Lógica de Validação de Transição de Status ---
        if (os.getStatus() == StatusOrdem.FINALIZADA || os.getStatus() == StatusOrdem.CANCELADA) {
            throw new IllegalStateException("Não é possível alterar o status de uma OS " + os.getStatus().getDescricao() + " (já está finalizada ou cancelada).");
        }
        // Exemplo de regras de transição mais complexas:
        // if (os.getStatus() == StatusOrdem.AGUARDANDO_DIAGNOSTICO && novoStatus == StatusOrdem.EM_EXECUCAO) {
        //     throw new IllegalStateException("Não pode ir de Diagnóstico para Execução diretamente.");
        // }
        
        os.alterarStatus(novoStatus); // O método da OS já notifica observadores
        ordemServicoRepository.atualizarOrdemServico(os); // Persiste a mudança de status
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

        os.adicionarServico(servico); // Adiciona o serviço (e recalcula o total)
        ordemServicoRepository.atualizarOrdemServico(os); // Persiste a mudança
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
            throw new IllegalArgumentException("Serviço '" + servico.getDescricao() + "' não encontrado na Ordem de Serviço " + os.getCodigo() + ".");
        }
        ordemServicoRepository.atualizarOrdemServico(os); // Persiste a mudança
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
}