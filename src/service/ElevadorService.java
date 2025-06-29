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
    private VeiculoRepository veiculoRepository; // Para validar se o veículo existe antes de ocupar o elevador

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
     * @return O Elevador alocado, ou Optional.empty() se nenhum elevador estiver disponível.
     * @throws IllegalArgumentException Se o veículo não for encontrado.
     * @throws IllegalStateException Se não houver elevadores disponíveis para a necessidade.
     */
    public Optional<Elevador> alocarElevador(int idOs, int idVeiculo, List<Servico> servicosNaOs)
                                               throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(servicosNaOs, "Lista de serviços não pode ser nula.");

        // 1. Validação: Verificar se o veículo existe
        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado.");
        }

        // 2. Verificar se a OS precisa de elevador de alinhamento
        boolean osRequerAlinhamento = servicosNaOs.stream()
                                                    .anyMatch(Servico::requerPrioridade); // Usa o requerPrioridade do Servico

        // 3. Buscar elevadores disponíveis
        List<Elevador> disponiveis = elevadorRepository.listarElevadoresDisponiveis();

        if (disponiveis.isEmpty()) {
            throw new IllegalStateException("Nenhum elevador disponível no momento.");
        }

        Optional<Elevador> elevadorAlocado = Optional.empty();

        // 4. Lógica de Priorização:
        if (osRequerAlinhamento) {
            // Tenta encontrar um elevador de alinhamento disponível
            elevadorAlocado = disponiveis.stream()
                                         .filter(Elevador::especializadoEmAlinhamento) // Usa o novo getter
                                         .findFirst();
            if (elevadorAlocado.isPresent()) {
                elevadorAlocado.get().ocupar(idVeiculo);
                System.out.println("Elevador de alinhamento ID " + elevadorAlocado.get().getId() + " alocado para OS " + idOs + ".");
                return elevadorAlocado;
            } else {
                // Se nenhum elevador de alinhamento disponível, pode-se decidir:
                // a) Não alocar e avisar que não há elevador especialista.
                // b) Alocar um elevador geral se a OS puder esperar ou se for temporário.
                System.out.println("Nenhum elevador de alinhamento disponível. Tentando alocar elevador geral se possível para OS " + idOs + "...");
                // Prossegue para tentar alocar um elevador geral.
            }
        }

        // 5. Se não requerer alinhamento ou se nenhum elevador de alinhamento foi alocado:
        // Tenta encontrar qualquer elevador geral disponível.
        if (elevadorAlocado.isEmpty()) {
             elevadorAlocado = disponiveis.stream()
                                         .filter(e -> !e.especializadoEmAlinhamento()) // Pega um elevador geral
                                         .findFirst();
             if(elevadorAlocado.isEmpty()){ // Se não achou geral, tenta o de alinhamento novamente (agora sem prioridade)
                elevadorAlocado = disponiveis.stream()
                                         .findFirst(); // Pega o primeiro disponível de qualquer tipo
             }

            if (elevadorAlocado.isPresent()) {
                elevadorAlocado.get().ocupar(idVeiculo);
                System.out.println("Elevador geral ID " + elevadorAlocado.get().getId() + " alocado para OS " + idOs + ".");
                return elevadorAlocado;
            } else {
                // Se chegou aqui, significa que todos os elevadores estão ocupados.
                throw new IllegalStateException("Nenhum elevador disponível para alocar o veículo ID " + idVeiculo + ".");
            }
        }
        return elevadorAlocado; // Retorna o elevador que foi alocado (se já foi no passo 4)
    }

    /**
     * Libera um elevador específico.
     * @param idElevador ID do elevador a ser liberado.
     * @return true se o elevador foi liberado com sucesso, false se não foi encontrado ou já estava livre.
     */
    public boolean liberarElevador(int idElevador) {
        Optional<Elevador> elevadorOpt = elevadorRepository.buscarElevadorPorId(idElevador);
        if (elevadorOpt.isEmpty()) {
            System.out.println("Elevador com ID " + idElevador + " não encontrado.");
            return false;
        }
        Elevador elevador = elevadorOpt.get();
        return elevador.liberar(); // Delega a operação de liberação ao objeto Elevador
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
