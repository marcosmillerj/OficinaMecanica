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

    private List<Servico> servicos; // A lista de todos os serviços em memória
    private JsonFileHandler<Servico> fileHandler; // O handler para salvar/carregar JSON

    /**
     * Construtor do ServicoRepository.
     * Ao ser instanciado, tenta carregar os serviços do arquivo JSON.
     */
    public ServicoRepository() {
        // Define o tipo para o JsonFileHandler: uma Lista de Servico
        Type typeOfListOfServicos = new TypeToken<List<Servico>>() {}.getType();
        // Inicializa o fileHandler com o nome do arquivo específico para serviços
        this.fileHandler = new JsonFileHandler<>("servicos.json", typeOfListOfServicos);

        // Carrega os serviços ao iniciar o repositório
        this.servicos = fileHandler.load();

        // CRÍTICO: Ajustar o próximo ID para Servico após o carregamento
        // Isso evita que novos serviços tenham IDs duplicados com os já carregados do arquivo.
        int maxId = 0;
        for (Servico servico : this.servicos) {
            if (servico.getId() > maxId) {
                maxId = servico.getId();
            }
        }
        Servico.proximoId = maxId + 1; // Ajusta o contador estático na classe Servico
        System.out.println("Contador de ID de Servico ajustado para: " + Servico.proximoId);
    }

    /**
     * Adiciona um novo serviço à coleção em memória e persiste as alterações no arquivo JSON.
     * @param servico O objeto Servico a ser adicionado.
     */
    public void adicionarServico(Servico servico) {
        servicos.add(servico);
        System.out.println("Serviço '" + servico.getDescricao() + "' (ID: " + servico.getId() + ") adicionado ao repositório.");
        fileHandler.save(servicos); // Salva a lista atualizada no JSON
    }

    /**
     * Atualiza um serviço existente na coleção em memória e persiste as alterações.
     * Utilizado quando um atributo do objeto Servico é modificado.
     * @param servicoParaAtualizar O objeto Servico que foi modificado (referência já existente na lista).
     */
    public void atualizarServico(Servico servicoParaAtualizar) {
        // Como o objeto servicoParaAtualizar já é uma referência da lista 'servicos',
        // basta salvar a lista inteira para persistir as alterações.
        System.out.println("Serviço '" + servicoParaAtualizar.getDescricao() + "' (ID: " + servicoParaAtualizar.getId() + ") atualizado no repositório.");
        fileHandler.save(servicos); // Salva a lista atualizada no JSON
    }
    
    /**
     * Remove um serviço da coleção em memória pelo seu ID e persiste as alterações.
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
     * Busca um serviço pelo seu ID.
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
     * Busca um serviço pelo seu código.
     * @param codigo O código do Servico a ser buscado.
     * @return Um Optional contendo o Servico se encontrado, ou um Optional vazio.
     */
    public Optional<Servico> buscarServicoPorCodigo(String codigo) {
        for (Servico s : servicos) {
            if (s.getCodigo().equalsIgnoreCase(codigo)) { // Comparação case-insensitive
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todos os serviços existentes no repositório.
     * @return Uma lista (cópia) de todos os serviços.
     */
    public List<Servico> listarTodosServicos() {
        return new ArrayList<>(servicos); // Retorna uma nova lista para encapsulamento
    }
}
