/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.Agendamento;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class AgendamentoRepository {

    private List<Agendamento> agendamentos;
    private JsonFileHandler<Agendamento> fileHandler;

    /**
     * Construtor do AgendamentoRepository.
     * 
     */
    public AgendamentoRepository() {
        Type typeOfListOfAgendamentos = new TypeToken<List<Agendamento>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("agendamentos.json", typeOfListOfAgendamentos);
        this.agendamentos = fileHandler.load();

        int maxId = 0;
        for (Agendamento agendamento : this.agendamentos) {
            if (agendamento.getId() > maxId) {
                maxId = agendamento.getId();
            }
        }
        Agendamento.proximoId = maxId + 1;
        System.out.println("Contador de ID de Agendamento ajustado para: " + Agendamento.proximoId);
    }

    /**
     * Adiciona um novo agendamento à coleção em memória e persiste as alterações no arquivo JSON.
     * @param agendamento O objeto Agendamento a ser adicionado.
     */
    public void adicionarAgendamento(Agendamento agendamento) {
        agendamentos.add(agendamento);
        System.out.println("Agendamento ID " + agendamento.getId() + " adicionado ao repositório para Cliente ID " + agendamento.getIdCliente() + ".");
        fileHandler.save(agendamentos);
    }

    /**
     * Atualiza um agendamento existente na coleção em memória e persiste as alterações.
     * Utilizado quando um atributo do objeto Agendamento é modificado.
     * @param agendamentoParaAtualizar O objeto Agendamento que foi modificado.
     */
    public void atualizarAgendamento(Agendamento agendamentoParaAtualizar) {
        System.out.println("Agendamento ID " + agendamentoParaAtualizar.getId() + " para Cliente ID " + agendamentoParaAtualizar.getIdCliente() + " atualizado no repositório.");
        fileHandler.save(agendamentos);
    }


    /**
     * Busca um agendamento pelo seu ID.
     * @param id O ID do Agendamento a ser buscado.
     * @return Um Optional contendo o Agendamento se encontrado, ou um Optional vazio.
     */
    public Optional<Agendamento> buscarAgendamentoPorId(int id) {
        for (Agendamento a : agendamentos) {
            if (a.getId() == id) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todos os agendamentos existentes no repositório.
     * @return Uma lista (cópia) de todos os agendamentos.
     */
    public List<Agendamento> listarTodosAgendamentos() {
        return new ArrayList<>(agendamentos);
    }

    
    /**
     * Lista agendamentos por ID do veículo.
     * @param idVeiculo ID do veículo.
     * @return Lista de agendamentos para o veículo.
     */
    public List<Agendamento> listarAgendamentosPorVeiculo(int idVeiculo) {
        List<Agendamento> agendamentosFiltrados = new ArrayList<>();
        for (Agendamento a : agendamentos) {
            if (a.getIdVeiculo() == idVeiculo) {
                agendamentosFiltrados.add(a);
            }
        }
        return agendamentosFiltrados;
    }
}
