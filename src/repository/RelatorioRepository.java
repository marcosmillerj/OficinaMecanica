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
import models.Relatorio;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class RelatorioRepository {

    private List<Relatorio> relatorios; // A lista de todos os relatórios em memória
    private JsonFileHandler<Relatorio> fileHandler; // O handler para salvar/carregar JSON

    /**
     * Construtor do RelatorioRepository.
     * Ao ser instanciado, tenta carregar os relatórios do arquivo JSON.
     */
    public RelatorioRepository() {
        // Define o tipo para o JsonFileHandler: uma Lista de Relatorio
        Type typeOfListOfRelatorios = new TypeToken<List<Relatorio>>() {}.getType();
        // Inicializa o fileHandler com o nome do arquivo específico para relatórios
        this.fileHandler = new JsonFileHandler<>("relatorios.json", typeOfListOfRelatorios);

        // Carrega os relatórios ao iniciar o repositório
        this.relatorios = fileHandler.load();

        // CRÍTICO: Ajustar o próximo ID para Relatorio após o carregamento
        int maxId = 0;
        for (Relatorio relatorio : this.relatorios) {
            if (relatorio.getId() > maxId) {
                maxId = relatorio.getId();
            }
        }
        // Garante que Relatorio.proximoId seja maior que qualquer ID existente
        Relatorio.proximoId = maxId + 1; 
        System.out.println("Contador de ID de Relatorio ajustado para: " + Relatorio.proximoId);
    }

    /**
     * Adiciona um novo relatório à coleção em memória e persiste as alterações no arquivo JSON.
     * @param relatorio O objeto Relatorio a ser adicionado (ID já deve ter sido atribuído no construtor do Relatorio).
     */
    public void adicionarRelatorio(Relatorio relatorio) {
        relatorios.add(relatorio);
        System.out.println("Relatório ID " + relatorio.getId() + " ('" + relatorio.getTitulo() + "') adicionado ao repositório.");
        fileHandler.save(relatorios); // Salva a lista atualizada no JSON
    }

    /**
     * Atualiza um relatório existente na coleção em memória e persiste as alterações.
     * @param relatorioParaAtualizar O objeto Relatorio que foi modificado.
     */
    public void atualizarRelatorio(Relatorio relatorioParaAtualizar) {
        // Como o objeto relatorioParaAtualizar já é uma referência da lista 'relatorios',
        // basta salvar a lista inteira para persistir as alterações.
        System.out.println("Relatório ID " + relatorioParaAtualizar.getId() + " ('" + relatorioParaAtualizar.getTitulo() + "') atualizado no repositório.");
        fileHandler.save(relatorios); // Salva a lista atualizada no JSON
    }

    /**
     * Remove um relatório da coleção em memória pelo seu ID e persiste as alterações.
     * @param id O ID do Relatorio a ser removido.
     * @return true se o relatório foi removido, false caso contrário.
     */
    public boolean removerRelatorio(int id) {
        boolean removido = relatorios.removeIf(relatorio -> relatorio.getId() == id);
        if (removido) {
            System.out.println("Relatório com ID " + id + " removido do repositório.");
            fileHandler.save(relatorios);
        } else {
            System.out.println("Relatório com ID " + id + " não encontrado para remoção no repositório.");
        }
        return removido;
    }

    /**
     * Busca um relatório pelo seu ID.
     * @param id O ID do Relatorio a ser buscado.
     * @return Um Optional contendo o Relatorio se encontrado, ou um Optional vazio.
     */
    public Optional<Relatorio> buscarRelatorioPorId(int id) {
        for (Relatorio r : relatorios) {
            if (r.getId() == id) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Lista todos os relatórios existentes no repositório.
     * @return Uma lista (cópia) de todos os relatórios.
     */
    public List<Relatorio> listarTodosRelatorios() {
        return new ArrayList<>(relatorios); // Retorna uma nova lista para encapsulamento
    }
}