/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.Servico;
import models.enums.SetorServico;
import repository.ItemEstoqueRepository;
import repository.ServicoRepository;

/**
 *
 * @author marcos_miller
 */
public class ServicoService {

    private ServicoRepository servicoRepository;
    private ItemEstoqueRepository itemEstoqueRepository;

    public ServicoService(ServicoRepository servicoRepository, ItemEstoqueRepository itemEstoqueRepository) {
        this.servicoRepository = servicoRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
    }

    /**
     * Adiciona um novo serviço ao sistema.
     * @param codigo Código único do serviço.
     * @param descricao Descrição do serviço.
     * @param preco Preço do serviço.
     * @param setor Setor a que pertence o serviço.
     * @param idItemEstoquePeca ID da peça de estoque associada (0 se não houver).
     * @param requerElevadorAlinhamento Indica se o serviço requer elevador de alinhamento.
     * @return O Servico criado.
     * @throws IllegalStateException Se um serviço com o mesmo código já existe.
     * @throws IllegalArgumentException Se dados de entrada forem inválidos ou peça não encontrada.
     */
    public Servico adicionarServico(String codigo, String descricao, BigDecimal preco,
                                    SetorServico setor, int idItemEstoquePeca, boolean requerElevadorAlinhamento)
                                    throws IllegalStateException, IllegalArgumentException {
        Objects.requireNonNull(codigo, "Código do serviço não pode ser nulo.");
        Objects.requireNonNull(descricao, "Descrição do serviço não pode ser nula.");
        Objects.requireNonNull(preco, "Preço do serviço não pode ser nulo.");
        Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo.");

        if (servicoRepository.buscarServicoPorCodigo(codigo).isPresent()) {
            throw new IllegalStateException("Erro: Serviço com código '" + codigo + "' já cadastrado.");
        }

        // Valida se a peça existe, se um idItemEstoquePeca > 0 foi fornecido
        if (idItemEstoquePeca > 0) {
            if (itemEstoqueRepository.buscarItemPorId(idItemEstoquePeca).isEmpty()) {
                throw new IllegalArgumentException("Erro: Peça de estoque com ID " + idItemEstoquePeca + " não encontrada para associar ao serviço.");
            }
        }

        // Validação de preço negativo é feita no construtor do Servico.
        Servico novoServico = new Servico(codigo, descricao, preco, setor, idItemEstoquePeca, requerElevadorAlinhamento);
        servicoRepository.adicionarServico(novoServico); // Delega ao repositório
        return novoServico;
    }

    /**
     * Atualiza os dados de um serviço existente.
     * @param id ID do serviço.
     * @param novoCodigo Novo código.
     * @param novaDescricao Nova descrição.
     * @param novoPreco Novo preço.
     * @param novoSetor Novo setor.
     * @param novoIdItemEstoquePeca Novo ID da peça de estoque.
     * @param novoRequerElevadorAlinhamento Novo status de requerimento de elevador.
     * @return true se atualizado, false se não encontrado.
     * @throws IllegalStateException Se o novo código já pertencer a outro serviço.
     * @throws IllegalArgumentException Se dados de entrada forem inválidos ou peça não encontrada.
     */
    public boolean atualizarServico(int id, String novoCodigo, String novaDescricao, BigDecimal novoPreco,
                                    SetorServico novoSetor, int novoIdItemEstoquePeca, boolean novoRequerElevadorAlinhamento)
                                    throws IllegalStateException, IllegalArgumentException {
        Objects.requireNonNull(novoCodigo, "Novo código do serviço não pode ser nulo.");
        Objects.requireNonNull(novaDescricao, "Nova descrição do serviço não pode ser nula.");
        Objects.requireNonNull(novoPreco, "Novo preço do serviço não pode ser nulo.");
        Objects.requireNonNull(novoSetor, "Novo setor do serviço não pode ser nulo.");

        Optional<Servico> servicoOpt = servicoRepository.buscarServicoPorId(id);
        if (servicoOpt.isEmpty()) {
            return false; // Serviço não encontrado
        }
        Servico servicoParaAtualizar = servicoOpt.get();

        // Validação de unicidade de código se for alterado e pertencer a outro serviço
        if (!servicoParaAtualizar.getCodigo().equalsIgnoreCase(novoCodigo)) {
            Optional<Servico> existentePorCodigo = servicoRepository.buscarServicoPorCodigo(novoCodigo);
            if (existentePorCodigo.isPresent() && existentePorCodigo.get().getId() != id) {
                throw new IllegalStateException("Erro: Novo código '" + novoCodigo + "' já cadastrado para outro serviço.");
            }
        }
        
        // Valida se a nova peça existe, se um idItemEstoquePeca > 0 foi fornecido
        if (novoIdItemEstoquePeca > 0) {
            if (itemEstoqueRepository.buscarItemPorId(novoIdItemEstoquePeca).isEmpty()) {
                throw new IllegalArgumentException("Erro: Nova peça de estoque com ID " + novoIdItemEstoquePeca + " não encontrada para associar ao serviço.");
            }
        }

        servicoParaAtualizar.setCodigo(novoCodigo);
        servicoParaAtualizar.setDescricao(novaDescricao);
        servicoParaAtualizar.setPreco(novoPreco);
        servicoParaAtualizar.setSetor(novoSetor);
        servicoParaAtualizar.setIdItemEstoquePeca(novoIdItemEstoquePeca);
        servicoParaAtualizar.setRequerElevadorAlinhamento(novoRequerElevadorAlinhamento);

        servicoRepository.atualizarServico(servicoParaAtualizar); // Delega ao repositório
        return true;
    }

    // Métodos de busca e listagem
    public Optional<Servico> buscarServicoPorId(int id) {
        return servicoRepository.buscarServicoPorId(id);
    }

    public Optional<Servico> buscarServicoPorCodigo(String codigo) {
        return servicoRepository.buscarServicoPorCodigo(codigo);
    }

    public List<Servico> listarTodosServicos() {
        return servicoRepository.listarTodosServicos();
    }

    public boolean removerServico(int id) {
        return servicoRepository.removerServico(id);
    }
}
