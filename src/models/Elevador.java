/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author marcos_miller
 */
public class Elevador {
    
    private int id; // ID único do elevador (definido no ElevadorRepository)
    private boolean isDisponivel; // Indica se o elevador está livre ou ocupado
    private boolean capacidadeAlinhamento; // Atributo renomeado
    
    // ID do veículo atualmente no elevador (Optional.empty() se livre).
    // O estado do elevador (quem o ocupa) AGORA SERÁ PERSISTIDO em JSON.
    private Optional<Integer> idVeiculoAtual; 

    /**
     * Construtor principal para criar uma instância de Elevador.
     * Usado na primeira inicialização do repositório.
     * @param id O ID único do elevador.
     * @param capacidadeAlinhamento Indica se este elevador tem capacidade para alinhamento.
     */
    public Elevador(int id, boolean capacidadeAlinhamento) {
        this.id = id;
        this.isDisponivel = true; // Elevador começa disponível por padrão
        this.capacidadeAlinhamento = capacidadeAlinhamento;
        this.idVeiculoAtual = Optional.empty(); // Nenhum veículo inicialmente
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * Garante que `idVeiculoAtual` sempre seja um `Optional` (nunca `null`).
     * @param id ID do elevador.
     * @param isDisponivel Status de disponibilidade do elevador.
     * @param capacidadeAlinhamento Capacidade de alinhamento do elevador.
     * @param idVeiculoAtual ID do veículo atualmente no elevador (Optional<Integer>).
     */
    public Elevador(int id, boolean isDisponivel, boolean capacidadeAlinhamento, Optional<Integer> idVeiculoAtual) {
        this.id = id;
        this.isDisponivel = isDisponivel;
        this.capacidadeAlinhamento = capacidadeAlinhamento;
        // <<< CORREÇÃO AQUI: Garante que idVeiculoAtual não seja null, mesmo que o Gson passe null
        this.idVeiculoAtual = Objects.requireNonNullElse(idVeiculoAtual, Optional.empty()); 
    }


    // --- Getters ---
    public int getId() { return id; }
    public boolean isDisponivel() { return isDisponivel; }
    public boolean temCapacidadeAlinhamento() { return capacidadeAlinhamento; }
    
    /**
     * Retorna o Optional<Integer> do ID do veículo atual.
     * GARANTE QUE NUNCA RETORNE NULL, SEMPRE UM OPTIONAL (VAZIO OU COM VALOR).
     * Isso evita NullPointerExceptions em cadeia.
     * @return Optional<Integer> do ID do veículo, ou Optional.empty() se não houver veículo.
     */
    public Optional<Integer> getIdVeiculoAtual() { 
        return Objects.requireNonNullElse(idVeiculoAtual, Optional.empty()); // <<< CORREÇÃO AQUI!
    }

    /**
     * Ocupa o elevador com um veículo.
     * @param idVeiculo O ID do veículo que ocupará o elevador.
     * @return true se o elevador foi ocupado com sucesso, false se já estiver ocupado.
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
     * Libera o elevador.
     * @return true se o elevador foi liberado com sucesso, false se já estiver disponível.
     */
    public boolean liberar() {
        if (!isDisponivel) {
            // Usa o getter defensivo para evitar NPE se por algum motivo idVeiculoAtual interno for null
            System.out.println("[Elevador ID " + id + "] Liberado. Veículo ID " + getIdVeiculoAtual().orElse(-1) + " saiu."); 
            this.isDisponivel = true;
            this.idVeiculoAtual = Optional.empty();
            return true;
        }
        System.out.println("[Elevador ID " + id + "] Já está disponível. Nenhuma ação necessária.");
        return false;
    }

    @Override
    public String toString() {
        // Usa o getter defensivo para evitar NPE
        String status = isDisponivel ? "Disponível" : "Ocupado com Veículo ID " + getIdVeiculoAtual().orElse(-1); 
        String capacidade = temCapacidadeAlinhamento() ? " (Alinhamento)" : " (Geral)";
        return "Elevador {ID: " + id + " | Status: " + status + capacidade + "}";
    }
}