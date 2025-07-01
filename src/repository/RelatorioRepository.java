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

    private List<Relatorio> relatorios;
    private JsonFileHandler<Relatorio> fileHandler;

    /**
     * Construtor do RelatorioRepository.
     * 
     */
    public RelatorioRepository() {
        Type typeOfListOfRelatorios = new TypeToken<List<Relatorio>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("relatorios.json", typeOfListOfRelatorios);
        this.relatorios = fileHandler.load();

        int maxId = 0;
        for (Relatorio relatorio : this.relatorios) {
            if (relatorio.getId() > maxId) {
                maxId = relatorio.getId();
            }
        }
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
        fileHandler.save(relatorios);
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
        return new ArrayList<>(relatorios); 
    }
}