/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import models.enums.StatusOrdem;
import observers.IObservavelOrdemServico;
import observers.IObservadorOrdemServico; 

/**
 *Classe que representa uma Ordem de Serviço na oficina mecânica
 *Implementa o padrão Observer para notificar sobre mudanças de status
 * @author barbo
 */
public class OrdemServico implements IObservavelOrdemServico {

    // Adição de ID único para a Ordem de Serviço
    public static int proximoId = 1; // Assim como em Usuario e RegistroPonto

    private int id; // NOVO: ID único da Ordem de Serviço
    private String codigo; // Mantém o código, pode ser um gerado ou manual (ex: OS-001)
    private LocalDateTime dataAbertura; // Renomeado para clareza (data de abertura)
    private BigDecimal precoTotal; // Alterado para BigDecimal para precisão monetária
    private int idVeiculo; // Referência por ID para o Veículo
    private int idCliente; // Referência por ID para o Cliente
    private int idMecanicoResponsavel; // Referência por ID para o Mecânico
    private StatusOrdem status;
    private final List<Servico> servicos; // Lista de serviços que compõem a OS
    private final List<IObservadorOrdemServico> observadores; // Observadores para o padrão Observer

    // Construtor
    // Refatorado para usar IDs das entidades relacionadas e BigDecimal
    public OrdemServico(String codigo, LocalDateTime dataAbertura, int idVeiculo,
                        int idCliente, int idMecanicoResponsavel, StatusOrdem status) {
        this.id = proximoId++; // Atribui ID único
        this.codigo = codigo;
        this.dataAbertura = dataAbertura;
        this.precoTotal = BigDecimal.ZERO; // Inicializa com zero, será calculado pelos serviços
        this.idVeiculo = idVeiculo;
        this.idCliente = idCliente;
        this.idMecanicoResponsavel = idMecanicoResponsavel;
        this.status = status;
        this.servicos = new ArrayList<>(); // Inicializa a lista vazia, serviços serão adicionados
        this.observadores = new ArrayList<>();
    }

    // --- MÉTODOS PARA PERSISTÊNCIA (Gson) ---
    // Construtor adicional para o Gson (ao carregar do JSON)
    // Este construtor permite ao Gson reconstruir o objeto com todos os seus atributos,
    // incluindo ID e a lista de serviços já populada. Observadores não são persistidos.
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
        this.servicos = new ArrayList<>(servicos); // Garante que a lista é uma nova instância
        this.observadores = new ArrayList<>(); // Observadores não são persistidos, são adicionados em tempo de execução
    }


    // --- MÉTODOS DO PADRÃO OBSERVER (Corretos!) ---
    // (Mantidos como estão, pois já estão bem implementados)

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
            obs.notificarStatusOrdem(this); // Passa a si mesma (a OrdemServico) para o assinante.
        }
    }

    // --- Getters e Setters ---

    public int getId() { return id; } // Getter para o novo ID

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDateTime getDataAbertura() { return dataAbertura; } // Getter para a data de abertura
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public BigDecimal getPrecoTotal() { return precoTotal; } // Getter para BigDecimal
    // setPrecoTotal (direto) NÃO é recomendado, pois o preço é CALCULADO.
    // Pode haver um setPrecoTotal interno para o cálculo, mas não público.

    public int getIdVeiculo() { return idVeiculo; } // Getter para ID do Veículo
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public int getIdCliente() { return idCliente; } // Getter para ID do Cliente
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdMecanicoResponsavel() { return idMecanicoResponsavel; } // Getter para ID do Mecânico
    public void setIdMecanicoResponsavel(int idMecanicoResponsavel) { this.idMecanicoResponsavel = idMecanicoResponsavel; }

    public StatusOrdem getStatus() { return status; }
    // setStatus SEM notificação (para uso interno)
    public void setStatus(StatusOrdem status) { this.status = status; }

    public List<Servico> getServicos() {
        return new ArrayList<>(servicos); // Retorna uma cópia para encapsulamento
    }

    // --- Métodos de Comportamento ---

    /**
     * Calcula o preço total da Ordem de Serviço somando os preços de todos os serviços e peças.
     * Atualiza o atributo precoTotal da OS.
     * @return O valor total calculado (BigDecimal).
     */
    public BigDecimal calcularTotal() {
        // Usa BigDecimal para somar os preços dos serviços com precisão
        this.precoTotal = servicos.stream()
                                  .map(Servico::getPreco) // Assume que Servico.getPreco() retorna BigDecimal
                                  .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma BigDecimals
        return this.precoTotal;
    }

    /**
     * Adiciona um serviço a ser realizado nesta Ordem de Serviço.
     * Após adicionar, recalcula o preço total.
     * @param servico O serviço a ser adicionado.
     */
    public void adicionarServico(Servico servico) {
        if (servico != null) {
            servicos.add(servico);
            calcularTotal(); // Recalcula o total após adicionar
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getDescricao() + "' adicionado. Novo total: " + this.precoTotal);
        }
    }

    /**
     * Remove um serviço desta Ordem de Serviço.
     * Após remover, recalcula o preço total.
     * @param servico O serviço a ser removido.
     * @return true se o serviço foi removido, false caso contrário.
     */
    public boolean removerServico(Servico servico) {
        boolean removido = servicos.remove(servico);
        if (removido) {
            calcularTotal(); // Recalcula o total após remover
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getDescricao() + "' removido. Novo total: " + this.precoTotal);
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
            System.out.println("\n[LOG:OrdemServico " + this.codigo + "] Status alterado para -> " + novoStatus.getDescricao()); // Usa getDescricao()
            notificarObservadores(); // CHAMA OS OBSERVADORES AQUI!
        } else {
            System.out.println("\n[LOG:OrdemServico " + this.codigo + "] Status já é " + novoStatus.getDescricao() + ". Nenhuma alteração/notificação.");
        }
    }

    @Override
    public String toString() {
        String servicosResumo = servicos.isEmpty() ? "Nenhum" : servicos.size() + " serviço(s)";
        return "OrdemServico{" +
               "ID=" + id +
               ", Código='" + codigo + '\'' +
               ", Status='" + status.getDescricao() + '\'' + // Usando getDescricao()
               ", Preço Total=" + precoTotal +
               ", Veículo ID=" + idVeiculo +
               ", Cliente ID=" + idCliente +
               ", Mecânico ID=" + idMecanicoResponsavel +
               ", Serviços=" + servicosResumo +
               ", Data Abertura=" + dataAbertura.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
               '}';
    }

    public int getClienteId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getVeiculoId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}