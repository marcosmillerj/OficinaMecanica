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
    private final List<Servico> servicos;
    private final List<IObservadorOrdemServico> observadores;
    
    public OrdemServico(String codigo, LocalDateTime dataAbertura, int idVeiculo,
                        int idCliente, int idMecanicoResponsavel, StatusOrdem status) {
        this.id = proximoId++;
        this.codigo = codigo;
        this.dataAbertura = dataAbertura;
        this.precoTotal = BigDecimal.ZERO;
        this.idVeiculo = idVeiculo;
        this.idCliente = idCliente;
        this.idMecanicoResponsavel = idMecanicoResponsavel;
        this.status = status;
        this.servicos = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // Construtor para Gson
    public OrdemServico(int id, String codigo, LocalDateTime dataAbertura, BigDecimal precoTotal,
                        int idVeiculo, int idCliente, int idMecanicoResponsavel,
                        StatusOrdem status, List<Servico> servicos) {
        this.id = id;
        this.codigo = codigo;
        this.dataAbertura = dataAbertura;
        this.precoTotal = precoTotal;
        this.idVeiculo = idVeiculo;
        this.idCliente = idCliente;
        this.idMecanicoResponsavel = idMecanicoResponsavel;
        this.status = status;
        this.servicos = new ArrayList<>(servicos);
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
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public BigDecimal getPrecoTotal() { return precoTotal; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdMecanicoResponsavel() { return idMecanicoResponsavel; }
    public void setIdMecanicoResponsavel(int idMecanicoResponsavel) { this.idMecanicoResponsavel = idMecanicoResponsavel; }

    public StatusOrdem getStatus() { return status; }
    public void setStatus(StatusOrdem status) { this.status = status; }

    public List<Servico> getServicos() {
        return new ArrayList<>(servicos);
    }

    // --- Métodos de Comportamento ---

    /**
     * Calcula o preço total da Ordem de Serviço somando os preços de todos os serviços e peças.
     * Atualiza o atributo precoTotal da OS.
     * NOTA: Este método agora precisa de um ItemEstoqueRepository ou ItemEstoqueService
     * para buscar o preço da peça pelo código. Isso é um desafio na classe model.
     * A solução mais limpa é que o cálculo real do preço TOTAL DA OS seja feito
     * na camada OrdemServicoService, que tem acesso aos repositórios/services.
     * Por enquanto, este método vai somar apenas a mão de obra.
     * A responsabilidade de somar o valor da peça (buscando no estoque) seria do Service.
     * @return O valor total calculado (BigDecimal).
     */
    public BigDecimal calcularTotal() {
        BigDecimal totalMaoDeObra = servicos.stream()
            .map(Servico::getPrecoMaoDeObra) // ALTERADO: Chama getPrecoMaoDeObra
            .filter(Objects::nonNull) // Garante que não haja valores nulos
            .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma BigDecimals
        
        // A lógica de somar o preço da peça viria aqui, mas precisaria do ItemEstoqueService
        // o que não é uma boa prática para uma classe de modelo.
        // O cálculo total da OS (M.O. + Peças) é melhor no OrdemServicoService.
        this.precoTotal = totalMaoDeObra;
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
            calcularTotal(); // Recalcula o total (apenas mão de obra) após adicionar
            // ALTERADO: servico.getDescricao() para servico.getObservacoes()
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getObservacoes() + "' adicionado. Novo total (apenas M.O.): " + this.precoTotal);
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
            calcularTotal(); // Recalcula o total (apenas mão de obra) após remover
            // ALTERADO: servico.getDescricao() para servico.getObservacoes()
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getObservacoes() + "' removido. Novo total (apenas M.O.): " + this.precoTotal);
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
        // ALTERADO: servico.getDescricao() para servico.getObservacoes()
        return "OrdemServico{" +
               "ID=" + id +
               ", Código='" + codigo + '\'' +
               ", Status='" + status.getDescricao() + '\'' +
               ", Preço Total=" + precoTotal +
               ", Veículo ID=" + idVeiculo +
               ", Cliente ID=" + idCliente +
               ", Mecânico ID=" + idMecanicoResponsavel +
               ", Serviços=" + servicosResumo +
               ", Data Abertura=" + dataAbertura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
               '}';
    }
}