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
import models.Servico;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class ServicoRepository {

    private List<Servico> servicos;
    private JsonFileHandler<Servico> fileHandler;

    /**
     * Construtor do ServicoRepository.
     *
     */
    public ServicoRepository() {
        Type typeOfListOfServicos = new TypeToken<List<Servico>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("servicos.json", typeOfListOfServicos);
        this.servicos = fileHandler.load();

        int maxId = 0;
        for (Servico servico : this.servicos) {
            if (servico.getId() > maxId) {
                maxId = servico.getId();
            }
        }
        Servico.proximoId = maxId + 1;
        System.out.println("Contador de ID de Servico ajustado para: " + Servico.proximoId);
    }

    /**
     * Adiciona um novo serviço (instância) à coleção em memória e persiste as alterações no arquivo JSON.
     * @param servico O objeto Servico a ser adicionado.
     */
    public void adicionarServico(Servico servico) {
        servicos.add(servico);
        System.out.println("Serviço ID " + servico.getId() + " ('" + servico.getObservacoes() + "') adicionado ao repositório.");
        fileHandler.save(servicos);
    }

    /**
     * Atualiza um serviço (instância) existente na coleção em memória e persiste as alterações.
     * Utilizado quando um atributo do objeto Servico é modificado.
     * @param servicoParaAtualizar O objeto Servico que foi modificado (referência já existente na lista).
     */
    public void atualizarServico(Servico servicoParaAtualizar) {
        System.out.println("Serviço ID " + servicoParaAtualizar.getId() + " ('" + servicoParaAtualizar.getObservacoes() + "') atualizado no repositório.");
        fileHandler.save(servicos);
    }
    
    /**
     * Remove um serviço (instância) da coleção em memória pelo seu ID e persiste as alterações.
     * @param id O ID do Servico a ser removido.
     * @return true se o serviço foi removido, false caso contrário.
     */
    public boolean removerServico(int id) {
        boolean removido = servicos.removeIf(servico -> servico.getId() == id);
        if (removido) {
            System.out.println("Serviço com ID " + id + " removido do repositório.");
            fileHandler.save(servicos);
        } else {
            System.out.println("Serviço com ID " + id + " não encontrado para remoção no repositório.");
        }
        return removido;
    }

    /**
     * Busca um serviço (instância) pelo seu ID.
     * @param id O ID do Servico a ser buscado.
     * @return Um Optional contendo o Servico se encontrado, ou um Optional vazio.
     */
    public Optional<Servico> buscarServicoPorId(int id) {
        for (Servico s : servicos) {
            if (s.getId() == id) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todos os serviços (instâncias) existentes no repositório.
     * @return Uma lista (cópia) de todos os serviços.
     */
    public List<Servico> listarTodosServicos() {
        return new ArrayList<>(servicos); 
    }
}
