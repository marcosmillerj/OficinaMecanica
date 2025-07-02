/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe utilitária responsável por salvar e carregar listas de objetos
 * em formato JSON para arquivos, garantindo o fechamento seguro dos recursos
 * utilizando o bloco `finally`.
 *
 * @param <T> O tipo de objeto contido na lista a ser persistida.
 * @author marcos_miller
 */
public class JsonFileHandler<T> {

    private final String filePath; 
    private final Type typeToken;
    private final Gson gson;

    /**
     * Construtor para criar um manipulador de arquivo JSON.
     * @param filePath O caminho completo do arquivo (ex: "usuarios.json").
     * @param typeToken Um TypeToken que representa o tipo da lista a ser serializada/desserializada.
     */
    public JsonFileHandler(String filePath, Type typeToken) {
        this.filePath = filePath;
        this.typeToken = typeToken;

        
        this.gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .registerTypeAdapter(Optional.class, new OptionalAdapter<>())
                        .setPrettyPrinting()
                        .create();
    }

    /**
     * Salva uma lista de objetos em um arquivo JSON.
     * @param dataList A lista de objetos a ser salva.
     */
    public void save(List<T> dataList) {
        String json = gson.toJson(dataList);

        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath);
            writer.write(json);
            System.out.println("Dados salvos com sucesso em '" + filePath + "'.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados em '" + filePath + "': " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar FileWriter para '" + filePath + "': " + e.getMessage());
                }
            }
        }
    }

    /**
     * Carrega uma lista de objetos de um arquivo JSON.
     * @return A lista de objetos carregada, ou uma lista vazia se o arquivo não existir ou for inválido.
     */
    public List<T> load() {
        FileReader reader = null;
        List<T> dataList = null;

        try {
            reader = new FileReader(filePath);
            dataList = gson.fromJson(reader, typeToken);

            if (dataList != null) {
                System.out.println("Dados carregados com sucesso de '" + filePath + "'.");
            } else {
                dataList = new ArrayList<>();
            }

        } catch (IOException e) {
            System.err.println("Arquivo '" + filePath + "' não encontrado ou erro de leitura: " + e.getMessage());
            System.out.println("Retornando uma lista vazia de dados.");
            dataList = new ArrayList<>();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar FileReader para '" + filePath + "': " + e.getMessage());
                }
            }
        }
        return dataList;
    }
}