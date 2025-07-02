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
import models.RegistroPonto;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class PontoRepository {

    private List<RegistroPonto> registros;
    private JsonFileHandler<RegistroPonto> fileHandler;

    /**
     * Construtor do PontoRepository.
     * 
     */
    public PontoRepository() {
        Type typeOfListOfRegistros = new TypeToken<List<RegistroPonto>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("registros_ponto.json", typeOfListOfRegistros);
        this.registros = fileHandler.load();

        int maxId = 0;
        for (RegistroPonto registro : this.registros) {
            if (registro.getId() > maxId) {
                maxId = registro.getId();
            }
        }
        RegistroPonto.proximoId = maxId + 1;
    }

    /**
     * Adiciona um novo registro de ponto à coleção e persiste as alterações.
     * @param registro O objeto RegistroPonto a ser adicionado.
     */
    public void adicionarRegistro(RegistroPonto registro) {
        registros.add(registro);
        fileHandler.save(registros);
    }

    /**
     * Atualiza um registro de ponto existente na coleção e persiste as alterações.
     * Geralmente usado para adicionar a dataHoraSaida a um registro de entrada.
     * @param registroParaAtualizar O objeto RegistroPonto que foi modificado (referência já existente na lista).
     */
    public void atualizarRegistro(RegistroPonto registroParaAtualizar) {
        fileHandler.save(registros);
    }

    /**
     * Busca o último registro de ponto de ENTRADA sem uma SAÍDA registrada para um usuário.
     * @param idUsuario O ID do usuário.
     * @return Um Optional contendo o RegistroPonto, se encontrado, ou um Optional vazio.
     */
    public Optional<RegistroPonto> buscarUltimoPontoAbertoPorUsuario(int idUsuario) {
        for (int i = registros.size() - 1; i >= 0; i--) {
            RegistroPonto registro = registros.get(i);
            if (registro.getIdUsuario() == idUsuario && registro.getDataHoraSaida() == null) {
                return Optional.of(registro);
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todos os registros de ponto para um usuário específico.
     * @param idUsuario O ID do usuário.
     * @return Uma lista (cópia) de todos os registros de ponto do usuário.
     */
    public List<RegistroPonto> listarRegistrosPorUsuario(int idUsuario) {
        List<RegistroPonto> registrosDoUsuario = new ArrayList<>();
        for (RegistroPonto registro : registros) {
            if (registro.getIdUsuario() == idUsuario) {
                registrosDoUsuario.add(registro);
            }
        }
        return registrosDoUsuario;
    }
}
