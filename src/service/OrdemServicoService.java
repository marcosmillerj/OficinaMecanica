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
    private Scanner scanner; // Para interação na escolha do elevador

    /**
     * Construtor do OrdemServicoService.
     * @param ordemServicoRepository O repositório de Ordens de Serviço.
     * @param usuarioCRUD O CRUD de usuários (para acessar mecânicos).
     * @param clienteService O serviço de clientes.
     * @param veiculoService O serviço de veículos.
     * @param servicoService O serviço de serviços.
     * @param elevadorService O serviço de elevadores.
     * @param scanner O scanner para interação com o usuário (escolha de elevador).
     */
    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               UsuarioCRUD usuarioCRUD,
                               ClienteService clienteService,
                               VeiculoService veiculoService,
                               ServicoService servicoService,
                               ElevadorService elevadorService,
                               Scanner scanner) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository, "OrdemServicoRepository não pode ser nulo.");
        this.usuarioCRUD = Objects.requireNonNull(usuarioCRUD, "UsuarioCRUD não pode ser nulo.");
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService não pode ser nulo.");
        this.veiculoService = Objects.requireNonNull(veiculoService, "VeiculoService não pode ser nulo.");
        this.servicoService = Objects.requireNonNull(servicoService, "ServicoService não pode ser nulo.");
        this.elevadorService = Objects.requireNonNull(elevadorService, "ElevadorService não pode ser nulo.");
        this.scanner = Objects.requireNonNull(scanner, "Scanner não pode ser nulo.");
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
        // Validações de existência de Cliente, Veículo e Mecânico
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
            LocalDateTime.now(), // Data de abertura
            idVeiculo,
            idCliente,
            idMecanicoResponsavel,
            StatusOrdem.AGUARDANDO_DIAGNOSTICO // Status inicial da OS
        );

        ordemServicoRepository.adicionarOrdemServico(novaOS);
        System.out.println("Nova Ordem de Serviço criada: " + novaOS.getCodigo());
        return novaOS;
    }

    /**
     * Persiste as alterações em uma Ordem de Serviço já modificada em memória.
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
     * Integra a lógica de alocação/liberação de elevador, incluindo a escolha pelo usuário.
     * @param idOs ID da Ordem de Serviço.
     * @param novoStatus O novo status a ser aplicado.
     * @return A OrdemServico atualizada.
     * @throws IllegalArgumentException Se a OS não for encontrada.
     * @throws IllegalStateException Se a transição de status não for permitida ou se houver problemas com elevador.
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
        
        // --- INTEGRAÇÃO COM ELEVADOR: Lógica de Alocação ---
        // Se o status está mudando PARA EM_DIAGNOSTICO ou EM_EXECUCAO
        if ((novoStatus == StatusOrdem.EM_DIAGNOSTICO || novoStatus == StatusOrdem.EM_EXECUCAO) && 
            !(os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO)) {
            
            System.out.println("\n[SISTEMA ELEVADOR] Tentando alocar elevador para OS " + os.getCodigo() + "...");
            
            // 1. Verificar se a OS requer elevador de alinhamento
            boolean osRequerAlinhamento = os.getServicos().stream()
                                            .anyMatch(Servico::requerPrioridade);

            List<Elevador> elevadoresDisponiveis = elevadorService.listarElevadoresDisponiveis();

            if (elevadoresDisponiveis.isEmpty()) {
                throw new IllegalStateException("Nenhum elevador disponível no momento para OS " + os.getCodigo() + ". Não foi possível alterar o status.");
            }

            Optional<Elevador> elevadorEscolhido = Optional.empty();

            if (osRequerAlinhamento) {
                List<Elevador> alinhamentoDisponiveis = elevadoresDisponiveis.stream()
                                                            .filter(Elevador::especializadoEmAlinhamento)
                                                            .collect(Collectors.toList());
                if (!alinhamentoDisponiveis.isEmpty()) {
                    System.out.println("OS requer elevador de Alinhamento. Elevadores disponíveis para Alinhamento:");
                    for (int i = 0; i < alinhamentoDisponiveis.size(); i++) {
                        System.out.println((i + 1) + ". " + alinhamentoDisponiveis.get(i).toString());
                    }
                    int escolha = lerInteiroValido("Escolha o número do elevador de alinhamento: ");
                    if (escolha > 0 && escolha <= alinhamentoDisponiveis.size()) {
                        elevadorEscolhido = Optional.of(alinhamentoDisponiveis.get(escolha - 1));
                    } else {
                        System.out.println("Opção inválida. Selecionando primeiro elevador de alinhamento disponível.");
                        elevadorEscolhido = Optional.of(alinhamentoDisponiveis.get(0));
                    }
                } else {
                    throw new IllegalStateException("Nenhum elevador de alinhamento disponível para esta OS. Não foi possível alterar o status.");
                }
            } else { // OS NÃO requer alinhamento
                System.out.println("OS não requer elevador de Alinhamento. Elevadores Gerais disponíveis:");
                List<Elevador> geraisDisponiveis = elevadoresDisponiveis.stream()
                                                    .filter(e -> !e.especializadoEmAlinhamento())
                                                    .collect(Collectors.toList());
                // Se não houver gerais, pode usar um de alinhamento se estiver livre
                if(geraisDisponiveis.isEmpty()){
                    geraisDisponiveis.addAll(elevadoresDisponiveis); // Todos elevadores disponíveis
                    System.out.println("Nenhum elevador geral disponível. Usando elevador de alinhamento se disponível.");
                }

                for (int i = 0; i < geraisDisponiveis.size(); i++) {
                    System.out.println((i + 1) + ". " + geraisDisponiveis.get(i).toString());
                }
                int escolha = lerInteiroValido("Escolha o número do elevador: ");
                if (escolha > 0 && escolha <= geraisDisponiveis.size()) {
                    elevadorEscolhido = Optional.of(geraisDisponiveis.get(escolha - 1));
                } else {
                    System.out.println("Opção inválida. Selecionando primeiro elevador geral disponível.");
                    elevadorEscolhido = Optional.of(geraisDisponiveis.get(0));
                }
            }

            // Alocar o elevador escolhido
            if (elevadorEscolhido.isPresent()) {
                elevadorEscolhido.get().ocupar(os.getIdVeiculo());
            } else {
                throw new IllegalStateException("Erro interno: Elevador não foi escolhido para alocação.");
            }

        } else if ((novoStatus == StatusOrdem.FINALIZADA || novoStatus == StatusOrdem.AGUARDANDO_PAGAMENTO ||
                    novoStatus == StatusOrdem.CANCELADA || novoStatus == StatusOrdem.AGUARDANDO_DIAGNOSTICO || // INCLUÍDO AQUI
                    novoStatus == StatusOrdem.AGUARDANDO_LIBERACAO ) && // INCLUÍDO AQUI
                   (os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO)) {
            // Se o status está saindo de "ocupado" (Diagnóstico ou Execução) para um status que libera o elevador
            System.out.println("\n[SISTEMA ELEVADOR] Solicitando liberação de elevador para veículo da OS " + os.getCodigo() + "...");
            
            Optional<Elevador> elevadorComVeiculo = elevadorService.listarStatusElevadores().stream()
                                                    .filter(e -> e.getIdVeiculoAtual().isPresent() && e.getIdVeiculoAtual().get() == os.getIdVeiculo())
                                                    .findFirst();
            if (elevadorComVeiculo.isPresent()) {
                elevadorService.liberarElevador(elevadorComVeiculo.get().getId());
                System.out.println("Elevador ID " + elevadorComVeiculo.get().getId() + " liberado para OS " + os.getCodigo() + ".");
            } else {
                System.out.println("Aviso: Veículo da OS " + os.getCodigo() + " não estava em nenhum elevador alocado. Status alterado.");
            }
        }
        // --- FIM DA INTEGRAÇÃO COM ELEVADOR ---

        os.alterarStatus(novoStatus); // Altera o status na OS (notifica observadores)
        atualizarOrdemServico(os); // Persiste a OS modificada
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
     * @return A Ordem de Serviço atualizada.
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

    // Método auxiliar para ler um inteiro válido
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
}