/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Objects;
import java.util.Optional;

/**
 * Representa um **elevador** na oficina mecânica.
 * Cada elevador possui um identificador único, um status de disponibilidade,
 * uma capacidade específica (se pode realizar alinhamento) e pode estar
 * ocupado por um veículo, cujo ID é opcionalmente armazenado.
 *
 * @author marcos_miller
 */
public class Elevador {
    
    private int id; // ID único do elevador (definido no ElevadorRepository)
    private boolean isDisponivel; // Indica se o elevador está livre ou ocupado
    private boolean capacidadeAlinhamento; // Indica se este elevador tem capacidade para alinhamento.
    
    // ID do veículo atualmente no elevador. Optional.empty() se estiver livre.
    // O estado do elevador (quem o ocupa) será persistido em JSON.
    private Optional<Integer> idVeiculoAtual; 

    /**
     * Construtor principal para criar uma nova instância de **Elevador**.
     * Este construtor é ideal para a primeira inicialização ou para cenários
     * onde o elevador é configurado como disponível e sem veículo associado.
     *
     * @param id O identificador único do elevador.
     * @param capacidadeAlinhamento Indica se este elevador possui capacidade para realizar serviços de alinhamento.
     */
    public Elevador(int id, boolean capacidadeAlinhamento) {
        this.id = id;
        this.isDisponivel = true; // Elevador começa disponível por padrão
        this.capacidadeAlinhamento = capacidadeAlinhamento;
        this.idVeiculoAtual = Optional.empty(); // Nenhum veículo associado inicialmente
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `Elevador` a partir de dados persistidos.
     * Garante que o campo `idVeiculoAtual` seja sempre um `Optional` válido
     * (nunca `null`), tratando casos em que a desserialização possa retornar `null`.
     *
     * @param id O identificador único do elevador.
     * @param isDisponivel O status de disponibilidade do elevador.
     * @param capacidadeAlinhamento A capacidade de alinhamento do elevador.
     * @param idVeiculoAtual O `Optional<Integer>` representando o ID do veículo atualmente no elevador.
     * Pode ser `Optional.empty()` se não houver veículo.
     */
    public Elevador(int id, boolean isDisponivel, boolean capacidadeAlinhamento, Optional<Integer> idVeiculoAtual) {
        this.id = id;
        this.isDisponivel = isDisponivel;
        this.capacidadeAlinhamento = capacidadeAlinhamento;
        this.idVeiculoAtual = Objects.requireNonNullElse(idVeiculoAtual, Optional.empty()); 
    }


    // --- Getters ---
    public int getId() { return id; }
    public boolean isDisponivel() { return isDisponivel; }
    public boolean temCapacidadeAlinhamento() { return capacidadeAlinhamento; }
    
    public Optional<Integer> getIdVeiculoAtual() { 
        return Objects.requireNonNullElse(idVeiculoAtual, Optional.empty()); 
    }

    /**
     * Ocupa o elevador com um veículo especificado pelo seu ID.
     * A operação é bem-sucedida apenas se o elevador estiver **disponível**.
     * Se o elevador já estiver ocupado, a operação falha.
     *
     * @param idVeiculo O identificador único do veículo que ocupará o elevador.
     * @return `true` se o elevador foi ocupado com sucesso; `false` se já estava ocupado.
     */
    public boolean ocupar(int idVeiculo) {
        if (isDisponivel) {
            this.isDisponivel = false;
            this.idVeiculoAtual = Optional.of(idVeiculo);
            System.out.println("[Elevador ID " + id + "] Ocupado com Veículo ID " + idVeiculo + ". Capacidade Alinhamento: " + temCapacidadeAlinhamento() + ".");
            return true;
        }
        System.out.println("[Elevador ID " + id + "] Já está ocupado. Não pode ocupar Veículo ID " + idVeiculo + ".");
        return false;
    }

    /**
     * Libera o elevador, tornando-o novamente **disponível**.
     * A operação é bem-sucedida apenas se o elevador estiver **ocupado**.
     * Se o elevador já estiver disponível, nenhuma ação é necessária.
     *
     * @return `true` se o elevador foi liberado com sucesso; `false` se já estava disponível.
     */
    public boolean liberar() {
        if (!isDisponivel) {
            System.out.println("[Elevador ID " + id + "] Liberado. Veículo ID " + getIdVeiculoAtual().orElse(-1) + " saiu."); 
            this.isDisponivel = true;
            this.idVeiculoAtual = Optional.empty();
            return true;
        }
        System.out.println("[Elevador ID " + id + "] Já está disponível. Nenhuma ação necessária.");
        return false;
    }

    /**
     * Retorna uma representação em String do objeto Elevador,
     * detalhando seu ID, status (disponível ou ocupado por qual veículo)
     * e sua capacidade (se é para alinhamento ou geral).
     *
     * @return Uma String formatada com os detalhes do elevador.
     */
    @Override
    public String toString() {
        String statusElevador = isDisponivel ? "Disponível" : "Ocupado com Veículo ID " + getIdVeiculoAtual().orElse(-1); 
        String capacidade = temCapacidadeAlinhamento() ? " (Alinhamento)" : " (Geral)";
        return "Elevador {ID: " + id + " | Status: " + statusElevador + capacidade + "}";
    }
}