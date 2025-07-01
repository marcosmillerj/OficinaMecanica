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
 * Representa uma **Ordem de Serviço (OS)** na oficina mecânica.
 * Uma Ordem de Serviço detalha os serviços a serem realizados em um veículo,
 * associando-o a um cliente e um mecânico responsável. Ela possui um status
 * que reflete seu progresso e implementa o padrão Observer para notificar
 * partes interessadas sobre mudanças de status.
 *
 * @author barbo
 */
public class OrdemServico implements IObservavelOrdemServico {

    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de OrdemServico.
     * Garante que cada OS receba um identificador exclusivo ao ser criada.
     */
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
     * Construtor principal para criar uma nova instância de **Ordem de Serviço**.
     * Atribui um ID único automaticamente e inicializa o preço total como zero.
     * A lista de serviços é inicializada vazia, e a lista de observadores também.
     * Realiza validações para garantir que parâmetros essenciais não sejam nulos.
     *
     * @param codigo O código identificador da Ordem de Serviço. Não pode ser nulo.
     * @param dataAbertura A data e hora de abertura da Ordem de Serviço. Não pode ser nula.
     * @param idVeiculo O identificador único do veículo associado a esta OS.
     * @param idCliente O identificador único do cliente proprietário do veículo.
     * @param idMecanicoResponsavel O identificador único do mecânico responsável pela OS.
     * @param status O status inicial da Ordem de Serviço. Não pode ser nulo.
     * @throws NullPointerException se `codigo`, `dataAbertura` ou `status` forem nulos.
     */
    public OrdemServico(String codigo, LocalDateTime dataAbertura, int idVeiculo,
                        int idCliente, int idMecanicoResponsavel, StatusOrdem status) {
        this.id = proximoId++;
        this.codigo = Objects.requireNonNull(codigo, "Código da OS não pode ser nulo.");
        this.dataAbertura = Objects.requireNonNull(dataAbertura, "Data de abertura da OS não pode ser nula.");
        this.precoTotal = BigDecimal.ZERO; // Inicializa com zero
        this.idVeiculo = idVeiculo;
        this.idCliente = idCliente;
        this.idMecanicoResponsavel = idMecanicoResponsavel;
        this.status = Objects.requireNonNull(status, "Status inicial da OS não pode ser nulo.");
        // Garante que idElevadorAtual seja Optional.empty() se não for explicitamente fornecido.
        this.idElevadorAtual = Optional.empty(); 
        this.servicos = new ArrayList<>(); // Inicializa lista de serviços vazia
        this.observadores = new ArrayList<>(); // Inicializa lista de observadores vazia
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `OrdemServico` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID,
     * status, elevador atual e a lista de serviços já associados.
     *
     * @param id O identificador único da Ordem de Serviço.
     * @param codigo O código da Ordem de Serviço.
     * @param dataAbertura A data e hora de abertura da Ordem de Serviço.
     * @param precoTotal O preço total (já calculado) da Ordem de Serviço.
     * @param idVeiculo O identificador do veículo.
     * @param idCliente O identificador do cliente.
     * @param idMecanicoResponsavel O identificador do mecânico responsável.
     * @param status O status atual da Ordem de Serviço.
     * @param idElevadorAtual Um `Optional<Integer>` representando o ID do elevador que a OS está utilizando.
     * @param servicos A lista de serviços já associados a esta Ordem de Serviço.
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
        this.servicos = (servicos != null) ? new ArrayList<>(servicos) : new ArrayList<>(); // Garante nova instância.
        this.observadores = new ArrayList<>(); // Observadores sempre iniciam vazios em desserialização
    }


    // --- MÉTODOS DO PADRÃO OBSERVER ---
    /**
     * Adiciona um observador à lista de observadores desta Ordem de Serviço.
     * O observador será notificado sobre mudanças no status da OS.
     * Se o observador já estiver na lista, ele não é adicionado novamente.
     *
     * @param obs A instância do observador a ser adicionado.
     */
    @Override
    public void adicionarObservador(IObservadorOrdemServico obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Assinante de OS adicionado.");
        }
    }

    /**
     * Remove um observador da lista de observadores desta Ordem de Serviço.
     * O observador deixará de receber notificações sobre mudanças no status da OS.
     *
     * @param obs A instância do observador a ser removido.
     */
    @Override
    public void removerObservador(IObservadorOrdemServico obs) {
        observadores.remove(obs);
        System.out.println("[LOG:OrdemServico " + this.codigo + "] Assinante de OS removido.");
    }

    /**
     * Notifica todos os observadores registrados sobre uma mudança no estado desta Ordem de Serviço.
     * Geralmente invocado após uma alteração de status.
     */
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
        return new ArrayList<>(servicos); // Retorna uma cópia para encapsulamento
    }

    // --- Métodos de Comportamento ---

    /**
     * Calcula o preço total da mão de obra para esta Ordem de Serviço,
     * somando os preços de mão de obra de todos os serviços associados.
     * Este cálculo **NÃO INCLUI** o preço de quaisquer peças. O preço total final (com peças)
     * é tipicamente calculado em uma camada de serviço (e.g., `OrdemServicoService`).
     *
     * @return O valor total da mão de obra calculada em `BigDecimal`.
     */
    public BigDecimal calcularTotal() {
        this.precoTotal = servicos.stream()
            .map(Servico::getPrecoMaoDeObra)
            .filter(Objects::nonNull) // Garante que apenas preços não nulos sejam somados
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.precoTotal;
    }

    /**
     * Adiciona um serviço à lista de serviços a serem realizados nesta Ordem de Serviço.
     * Após a adição bem-sucedida de um serviço, o preço total da mão de obra da OS é recalculado.
     *
     * @param servico O objeto {@link Servico} a ser adicionado à Ordem de Serviço. Não pode ser nulo.
     */
    public void adicionarServico(Servico servico) {
        if (servico != null) {
            servicos.add(servico);
            calcularTotal(); // Recalcula o total de mão de obra
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getObservacoes() + "' adicionado. Novo total (M.O.): R$ " + String.format("%.2f", this.precoTotal));
        }
    }

    /**
     * Remove um serviço da lista de serviços associados a esta Ordem de Serviço.
     * Após a remoção de um serviço, o preço total da mão de obra da OS é recalculado.
     *
     * @param servico O objeto {@link Servico} a ser removido da Ordem de Serviço.
     * @return `true` se o serviço foi encontrado e removido com sucesso; `false` caso contrário.
     */
    public boolean removerServico(Servico servico) {
        boolean removido = servicos.remove(servico);
        if (removido) {
            calcularTotal(); // Recalcula o total de mão de obra após a remoção
            System.out.println("[LOG:OrdemServico " + this.codigo + "] Serviço '" + servico.getObservacoes() + "' removido. Novo total (M.O.): R$ " + String.format("%.2f", this.precoTotal));
        }
        return removido;
    }

    /**
     * Altera o status atual da Ordem de Serviço para o novo status especificado.
     * Se o status for realmente alterado, todos os observadores registrados são notificados.
     * Se o novo status for o mesmo que o atual, nenhuma ação ou notificação é realizada.
     *
     * @param novoStatus O novo {@link StatusOrdem} para o qual a ordem será atualizada. Não pode ser nulo.
     */
    public void alterarStatus(StatusOrdem novoStatus) {
        if (this.status != novoStatus) {
            this.status = novoStatus;
            System.out.println("\n[LOG:OrdemServico " + this.codigo + "] Status alterado para -> " + novoStatus.getDescricao());
            notificarObservadores(); // Notifica os observadores sobre a mudança
        } else {
            System.out.println("\n[LOG:OrdemServico " + this.codigo + "] Status já é " + novoStatus.getDescricao() + ". Nenhuma alteração/notificação.");
        }
    }

    /**
     * Retorna uma representação em String do objeto Ordem de Serviço,
     * incluindo detalhes como ID, código, status, preço total de mão de obra,
     * IDs de veículo, cliente e mecânico, informações do elevador (se aplicável),
     * resumo dos serviços e a data de abertura.
     *
     * @return Uma String formatada com os detalhes da Ordem de Serviço.
     */
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
    
    /**
     * Compara este objeto OrdemServico com o objeto especificado para verificar igualdade.
     * Duas Ordens de Serviço são consideradas iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com esta Ordem de Serviço.
     * @return `true` se o objeto especificado for igual a esta Ordem de Serviço, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdemServico that = (OrdemServico) o;
        return id == that.id;
    }

    /**
     * Retorna um valor de código hash para o objeto OrdemServico.
     * O código hash é baseado exclusivamente no **ID da Ordem de Serviço**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}