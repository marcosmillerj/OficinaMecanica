/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import models.Agendamento;
import models.Cliente;
import models.Veiculo;
import models.enums.StatusAgendamento;
import repository.AgendamentoRepository;
import repository.ClienteRepository;
import repository.VeiculoRepository;

/**
 *
 * @author marcos_miller
 */
public class AgendamentoService {

    private AgendamentoRepository agendamentoRepository;
    private ClienteRepository clienteRepository;
    private VeiculoRepository veiculoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ClienteRepository clienteRepository,
                              VeiculoRepository veiculoRepository) {
        this.agendamentoRepository = Objects.requireNonNull(agendamentoRepository, "AgendamentoRepository não pode ser nulo.");
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository não pode ser nulo.");
        this.veiculoRepository = Objects.requireNonNull(veiculoRepository, "VeiculoRepository não pode ser nulo.");
    }

    /**
     * Cria um novo agendamento no sistema.
     * @param dataHora Data e hora do agendamento.
     * @param idCliente ID do cliente.
     * @param idVeiculo ID do veículo.
     * @param valor Valor do agendamento (taxa ou estimado).
     * @return O Agendamento criado.
     * @throws IllegalArgumentException Se data/hora inválida, cliente/veículo não encontrados.
     * @throws IllegalStateException Se já houver agendamento para o mesmo veículo na mesma data/hora.
     */
    public Agendamento criarAgendamento(LocalDateTime dataHora, int idCliente, int idVeiculo, BigDecimal valor)
                                        throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(dataHora, "Data e hora não podem ser nulas.");
        Objects.requireNonNull(valor, "Valor não pode ser nulo.");

        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar para uma data/hora no passado.");
        }

        Optional<Cliente> clienteOpt = clienteRepository.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) { throw new IllegalArgumentException("Cliente com ID " + idCliente + " não encontrado."); }

        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) { throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado."); }

        boolean conflito = agendamentoRepository.listarAgendamentosPorVeiculo(idVeiculo).stream()
            .anyMatch(a -> a.getDataHora().equals(dataHora) &&
                           (a.getStatus() == StatusAgendamento.PENDENTE || a.getStatus() == StatusAgendamento.CONFIRMADO));
        if (conflito) {
            throw new IllegalStateException("Já existe um agendamento para o veículo ID " + idVeiculo + " nesta data/hora.");
        }

        Agendamento novoAgendamento = new Agendamento(dataHora, idCliente, idVeiculo, valor);
        agendamentoRepository.adicionarAgendamento(novoAgendamento);
        System.out.println("Agendamento criado: " + novoAgendamento.toString());
        return novoAgendamento;
    }

    /**
     * Reagenda um agendamento existente.
     * @param idAgendamento ID do agendamento a reagendar.
     * @param novaDataHora Nova data e hora.
     * @return true se reagendado, false se não encontrado ou status inválido.
     */
    public boolean reagendarAgendamento(int idAgendamento, LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.buscarAgendamentoPorId(idAgendamento);
        if (agendamentoOpt.isEmpty()) { return false; }
        Agendamento agendamento = agendamentoOpt.get();

        boolean sucesso = agendamento.reagendar(novaDataHora);
        if (sucesso) { agendamentoRepository.atualizarAgendamento(agendamento); }
        return sucesso;
    }

    /**
     * Cancela um agendamento existente.
     * @param idAgendamento ID do agendamento a cancelar.
     * @return O valor retido do cancelamento (BigDecimal.ZERO se falhar), ou null se não encontrado.
     */
    public BigDecimal cancelarAgendamento(int idAgendamento) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.buscarAgendamentoPorId(idAgendamento);
        if (agendamentoOpt.isEmpty()) { return null; }
        Agendamento agendamento = agendamentoOpt.get();

        BigDecimal valorRetido = agendamento.cancelar(); 
        agendamentoRepository.atualizarAgendamento(agendamento);
        return valorRetido;
    }

    /**
     * Confirma um agendamento existente.
     * @param idAgendamento ID do agendamento a confirmar.
     * @return true se confirmado, false se não encontrado ou status inválido.
     */
    public boolean confirmarAgendamento(int idAgendamento) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.buscarAgendamentoPorId(idAgendamento);
        if (agendamentoOpt.isEmpty()) { return false; }
        Agendamento agendamento = agendamentoOpt.get();

        agendamento.confirmar();
        agendamentoRepository.atualizarAgendamento(agendamento);
        return true;
    }
    
    /**
     * Lista todos os agendamentos existentes no sistema.
     * @return Uma lista (cópia) de todos os agendamentos.
     */
    public List<Agendamento> listarTodosAgendamentos() {
        return agendamentoRepository.listarTodosAgendamentos();
    }
}