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
import models.Elevador;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class ElevadorRepository {

    // A única instância da classe (privada e estática) para o padrão Singleton
    private static ElevadorRepository instance; 

    private List<Elevador> elevadores; // A coleção de elevadores (não é mais 'final' no sentido de ser imutável no conteúdo)
    private JsonFileHandler<Elevador> fileHandler; // Handler para persistência JSON

    /**
     * Construtor privado.
     * Tenta carregar os elevadores do arquivo JSON. Se não houver, inicializa com elevadores padrão.
     */
    private ElevadorRepository() {
        // Inicializa o fileHandler para Elevador
        Type typeOfListOfElevadores = new TypeToken<List<Elevador>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("elevadores.json", typeOfListOfElevadores);

        // Carrega os elevadores do arquivo JSON
        this.elevadores = fileHandler.load();

        // Se a lista carregada estiver vazia, significa que é a primeira execução ou o arquivo foi apagado.
        // Então, inicializa os elevadores padrão e os salva.
        if (this.elevadores.isEmpty()) {
            System.out.println("Nenhum elevador encontrado no arquivo. Inicializando com elevadores padrão...");
            elevadores.add(new Elevador(1, false)); // Elevador 1: Geral
            elevadores.add(new Elevador(2, false)); // Elevador 2: Geral
            elevadores.add(new Elevador(3, true));  // Elevador 3: Capacidade de Alinhamento
            fileHandler.save(elevadores); // Salva o estado inicial no JSON
            System.out.println("Elevadores padrão criados e salvos em 'elevadores.json'.");
        } else {
            System.out.println("Elevadores carregados do arquivo 'elevadores.json'.");
        }
        elevadores.forEach(System.out::println); // Imprime o estado atual dos elevadores (útil para debug)
    }

    /**
     * Método estático para obter a única instância de ElevadorRepository (Singleton).
     * @return A única instância de ElevadorRepository.
     */
    public static ElevadorRepository getInstance() {
        if (instance == null) {
            instance = new ElevadorRepository();
        }
        return instance;
    }

    /**
     * Busca um elevador pelo seu ID.
     * @param id O ID do elevador.
     * @return Um Optional contendo o Elevador se encontrado, ou um Optional vazio.
     */
    public Optional<Elevador> buscarElevadorPorId(int id) {
        for (Elevador elevador : elevadores) {
            if (elevador.getId() == id) {
                return Optional.of(elevador);
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todos os elevadores disponíveis.
     * @return Uma lista (cópia) de todos os elevadores disponíveis.
     */
    public List<Elevador> listarElevadoresDisponiveis() {
        List<Elevador> disponiveis = new ArrayList<>();
        for (Elevador elevador : elevadores) {
            if (elevador.isDisponivel()) {
                disponiveis.add(elevador);
            }
        }
        return disponiveis;
    }

    /**
     * Lista todos os elevadores (disponíveis ou ocupados).
     * @return Uma lista (cópia) de todos os elevadores.
     */
    public List<Elevador> listarTodosElevadores() {
        return new ArrayList<>(elevadores); // Retorna uma cópia da lista interna
    }

    /**
     * Persiste o estado atual da coleção de elevadores em arquivo JSON.
     * Chamado pelo ElevadorService após uma mudança de estado (ocupar/liberar).
     */
    public void persistirEstadoElevadores() {
        fileHandler.save(elevadores);
        System.out.println("Estado dos elevadores salvo em 'elevadores.json'.");
    }
}