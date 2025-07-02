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

    private List<OrdemServico> ordensDeServico;
    private JsonFileHandler<OrdemServico> fileHandler;

    /**
     * Construtor do OrdemServicoRepository.
     * 
     */
    public OrdemServicoRepository() {
        Type typeOfListOfOrdens = new TypeToken<List<OrdemServico>>() {}.getType();
        this.fileHandler = new JsonFileHandler<>("ordens_servico.json", typeOfListOfOrdens);
        this.ordensDeServico = fileHandler.load();
        
        int maxIdOrdemServico = 0;
        int maxIdServico = 0;

        for (OrdemServico os : this.ordensDeServico) {
            if (os.getId() > maxIdOrdemServico) {
                maxIdOrdemServico = os.getId();
            }
            for (Servico servico : os.getServicos()) {
                if (servico.getId() > maxIdServico) {
                    maxIdServico = servico.getId();
                }
            }
        }
        OrdemServico.proximoId = maxIdOrdemServico + 1;
        Servico.proximoId = maxIdServico + 1; 
    }

    /**
     * Adiciona uma nova ordem de serviço à coleção e persiste as alterações.
     * @param ordem A OrdemServico a ser adicionada.
     */
    public void adicionarOrdemServico(OrdemServico ordem) {
        ordensDeServico.add(ordem);
        System.out.println("Ordem de Serviço '" + ordem.getCodigo() + "' (ID: " + ordem.getId() + ") adicionada.");
        fileHandler.save(ordensDeServico); 
    }

    /**
     * Atualiza uma ordem de serviço existente na coleção e persiste as alterações.
     * @param ordemParaAtualizar O objeto OrdemServico que foi modificado (referência já existente na lista).
     */
    public void atualizarOrdemServico(OrdemServico ordemParaAtualizar) {
        System.out.println("Ordem de Serviço '" + ordemParaAtualizar.getCodigo() + "' (ID: " + ordemParaAtualizar.getId() + ") atualizada.");
        fileHandler.save(ordensDeServico); 
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
     * Lista todas as ordens de serviço existentes no repositório.
     * @return Uma lista (cópia) de todas as ordens de serviço.
     */
    public List<OrdemServico> listarTodasOrdens() {
        return new ArrayList<>(ordensDeServico);
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