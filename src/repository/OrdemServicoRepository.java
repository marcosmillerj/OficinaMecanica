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
import models.OrdemServico;
import models.Servico;
import util.JsonFileHandler;

/**
 *
 * @author marcos_miller
 */
public class OrdemServicoRepository {

    private List<OrdemServico> ordensDeServico; // A lista de todas as ordens de serviço
    private JsonFileHandler<OrdemServico> fileHandler; // O handler para salvar/carregar JSON

    /**
     * Construtor do OrdemServicoRepository.
     * Ao ser instanciado, tenta carregar as ordens de serviço do arquivo JSON.
     */
    public OrdemServicoRepository() {
        // Define o tipo para o JsonFileHandler: uma Lista de OrdemServico
        Type typeOfListOfOrdens = new TypeToken<List<OrdemServico>>() {}.getType();
        // Inicializa o fileHandler com o nome do arquivo específico para ordens de serviço
        this.fileHandler = new JsonFileHandler<>("ordens_servico.json", typeOfListOfOrdens);

        // Carrega as ordens de serviço ao iniciar o repositório
        this.ordensDeServico = fileHandler.load();

        // CRÍTICO: Ajustar o próximo ID para OrdemServico E Servico após o carregamento
        int maxIdOrdemServico = 0;
        int maxIdServico = 0; // <<--- NOVO: Para rastrear o maior ID de Servico

        for (OrdemServico os : this.ordensDeServico) {
            if (os.getId() > maxIdOrdemServico) {
                maxIdOrdemServico = os.getId();
            }
            // NOVO: Percorrer os serviços de cada OS para encontrar o maior ID de Servico
            for (Servico servico : os.getServicos()) {
                if (servico.getId() > maxIdServico) {
                    maxIdServico = servico.getId();
                }
            }
        }
        OrdemServico.proximoId = maxIdOrdemServico + 1; // Ajusta o contador estático da OrdemServico
        Servico.proximoId = maxIdServico + 1; // <<--- NOVO: Ajusta o contador estático da Servico!

        System.out.println("Contador de ID de OrdemServico ajustado para: " + OrdemServico.proximoId);
        System.out.println("Contador de ID de Servico ajustado para: " + Servico.proximoId); // MENSAGEM ADICIONAL
    }

    /**
     * Adiciona uma nova ordem de serviço à coleção e persiste as alterações.
     * @param ordem A OrdemServico a ser adicionada.
     */
    public void adicionarOrdemServico(OrdemServico ordem) {
        ordensDeServico.add(ordem);
        System.out.println("Ordem de Serviço '" + ordem.getCodigo() + "' (ID: " + ordem.getId() + ") adicionada.");
        fileHandler.save(ordensDeServico); // Salva a lista atualizada no JSON
    }

    /**
     * Atualiza uma ordem de serviço existente na coleção e persiste as alterações.
     * @param ordemParaAtualizar O objeto OrdemServico que foi modificado (referência já existente na lista).
     */
    public void atualizarOrdemServico(OrdemServico ordemParaAtualizar) {
        System.out.println("Ordem de Serviço '" + ordemParaAtualizar.getCodigo() + "' (ID: " + ordemParaAtualizar.getId() + ") atualizada.");
        fileHandler.save(ordensDeServico); // Salva a lista atualizada no JSON
    }
    
    /**
     * Remove uma ordem de serviço da coleção pelo seu ID e persiste as alterações.
     * @param id O ID da Ordem de Serviço a ser removida.
     * @return true se a ordem foi removida, false caso contrário.
     */
    public boolean removerOrdemServico(int id) {
        boolean removido = ordensDeServico.removeIf(os -> os.getId() == id);
        if (removido) {
            System.out.println("Ordem de Serviço com ID " + id + " removida com sucesso.");
            fileHandler.save(ordensDeServico);
        } else {
            System.out.println("Ordem de Serviço com ID " + id + " não encontrada para remoção.");
        }
        return removido;
    }

    /**
     * Busca uma ordem de serviço pelo seu ID.
     * @param id O ID da Ordem de Serviço a ser buscada.
     * @return Um Optional contendo a Ordem de Serviço se encontrada, ou um Optional vazio.
     */
    public Optional<OrdemServico> buscarOrdemServicoPorId(int id) {
        for (OrdemServico os : ordensDeServico) {
            if (os.getId() == id) {
                return Optional.of(os);
            }
        }
        return Optional.empty();
    }

    /**
     * Busca uma ordem de serviço pelo seu código.
     * @param codigo O código da Ordem de Serviço a ser buscada.
     * @return Um Optional contendo a Ordem de Serviço se encontrada, ou um Optional vazio.
     */
    public Optional<OrdemServico> buscarOrdemServicoPorCodigo(String codigo) {
        for (OrdemServico os : ordensDeServico) {
            if (os.getCodigo().equalsIgnoreCase(codigo)) {
                return Optional.of(os);
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todas as ordens de serviço existentes no repositório.
     * @return Uma lista (cópia) de todas as ordens de serviço.
     */
    public List<OrdemServico> listarTodasOrdens() {
        return new ArrayList<>(ordensDeServico); // Retorna uma nova lista para encapsulamento
    }
    
    /**
     * Lista ordens de serviço por ID do cliente.
     * @param idCliente ID do cliente.
     * @return Lista de ordens de serviço para o cliente.
     */
    public List<OrdemServico> listarOrdensPorCliente(int idCliente) {
        List<OrdemServico> ordensFiltradas = new ArrayList<>();
        for (OrdemServico os : ordensDeServico) {
            if (os.getIdCliente() == idCliente) {
                ordensFiltradas.add(os);
            }
        }
        return ordensFiltradas;
    }

    /**
     * Lista ordens de serviço por ID do mecânico responsável.
     * @param idMecanico ID do mecânico.
     * @return Lista de ordens de serviço atribuídas ao mecânico.
     */
    public List<OrdemServico> listarOrdensPorMecanico(int idMecanico) {
        List<OrdemServico> ordensFiltradas = new ArrayList<>();
        for (OrdemServico os : ordensDeServico) {
            if (os.getIdMecanicoResponsavel() == idMecanico) {
                ordensFiltradas.add(os);
            }
        }
        return ordensFiltradas;
    }
}