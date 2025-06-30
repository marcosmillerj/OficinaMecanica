/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.Elevador;
import models.Servico;
import models.Veiculo;
import repository.ElevadorRepository;
import repository.VeiculoRepository;

/**
 *
 * @author marcos_miller
 */
public class ElevadorService {

    private ElevadorRepository elevadorRepository;
    private VeiculoRepository veiculoRepository;

    /**
     * Construtor do ElevadorService.
     * @param elevadorRepository O repositório dos elevadores.
     * @param veiculoRepository O repositório de veículos (para validar existência do veículo).
     */
    public ElevadorService(ElevadorRepository elevadorRepository, VeiculoRepository veiculoRepository) {
        this.elevadorRepository = Objects.requireNonNull(elevadorRepository, "ElevadorRepository não pode ser nulo.");
        this.veiculoRepository = Objects.requireNonNull(veiculoRepository, "VeiculoRepository não pode ser nulo.");
    }

    /**
     * Aloca o elevador mais adequado para uma Ordem de Serviço e o veículo associado.
     * Prioriza elevadores com capacidade de alinhamento se a OS contiver serviços de PNEUS_RODAS.
     *
     * @param idOs ID da Ordem de Serviço para a qual o elevador será alocado.
     * @param idVeiculo ID do veículo a ser colocado no elevador.
     * @param servicosNaOs A lista de Servicos contidos na Ordem de Serviço (para verificar necessidade de alinhamento).
     * @param idElevadorEscolhido ID do elevador que a CAMADA DE VIEW ESCOLHEU para alocar (se aplicável, 0 se não).
     * @return O Elevador alocado.
     * @throws IllegalArgumentException Se o veículo não for encontrado, o elevador escolhido não existir, ou for incompatível.
     * @throws IllegalStateException Se não houver elevadores disponíveis para a necessidade ou se já estiver ocupado.
     */
    public Elevador alocarElevador(int idOs, int idVeiculo, List<Servico> servicosNaOs, int idElevadorEscolhido) // Parâmetro idElevadorEscolhido
                                               throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(servicosNaOs, "Lista de serviços não pode ser nula.");

        // 1. Validação: Verificar se o veículo existe
        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado.");
        }
        
        // Verificar se o veículo já está em algum elevador
        if (elevadorRepository.listarTodosElevadores().stream()
                              .anyMatch(e -> e.getIdVeiculoAtual().isPresent() && e.getIdVeiculoAtual().get() == idVeiculo)) {
            throw new IllegalStateException("O veículo com ID " + idVeiculo + " já está ocupando um elevador.");
        }


        // 2. Verificar se a OS precisa de elevador de alinhamento
        boolean osRequerAlinhamento = servicosNaOs.stream()
                                                    .anyMatch(Servico::requerPrioridade); // Usa Servico.requerPrioridade()

        // 3. Buscar elevadores disponíveis
        List<Elevador> disponiveis = elevadorRepository.listarElevadoresDisponiveis();

        if (disponiveis.isEmpty()) {
            throw new IllegalStateException("Nenhum elevador disponível no momento.");
        }

        Optional<Elevador> elevadorParaAlocar = Optional.empty();

        // Lógica de seleção do elevador: Prioriza o escolhido pela VIEW, depois a necessidade da OS
        if (idElevadorEscolhido > 0) { // Se um elevador específico foi escolhido pela UI
            Optional<Elevador> escolhidoPeloUsuario = elevadorRepository.buscarElevadorPorId(idElevadorEscolhido);
            if (escolhidoPeloUsuario.isEmpty() || !escolhidoPeloUsuario.get().isDisponivel()) {
                throw new IllegalArgumentException("Elevador ID " + idElevadorEscolhido + " escolhido não existe ou não está disponível.");
            }
            
            // Validar se o elevador escolhido PELA VIEW atende à necessidade de alinhamento da OS
            if (osRequerAlinhamento && !escolhidoPeloUsuario.get().temCapacidadeAlinhamento()) { // <<< CORRIGIDO AQUI!
                throw new IllegalStateException("Elevador ID " + idElevadorEscolhido + " não tem capacidade de alinhamento, mas a OS requer.");
            }
            elevadorParaAlocar = escolhidoPeloUsuario;

        } else { // Se NENHUM elevador específico foi escolhido pela UI (idElevadorEscolhido == 0)
            if (osRequerAlinhamento) {
                elevadorParaAlocar = disponiveis.stream()
                                             .filter(Elevador::temCapacidadeAlinhamento) // <<< CORRIGIDO AQUI!
                                             .findFirst();
                if (elevadorParaAlocar.isEmpty()) {
                    throw new IllegalStateException("Nenhum elevador de alinhamento disponível para esta OS. Não foi possível alocar.");
                }
            } else { // OS NÃO requer alinhamento
                 elevadorParaAlocar = disponiveis.stream()
                                             .filter(e -> !e.temCapacidadeAlinhamento()) // <<< CORRIGIDO AQUI!
                                             .findFirst();
                 if(elevadorParaAlocar.isEmpty()){
                    elevadorParaAlocar = disponiveis.stream().findFirst();
                 }

                if (elevadorParaAlocar.isEmpty()) {
                    throw new IllegalStateException("Nenhum elevador disponível para alocar o veículo ID " + idVeiculo + ".");
                }
            }
        }
        
        // Finaliza a alocação no elevador escolhido/selecionado
        Elevador elevadorAlocado = elevadorParaAlocar.get();
        elevadorAlocado.ocupar(idVeiculo); // Ocupa o elevador
        elevadorRepository.persistirEstadoElevadores(); // PERSISTE O ESTADO!
        return elevadorAlocado;
    }

    /**
     * Libera um elevador específico.
     * @param idElevador ID do elevador a ser liberado.
     * @return true se o elevador foi liberado com sucesso, false se não foi encontrado ou já estava livre.
     */
    public boolean liberarElevador(int idElevador) {
        Optional<Elevador> elevadorOpt = elevadorRepository.buscarElevadorPorId(idElevador);
        if (elevadorOpt.isEmpty()) {
            return false;
        }
        Elevador elevador = elevadorOpt.get();
        boolean sucesso = elevador.liberar();
        if (sucesso) {
            elevadorRepository.persistirEstadoElevadores(); // PERSISTE O ESTADO!
        }
        return sucesso;
    }

    /**
     * Libera o elevador que o veículo de uma Ordem de Serviço está ocupando.
     * @param idVeiculo ID do veículo.
     * @return true se o elevador foi encontrado e liberado, false caso contrário.
     */
    public boolean liberarElevadorPorIdVeiculo(int idVeiculo) {
        Optional<Elevador> elevadorComVeiculo = elevadorRepository.listarTodosElevadores().stream()
                                                .filter(e -> e.getIdVeiculoAtual().isPresent() && e.getIdVeiculoAtual().get() == idVeiculo)
                                                .findFirst();
        if (elevadorComVeiculo.isPresent()) {
            return liberarElevador(elevadorComVeiculo.get().getId());
        }
        return false;
    }

    /**
     * Lista o status de todos os elevadores.
     * @return Uma lista de todos os objetos Elevador.
     */
    public List<Elevador> listarStatusElevadores() {
        return elevadorRepository.listarTodosElevadores();
    }
    
    /**
     * Lista os elevadores disponíveis.
     * @return Uma lista de elevadores disponíveis.
     */
    public List<Elevador> listarElevadoresDisponiveis() {
        return elevadorRepository.listarElevadoresDisponiveis();
    }
}