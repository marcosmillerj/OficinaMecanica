/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.ItemEstoque;
import repository.ItemEstoqueRepository;

/**
 *
 * @author marcos_miller
 */
public class ItemEstoqueService {

    private ItemEstoqueRepository itemEstoqueRepository; // Dependência do repositório de estoque

    public ItemEstoqueService(ItemEstoqueRepository itemEstoqueRepository) {
        this.itemEstoqueRepository = itemEstoqueRepository;
    }

    /**
     * Adiciona um novo item ao estoque.
     * @param codigo Código único do item.
     * @param nome Nome do item.
     * @param quantidade Quantidade inicial.
     * @param precoUnitario Preço unitário.
     * @return O ItemEstoque criado.
     * @throws IllegalStateException Se um item com o mesmo código já existe.
     * @throws IllegalArgumentException Se dados de entrada forem inválidos (quantidade/preço negativo).
     */
    public ItemEstoque adicionarItem(String codigo, String nome, int quantidade, BigDecimal precoUnitario) throws IllegalStateException, IllegalArgumentException { // 'codigo' como parâmetro
        Objects.requireNonNull(codigo, "Código do item não pode ser nulo.");
        Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        Objects.requireNonNull(precoUnitario, "Preço unitário não pode ser nulo.");

        // ALTERADO: Validação de unicidade pelo CÓDIGO, não pelo nome
        if (itemEstoqueRepository.buscarItemPorCodigo(codigo).isPresent()) {
            throw new IllegalStateException("Erro: Item com código '" + codigo + "' já cadastrado no estoque.");
        }
        
        // Validações de quantidade e preço já são feitas no construtor/setters do ItemEstoque
        ItemEstoque novoItem = new ItemEstoque(codigo, nome, quantidade, precoUnitario); // Passa o código para o construtor
        itemEstoqueRepository.adicionarItem(novoItem); // Delega ao repositório
        return novoItem;
    }

    /**
     * Atualiza os dados de um item de estoque existente.
     * @param id ID do item.
     * @param novoCodigo Novo código.
     * @param novoNome Novo nome.
     * @param novaQuantidade Nova quantidade.
     * @param novoPreco Novo preço unitário.
     * @return true se atualizado, false se não encontrado.
     * @throws IllegalStateException Se o novo código já pertencer a outro item.
     * @throws IllegalArgumentException Se dados de entrada forem inválidos.
     */
    public boolean atualizarItem(int id, String novoCodigo, String novoNome, int novaQuantidade, BigDecimal novoPreco) throws IllegalStateException, IllegalArgumentException { // NOVO PARÂMETRO 'novoCodigo'
        Objects.requireNonNull(novoCodigo, "Novo código do item não pode ser nulo.");
        Objects.requireNonNull(novoNome, "Novo nome do item não pode ser nulo.");
        Objects.requireNonNull(novoPreco, "Novo preço unitário não pode ser nulo.");

        Optional<ItemEstoque> itemOpt = itemEstoqueRepository.buscarItemPorId(id);
        if (itemOpt.isEmpty()) {
            return false; // Item não encontrado
        }
        ItemEstoque itemParaAtualizar = itemOpt.get();

        // ALTERADO: Validação de unicidade de CÓDIGO se for alterado e pertencer a outro item
        if (!itemParaAtualizar.getCodigo().equalsIgnoreCase(novoCodigo)) {
            Optional<ItemEstoque> existentePorCodigo = itemEstoqueRepository.buscarItemPorCodigo(novoCodigo);
            if (existentePorCodigo.isPresent() && existentePorCodigo.get().getId() != id) {
                throw new IllegalStateException("Erro: Novo código '" + novoCodigo + "' já cadastrado para outro item.");
            }
        }
        
        // As validações de quantidade e preço negativo são feitas nos setters do ItemEstoque
        itemParaAtualizar.setCodigo(novoCodigo); // Atualiza o código
        itemParaAtualizar.setNome(novoNome);
        itemParaAtualizar.setQuantidade(novaQuantidade);
        itemParaAtualizar.setPrecoUnitario(novoPreco);

        itemEstoqueRepository.atualizarItem(itemParaAtualizar); // Delega ao repositório (agora espera ItemEstoque)
        return true;
    }

    /**
     * Dá baixa em uma quantidade de um item do estoque.
     * @param id ID do item.
     * @param quantidadeBaixa Quantidade a ser removida.
     * @return true se a baixa foi dada com sucesso, false se o item não for encontrado.
     * @throws IllegalArgumentException Se a quantidade de baixa for negativa ou maior que a disponível.
     */
    public boolean darBaixaEstoque(int id, int quantidadeBaixa) throws IllegalArgumentException {
        if (quantidadeBaixa < 0) {
            throw new IllegalArgumentException("Quantidade de baixa não pode ser negativa.");
        }

        Optional<ItemEstoque> itemOpt = itemEstoqueRepository.buscarItemPorId(id);
        if (itemOpt.isEmpty()) {
            return false; // Item não encontrado
        }
        ItemEstoque item = itemOpt.get();

        if (item.getQuantidade() < quantidadeBaixa) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque para dar baixa. Disponível: " + item.getQuantidade());
        }

        item.setQuantidade(item.getQuantidade() - quantidadeBaixa);
        itemEstoqueRepository.atualizarItem(item); // Delega ao repositório
        System.out.println("Baixa de " + quantidadeBaixa + " unidades do item '" + item.getNome() + "' realizada. Novo estoque: " + item.getQuantidade());
        return true;
    }

    /**
     * Adiciona uma quantidade a um item do estoque (reestoque).
     * @param id ID do item.
     * @param quantidadeAdicao Quantidade a ser adicionada.
     * @return true se a adição foi bem-sucedida, false se o item não for encontrado.
     * @throws IllegalArgumentException Se a quantidade de adição for negativa.
     */
    public boolean adicionarEstoque(int id, int quantidadeAdicao) throws IllegalArgumentException {
        if (quantidadeAdicao < 0) {
            throw new IllegalArgumentException("Quantidade de adição não pode ser negativa.");
        }

        Optional<ItemEstoque> itemOpt = itemEstoqueRepository.buscarItemPorId(id);
        if (itemOpt.isEmpty()) {
            return false; // Item não encontrado
        }
        ItemEstoque item = itemOpt.get();

        item.setQuantidade(item.getQuantidade() + quantidadeAdicao);
        itemEstoqueRepository.atualizarItem(item); // Delega ao repositório
        System.out.println("Adição de " + quantidadeAdicao + " unidades do item '" + item.getNome() + "' realizada. Novo estoque: " + item.getQuantidade());
        return true;
    }

    // Métodos de busca e listagem
    public Optional<ItemEstoque> buscarItemPorId(int id) {
        return itemEstoqueRepository.buscarItemPorId(id);
    }

    public Optional<ItemEstoque> buscarItemPorCodigo(String codigo) { // NOVO: Busca por CÓDIGO
        return itemEstoqueRepository.buscarItemPorCodigo(codigo);
    }

    public Optional<ItemEstoque> buscarItemPorNome(String nome) {
        return itemEstoqueRepository.buscarItemPorNome(nome);
    }

    public List<ItemEstoque> listarTodosItens() {
        return itemEstoqueRepository.listarItens(); // Delega para o repositório
    }

    public boolean removerItem(int id) {
        return itemEstoqueRepository.removerItem(id); // Delega para o repositório
    }
}