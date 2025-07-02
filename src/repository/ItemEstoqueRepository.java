/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author marcos_miller
 */

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.ItemEstoque;
import util.JsonFileHandler;

public class ItemEstoqueRepository {

    private List<ItemEstoque> itens;
    private JsonFileHandler<ItemEstoque> fileHandler;

    public ItemEstoqueRepository() {
        Type typeOfListOfItens = new TypeToken<List<ItemEstoque>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("itens_estoque.json", typeOfListOfItens);
        this.itens = fileHandler.load();

        int maxId = 0;
        for (ItemEstoque item : this.itens) {
            if (item.getId() > maxId) {
                maxId = item.getId();
            }
        }
        ItemEstoque.proximoId = maxId + 1;
    }

    public void adicionarItem(ItemEstoque item) {
        itens.add(item);
        System.out.println("Item de Estoque '" + item.getNome() + "' (ID: " + item.getId() + ") adicionado ao repositório.");
        fileHandler.save(itens);
    }

    public boolean removerItem(int id) {
        boolean removido = itens.removeIf(item -> item.getId() == id);
        if (removido) {
            System.out.println("Item de Estoque com ID " + id + " removido.");
            fileHandler.save(itens);
        } else {
            System.out.println("Item de Estoque com ID " + id + " não encontrado para remoção.");
        }
        return removido;
    }

    public void atualizarItem(ItemEstoque itemParaAtualizar) {
        System.out.println("Item de Estoque '" + itemParaAtualizar.getNome() + "' (ID: " + itemParaAtualizar.getId() + ") atualizado no repositório.");
        fileHandler.save(itens);
    }

    public List<ItemEstoque> listarItens() {
        return new ArrayList<>(itens);
    }

    public Optional<ItemEstoque> buscarItemPorId(int id) {
        for (ItemEstoque item : itens) {
            if (item.getId() == id) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public Optional<ItemEstoque> buscarItemPorCodigo(String codigo) {
        for (ItemEstoque item : itens) {
            if (item.getCodigo().equalsIgnoreCase(codigo)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public Optional<ItemEstoque> buscarItemPorNome(String nome) {
        for (ItemEstoque item : itens) {
            if (item.getNome().equalsIgnoreCase(nome)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}