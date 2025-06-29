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

    private List<Agendamento> agendamentos; // A lista de todos os agendamentos em memória
    private JsonFileHandler<Agendamento> fileHandler; // O handler para salvar/carregar JSON

    /**
     * Construtor do AgendamentoRepository.
     * Ao ser instanciado, tenta carregar os agendamentos do arquivo JSON.
     */
    public AgendamentoRepository() {
        // Define o tipo para o JsonFileHandler: uma Lista de Agendamento
        Type typeOfListOfAgendamentos = new TypeToken<List<Agendamento>>() {}.getType();
        // Inicializa o fileHandler com o nome do arquivo específico para agendamentos
        this.fileHandler = new JsonFileHandler<>("agendamentos.json", typeOfListOfAgendamentos);

        // Carrega os agendamentos ao iniciar o repositório
        this.agendamentos = fileHandler.load();

        // CRÍTICO: Ajustar o próximo ID para Agendamento após o carregamento
        int maxId = 0;
        for (Agendamento agendamento : this.agendamentos) {
            if (agendamento.getId() > maxId) {
                maxId = agendamento.getId();
            }
        }
        Agendamento.proximoId = maxId + 1; // Ajusta o contador estático na classe Agendamento
        System.out.println("Contador de ID de Agendamento ajustado para: " + Agendamento.proximoId);
    }

    /**
     * Adiciona um novo agendamento à coleção em memória e persiste as alterações no arquivo JSON.
     * @param agendamento O objeto Agendamento a ser adicionado.
     */
    public void adicionarAgendamento(Agendamento agendamento) {
        agendamentos.add(agendamento);
        System.out.println("Agendamento ID " + agendamento.getId() + " adicionado ao repositório para Cliente ID " + agendamento.getIdCliente() + ".");
        fileHandler.save(agendamentos); // Salva a lista atualizada no JSON
    }

    /**
     * Atualiza um agendamento existente na coleção em memória e persiste as alterações.
     * Utilizado quando um atributo do objeto Agendamento é modificado.
     * @param agendamentoParaAtualizar O objeto Agendamento que foi modificado.
     */
    public void atualizarAgendamento(Agendamento agendamentoParaAtualizar) {
        // Como o objeto agendamentoParaAtualizar já é uma referência da lista 'agendamentos',
        // basta salvar a lista inteira para persistir as alterações.
        System.out.println("Agendamento ID " + agendamentoParaAtualizar.getId() + " para Cliente ID " + agendamentoParaAtualizar.getIdCliente() + " atualizado no repositório.");
        fileHandler.save(agendamentos); // Salva a lista atualizada no JSON
    }

    /**
     * Remove um agendamento da coleção em memória pelo seu ID e persiste as alterações.
     * @param id O ID do Agendamento a ser removido.
     * @return true se o agendamento foi removido, false caso contrário.
     */
    public boolean removerAgendamento(int id) {
        boolean removido = agendamentos.removeIf(agendamento -> agendamento.getId() == id);
        if (removido) {
            System.out.println("Agendamento com ID " + id + " removido do repositório.");
            fileHandler.save(agendamentos);
        } else {
            System.out.println("Agendamento com ID " + id + " não encontrado para remoção no repositório.");
        }
        return removido;
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
        return new ArrayList<>(agendamentos); // Retorna uma nova lista para encapsulamento
    }

    /**
     * Lista agendamentos por ID do cliente.
     * @param idCliente ID do cliente.
     * @return Lista de agendamentos para o cliente.
     */
    public List<Agendamento> listarAgendamentosPorCliente(int idCliente) {
        List<Agendamento> agendamentosFiltrados = new ArrayList<>();
        for (Agendamento a : agendamentos) {
            if (a.getIdCliente() == idCliente) {
                agendamentosFiltrados.add(a);
            }
        }
        return agendamentosFiltrados;
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
