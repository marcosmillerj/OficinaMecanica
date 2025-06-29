/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.Elevador;

/**
 *
 * @author marcos_miller
 */
public class ElevadorRepository {

    private static ElevadorRepository instance;

    // A coleção de elevadores (vetor fixo, inicializado uma vez)
    private final List<Elevador> elevadores;

    /**
     * Construtor privado.
     * Inicializa a coleção fixa de elevadores.
     * Impede que outras classes criem instâncias diretamente.
     */
    private ElevadorRepository() {
        this.elevadores = new ArrayList<>();
        elevadores.add(new Elevador(1, false));
        elevadores.add(new Elevador(2, false));
        elevadores.add(new Elevador(3, true));
        
        System.out.println("ElevadorRepository inicializado com 3 elevadores fixos.");
        elevadores.forEach(System.out::println);
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
        return new ArrayList<>(elevadores);
    }
}