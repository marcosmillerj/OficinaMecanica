/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.enums.StatusOrdem;
import observers.IObservavelOrdemServico;
import observers.IObservadorOrdemServico; 

/**
 *Classe que representa uma Ordem de Serviço na oficina mecânica
 *Implementa o padrão Observer para notificar sobre mudanças de status
 * @author barbo
 */
public class OrdemServico implements IObservavelOrdemServico {

    public static int proximoId = 1;

    private int id;
    private String codigo;
    private LocalDateTime dataAbertura;
    private BigDecimal precoTotal;
    private int idVeiculo;
    private int idCliente;
    private int idMecanicoResponsavel;
    private StatusOrdem status;
    private Optional<Integer> idElevadorAtual;
    private final List<Servico> servicos;
    private final List<IObservadorOrdemServico> observadores;
    
    /**
     * Construtor principal.
     */
    public OrdemServico(String codigo, LocalDateTime dataAbertura, int idVeiculo,
                        int idCliente, int idMecanicoResponsavel, StatusOrdem status) {
        this.id = proximoId++;
        this.codigo = Objects.requireNonNull(codigo, "Código da OS não pode ser nulo.");
        this.dataAbertura = Objects.requireNonNull(dataAbertura, "Data de abertura da OS não pode ser nula.");
        this.precoTotal = BigDecimal.ZERO;
        this.idVeiculo = idVeiculo;
        this.idCliente = idCliente;
        this.idMecanicoResponsavel = idMecanicoResponsavel;
        this.status = Objects.requireNonNull(status, "Status inicial da OS não pode ser nulo.");
        this.idElevadorAtual = Objects.requireNonNullElse(idElevadorAtual, Optional.empty());
        this.servicos = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    /**
     * Construtor para Gson (incluindo o novo atributo idElevadorAtual).
     */
    public OrdemServico(int id, String codigo, LocalDateTime dataAbertura, BigDecimal precoTotal,
                        int idVeiculo, int idCliente, int idMecanicoResponsavel,
                        StatusOrdem status, Optional<Integer> idElevadorAtual, List<Servico> servicos) {
        this.id = id;
        this.codigo = codigo;
        this.dataAbertura = dataAbertura;
        this.precoTotal = precoTotal;
        this.idVeiculo = idVeiculo;
        this.idCliente = idCliente;
        this.idMecanicoResponsavel = idMecanicoResponsavel;
        this.status = status;
        this.idElevadorAtual = idElevadorAtual; // <<--- INICIALIZA AQUI!
        this.servicos = (servicos != null) ? new ArrayList<>(servicos) : new ArrayList<>();
        this.observadores = new ArrayList<>();
    }


    // --- MÉTODOS DO PADRÃO OBSERVER ---
    @Override
    public void adicionarObservador(IObservadorOrdemServico obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Assinante de OS adicionado.");
        }
    }

    @Override
    public void removerObservador(IObservadorOrdemServico obs) {
        observadores.remove(obs);
        System.out.println("[LOG:OrdemServico " + this.codigo + "] Assinante de OS removido.");
    }

    @Override
    public void notificarObservadores() {
        System.out.println("[LOG:OrdemServico " + this.codigo + "] Enviando notificação de status aos assinantes...");
        for (IObservadorOrdemServico obs : observadores) {
            obs.notificarStatusOrdem(this);
        }
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = Objects.requireNonNull(codigo, "Código da OS não pode ser nulo."); }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = Objects.requireNonNull(dataAbertura, "Data de abertura da OS não pode ser nula."); }

    public BigDecimal getPrecoTotal() { return precoTotal; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdMecanicoResponsavel() { return idMecanicoResponsavel; }
    public void setIdMecanicoResponsavel(int idMecanicoResponsavel) { this.idMecanicoResponsavel = idMecanicoResponsavel; }

    public StatusOrdem getStatus() { return status; }
    public void setStatus(StatusOrdem status) { this.status = Objects.requireNonNull(status, "Status não pode ser nulo."); }

    public Optional<Integer> getIdElevadorAtual() { 
        return Objects.requireNonNullElse(idElevadorAtual, Optional.empty());
    }
    public void setIdElevadorAtual(Optional<Integer> idElevadorAtual) {
        this.idElevadorAtual = Objects.requireNonNull(idElevadorAtual, "ID do elevador atual não pode ser nulo (use Optional.empty()).");
    }
    
    public List<Servico> getServicos() {
        return new ArrayList<>(servicos);
    }

    // --- Métodos de Comportamento ---

    /**
     * Calcula o preço total da Ordem de Serviço somando os preços de mão de obra de todos os serviços.
     * NÃO INCLUI PREÇO DE PEÇAS AQUI. O preço total final (com peças) é calculado no OrdemServicoService.
     * @return O valor total de mão de obra calculado (BigDecimal).
     */
    public BigDecimal calcularTotal() {
        this.precoTotal = servicos.stream()
            .map(Servico::getPrecoMaoDeObra)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.precoTotal;
    }

    /**
     * Adiciona um serviço a ser realizado nesta Ordem de Serviço.
     * Após adicionar, recalcula o preço total (apenas mão de obra).
     * @param servico O serviço a ser adicionado.
     */
    public void adicionarServico(Servico servico) {
        if (servico != null) {
            servicos.add(servico);
            calcularTotal();
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getObservacoes() + "' adicionado. Novo total (M.O.): R$ " + String.format("%.2f", this.precoTotal));
        }
    }

    /**
     * Remove um serviço desta Ordem de Serviço.
     * Após remover, recalcula o preço total (apenas mão de obra).
     * @param servico O serviço a ser removido.
     * @return true se o serviço foi removido, false caso contrário.
     */
    public boolean removerServico(Servico servico) {
        boolean removido = servicos.remove(servico);
        if (removido) {
            calcularTotal(); 
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getObservacoes() + "' removido. Novo total (M.O.): R$ " + String.format("%.2f", this.precoTotal));
        }
        return removido;
    }

    /**
     * Altera o status da ordem de serviço e notifica todos os observadores registrados.
     * Notifica apenas se o status realmente mudou.
     * @param novoStatus O novo status para o qual a ordem será atualizada.
     */
    public void alterarStatus(StatusOrdem novoStatus) {
        if (this.status != novoStatus) {
            this.status = novoStatus;
            System.out.println("\n[LOG:OrdemServico " + this.codigo + "] Status alterado para -> " + novoStatus.getDescricao());
            notificarObservadores();
        } else {
            System.out.println("\n[LOG:OrdemServico " + this.codigo + "] Status já é " + novoStatus.getDescricao() + ". Nenhuma alteração/notificação.");
        }
    }

    @Override
    public String toString() {
        String servicosResumo = servicos.isEmpty() ? "Nenhum" : servicos.size() + " serviço(s)";
        String elevadorInfo = idElevadorAtual.isPresent() ? ", ElevadorID=" + idElevadorAtual.get() : "";
        return "OrdemServico{" +
               "ID=" + id +
               ", Código='" + codigo + '\'' +
               ", Status='" + status.getDescricao() + '\'' +
               ", Preço M.O.=" + String.format("%.2f", precoTotal) +
               ", Veículo ID=" + idVeiculo +
               ", Cliente ID=" + idCliente +
               ", Mecânico ID=" + idMecanicoResponsavel +
               elevadorInfo +
               ", Serviços=" + servicosResumo +
               ", Data Abertura=" + dataAbertura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
               '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdemServico that = (OrdemServico) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}