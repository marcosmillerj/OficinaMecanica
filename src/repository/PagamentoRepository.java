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
import models.Pagamento;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class PagamentoRepository {

    private List<Pagamento> pagamentos; // A lista de todos os pagamentos em memória
    private JsonFileHandler<Pagamento> fileHandler; // O handler para salvar/carregar JSON

    /**
     * Construtor do PagamentoRepository.
     * Ao ser instanciado, tenta carregar os pagamentos do arquivo JSON.
     */
    public PagamentoRepository() {
        // Define o tipo para o JsonFileHandler: uma Lista de Pagamento
        Type typeOfListOfPagamentos = new TypeToken<List<Pagamento>>() {}.getType();
        // Inicializa o fileHandler com o nome do arquivo específico para pagamentos
        this.fileHandler = new JsonFileHandler<>("pagamentos.json", typeOfListOfPagamentos);

        // Carrega os pagamentos ao iniciar o repositório
        this.pagamentos = fileHandler.load();

        // CRÍTICO: Ajustar o próximo ID para Pagamento após o carregamento
        int maxId = 0;
        for (Pagamento pagamento : this.pagamentos) {
            if (pagamento.getId() > maxId) {
                maxId = pagamento.getId();
            }
        }
        Pagamento.proximoId = maxId + 1; // Ajusta o contador estático na classe Pagamento
        System.out.println("Contador de ID de Pagamento ajustado para: " + Pagamento.proximoId);
    }

    /**
     * Adiciona um novo pagamento à coleção em memória e persiste as alterações no arquivo JSON.
     * @param pagamento O objeto Pagamento a ser adicionado.
     */
    public void adicionarPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
        System.out.println("Pagamento ID " + pagamento.getId() + " adicionado ao repositório para OS ID " + pagamento.getIdOrdemServico().orElse(0) + ".");
        fileHandler.save(pagamentos); // Salva a lista atualizada no JSON
    }

    /**
     * Atualiza um pagamento existente na coleção em memória e persiste as alterações.
     * Utilizado quando um atributo do objeto Pagamento é modificado.
     * @param pagamentoParaAtualizar O objeto Pagamento que foi modificado.
     */
    public void atualizarPagamento(Pagamento pagamentoParaAtualizar) {
        // Se a referência já está na lista, basta salvar.
        System.out.println("Pagamento ID " + pagamentoParaAtualizar.getId() + " para OS ID " + pagamentoParaAtualizar.getIdOrdemServico().orElse(0) + " atualizado no repositório.");
        fileHandler.save(pagamentos); // Salva a lista atualizada no JSON
    }

    /**
     * Remove um pagamento da coleção em memória pelo seu ID e persiste as alterações.
     * @param id O ID do Pagamento a ser removido.
     * @return true se o pagamento foi removido, false caso contrário.
     */
    public boolean removerPagamento(int id) {
        boolean removido = pagamentos.removeIf(pagamento -> pagamento.getId() == id);
        if (removido) {
            System.out.println("Pagamento com ID " + id + " removido do repositório.");
            fileHandler.save(pagamentos);
        } else {
            System.out.println("Pagamento com ID " + id + " não encontrado para remoção no repositório.");
        }
        return removido;
    }

    /**
     * Busca um pagamento pelo seu ID.
     * @param id O ID do Pagamento a ser buscado.
     * @return Um Optional contendo o Pagamento se encontrado, ou um Optional vazio.
     */
    public Optional<Pagamento> buscarPagamentoPorId(int id) {
        for (Pagamento p : pagamentos) {
            if (p.getId() == id) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Lista todos os pagamentos existentes no repositório.
     * @return Uma lista (cópia) de todos os pagamentos.
     */
    public List<Pagamento> listarTodosPagamentos() {
        return new ArrayList<>(pagamentos); // Retorna uma nova lista para encapsulamento
    }

    /**
     * Lista pagamentos por ID da Ordem de Serviço.
     * @param idOrdemServico ID da Ordem de Serviço.
     * @return Lista de pagamentos para a Ordem de Serviço.
     */
    public List<Pagamento> listarPagamentosPorOrdemServico(int idOrdemServico) {
        List<Pagamento> pagamentosFiltrados = new ArrayList<>();
        for (Pagamento p : pagamentos) {
            if (p.getIdOrdemServico().isPresent() && p.getIdOrdemServico().get() == idOrdemServico) {
                pagamentosFiltrados.add(p);
            }
        }
        return pagamentosFiltrados;
    }
}
