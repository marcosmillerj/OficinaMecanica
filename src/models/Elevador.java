/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Optional;

/**
 *
 * @author marcos_miller
 */
public class Elevador {
    
    private int id;
    private boolean isDisponivel;
    private boolean especializadoEmAlinhamento;

    // ID do veículo atualmente no elevador (Optional.empty() se livre).
    // O estado do elevador (quem o ocupa) NÃO É PERSISTIDO em JSON automaticamente,
    // ele será reiniciado a cada execução do sistema.
    private Optional<Integer> idVeiculoAtual; 

    /**
     * Construtor para criar uma instância de Elevador.
     * @param id O ID único do elevador.
     * @param especializadoEmAlinhamento Indica se este elevador tem capacidade para alinhamento.
     */
    public Elevador(int id, boolean especializadoEmAlinhamento) {
        this.id = id;
        this.isDisponivel = true;
        this.especializadoEmAlinhamento = especializadoEmAlinhamento;
        this.idVeiculoAtual = Optional.empty();
    }

    // --- Getters ---
    public int getId() 
        { return id; }
    
    public boolean isDisponivel() 
        { return isDisponivel; }
    
    public boolean especializadoEmAlinhamento() 
        { return especializadoEmAlinhamento; }
    
    public Optional<Integer> getIdVeiculoAtual() 
        { return idVeiculoAtual; }

    /**
     * Ocupa o elevador com um veículo.
     * @param idVeiculo O ID do veículo que ocupará o elevador.
     * @return true se o elevador foi ocupado com sucesso, false se já estiver ocupado.
     */
    public boolean ocupar(int idVeiculo) {
        if (isDisponivel) {
            this.isDisponivel = false;
            this.idVeiculoAtual = Optional.of(idVeiculo);
            System.out.println("[Elevador ID " + id + "] Ocupado com Veículo ID " + idVeiculo + ". Capacidade Alinhamento: " + especializadoEmAlinhamento + ".");
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
            System.out.println("[Elevador ID " + id + "] Liberado. Veículo ID " + idVeiculoAtual.orElse(-1) + " saiu.");
            this.isDisponivel = true;
            this.idVeiculoAtual = Optional.empty();
            return true;
        }
        System.out.println("[Elevador ID " + id + "] Já está disponível. Nenhuma ação necessária.");
        return false;
    }

    @Override
    public String toString() {
        String status = isDisponivel ? "Disponível" : "Ocupado com Veículo ID " + idVeiculoAtual.orElse(-1);
        String capacidade = especializadoEmAlinhamento ? " (Alinhamento)" : " (Geral)";
        return "Elevador {ID: " + id + " | Status: " + status + capacidade + "}";
    }
}
