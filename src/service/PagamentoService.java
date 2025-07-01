/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.OrdemServico;
import models.Pagamento;
import models.enums.StatusOrdem;
import models.enums.TipoPagamento;
import repository.PagamentoRepository;

/**
 *
 * @author marcos_miller
 */
public class PagamentoService {

    private PagamentoRepository pagamentoRepository;
    private OrdemServicoService ordemServicoService;

    public PagamentoService(PagamentoRepository pagamentoRepository, OrdemServicoService ordemServicoService) {
        this.pagamentoRepository = Objects.requireNonNull(pagamentoRepository, "PagamentoRepository não pode ser nulo.");
        this.ordemServicoService = Objects.requireNonNull(ordemServicoService, "OrdemServicoService não pode ser nulo.");
    }

    /**
     * Inicia um novo processo de pagamento para uma Ordem de Serviço.
     * Cria um objeto Pagamento, mas ainda não o finaliza.
     * @param idOrdemServico ID da Ordem de Serviço para a qual o pagamento será processado.
     * @return O objeto Pagamento criado.
     * @throws IllegalArgumentException Se a Ordem de Serviço não for encontrada.
     * @throws IllegalStateException Se a Ordem de Serviço não estiver no status AGUARDANDO_PAGAMENTO.
     */
    public Pagamento iniciarProcessoPagamento(int idOrdemServico) throws IllegalArgumentException, IllegalStateException {
        Optional<OrdemServico> osOpt = ordemServicoService.buscarOrdemServicoPorId(idOrdemServico);
        if (osOpt.isEmpty()) {
            throw new IllegalArgumentException("Ordem de Serviço com ID " + idOrdemServico + " não encontrada para iniciar pagamento.");
        }
        OrdemServico os = osOpt.get();

        if (os.getStatus() != StatusOrdem.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Não é possível iniciar pagamento para OS " + os.getCodigo() + ". Status atual: " + os.getStatus().getDescricao() + ".");
        }
        
        if (!pagamentoRepository.listarPagamentosPorOrdemServico(idOrdemServico).isEmpty()) {
            throw new IllegalStateException("Já existe um pagamento registrado para a OS " + os.getCodigo() + ".");
        }

        Pagamento novoPagamento = new Pagamento(Optional.of(idOrdemServico));
        pagamentoRepository.adicionarPagamento(novoPagamento);
        System.out.println("Processo de pagamento ID " + novoPagamento.getId() + " iniciado para OS " + os.getCodigo() + ".");
        return novoPagamento;
    }

    /**
     * Finaliza um pagamento existente.
     * @param idPagamento ID do pagamento a ser finalizado.
     * @param valorFinal O valor final pago.
     * @param tipoFinal O tipo de pagamento.
     * @return O objeto Pagamento finalizado.
     * @throws IllegalArgumentException Se o pagamento não for encontrado ou dados inválidos.
     * @throws IllegalStateException Se o pagamento já estiver finalizado.
     */
    public Pagamento finalizarPagamento(int idPagamento, BigDecimal valorFinal, TipoPagamento tipoFinal) throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(valorFinal, "Valor final não pode ser nulo.");
        Objects.requireNonNull(tipoFinal, "Tipo de pagamento não pode ser nulo.");

        Optional<Pagamento> pagamentoOpt = pagamentoRepository.buscarPagamentoPorId(idPagamento);
        if (pagamentoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pagamento com ID " + idPagamento + " não encontrado.");
        }
        Pagamento pagamento = pagamentoOpt.get();

        if (pagamento.getDataHora() != null) {
            throw new IllegalStateException("Pagamento ID " + idPagamento + " já foi finalizado.");
        }

        pagamento.finalizar(valorFinal, tipoFinal);
        pagamentoRepository.atualizarPagamento(pagamento);
        
        Optional<Integer> idElevadorParaAlocar = Optional.empty();

        if (pagamento.getIdOrdemServico().isPresent()) {
            int idOs = pagamento.getIdOrdemServico().get();
            try {
                ordemServicoService.alterarStatusOrdemServico(idOs, StatusOrdem.FINALIZADA, idElevadorParaAlocar);
                System.out.println("Status da OS " + idOs + " alterado para FINALIZADA após pagamento.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.err.println("Aviso: Falha ao atualizar status da OS " + idOs + " após pagamento: " + e.getMessage());
            }
        }
        return pagamento;
    }

    /**
     * Calcula o troco para um pagamento em dinheiro.
     * @param valorPago Valor entregue pelo cliente.
     * @param valorTotal Valor devido.
     * @return O valor do troco.
     * @throws IllegalArgumentException Se o valor pago for menor que o total.
     */
    public BigDecimal calcularTroco(BigDecimal valorPago, BigDecimal valorTotal) throws IllegalArgumentException {
        Objects.requireNonNull(valorPago, "Valor pago não pode ser nulo.");
        Objects.requireNonNull(valorTotal, "Valor total não pode ser nulo.");
        if (valorPago.compareTo(valorTotal) < 0) {
            throw new IllegalArgumentException("Valor pago (R$ " + String.format("%.2f", valorPago) + ") é menor que o valor total (R$ " + String.format("%.2f", valorTotal) + ").");
        }
        return valorPago.subtract(valorTotal);
    }

    /**
     * Busca um pagamento pelo ID.
     * @param idPagamento ID do pagamento.
     * @return Optional contendo o pagamento, ou vazio se não encontrado.
     */
    public Optional<Pagamento> buscarPagamentoPorId(int idPagamento) {
        return pagamentoRepository.buscarPagamentoPorId(idPagamento);
    }

    /**
     * Lista todos os pagamentos.
     * @return Lista de todos os pagamentos.
     */
    public List<Pagamento> listarTodosPagamentos() {
        return pagamentoRepository.listarTodosPagamentos();
    }
}