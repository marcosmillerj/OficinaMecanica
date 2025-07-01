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
     * Cria e retorna uma nova instância de Serviço.
     * Este serviço NÃO é adicionado automaticamente ao ServicoRepository aqui,
     * pois ele é destinado a ser adicionado a uma Ordem de Serviço específica.
     * @param precoMaoDeObra Preço da mão de obra para este serviço.
     * @param setor Setor a que pertence o serviço.
     * @param codigoPeca Código da peça de estoque associada (nulo/vazio se não houver).
     * @param quantidadePeca Quantidade da peça usada (0 se não houver).
     * @param observacoes Observações para este serviço.
     * @return O objeto Servico criado.
     * @throws IllegalArgumentException Se dados de entrada forem inválidos ou peça não encontrada.
     */
    public Servico criarInstanciaServico(BigDecimal precoMaoDeObra, SetorServico setor,
                                         String codigoPeca, int quantidadePeca, String observacoes)
                                         throws IllegalArgumentException {
        Objects.requireNonNull(precoMaoDeObra, "Preço de mão de obra não pode ser nulo.");
        Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo.");

        if (precoMaoDeObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço de mão de obra não pode ser negativo.");
        }
        if (quantidadePeca < 0) {
            throw new IllegalArgumentException("Quantidade de peça não pode ser negativa.");
        }

        if (codigoPeca != null && !codigoPeca.isEmpty()) {
            if (itemEstoqueRepository.buscarItemPorCodigo(codigoPeca).isEmpty()) {
                throw new IllegalArgumentException("Erro: Peça de estoque com código '" + codigoPeca + "' não encontrada.");
            }
        } else {
            quantidadePeca = 0;
        }

        boolean requerPrioridade = (setor == SetorServico.PNEUS_RODAS);

        Servico novoServico = new Servico(precoMaoDeObra, setor, codigoPeca, quantidadePeca, requerPrioridade, observacoes);
        System.out.println("Instância de Serviço ID " + novoServico.getId() + " criada para OS. Observações: " + novoServico.getObservacoes());
        return novoServico;
    }

    /**
     * Atualiza os dados de uma instância de Serviço existente.
     * @param id ID do serviço a ser atualizado.
     * @param novoPrecoMaoDeObra Novo preço de mão de obra.
     * @param novoSetor Novo setor.
     * @param novoCodigoPeca Novo código da peça.
     * @param novaQuantidadePeca Nova quantidade da peça.
     * @param novoObservacoes Novas observações.
     * @return true se atualizado, false se não encontrado.
     * @throws IllegalArgumentException Se dados de entrada forem inválidos ou peça não encontrada.
     */
    public boolean atualizarInstanciaServico(int id, BigDecimal novoPrecoMaoDeObra, SetorServico novoSetor,
                                             String novoCodigoPeca, int novaQuantidadePeca, String novoObservacoes)
                                             throws IllegalArgumentException {
        Objects.requireNonNull(novoPrecoMaoDeObra, "Novo preço de mão de obra não pode ser nulo.");
        Objects.requireNonNull(novoSetor, "Novo setor do serviço não pode ser nulo.");
        
        Optional<Servico> servicoOpt = servicoRepository.buscarServicoPorId(id);
        if(servicoOpt.isEmpty()){
            System.err.println("Serviço ID " + id + " não encontrado para atualização de instância. (Ele deve estar na OS).");
            return false;
        }
        Servico servicoParaAtualizar = servicoOpt.get();

        if (novoCodigoPeca != null && !novoCodigoPeca.isEmpty()) {
            if (itemEstoqueRepository.buscarItemPorCodigo(novoCodigoPeca).isEmpty()) {
                throw new IllegalArgumentException("Erro: Nova peça de estoque com código '" + novoCodigoPeca + "' não encontrada.");
            }
        } else {
            novaQuantidadePeca = 0;
        }

        boolean novoRequerPrioridade = (novoSetor == SetorServico.PNEUS_RODAS);

        servicoParaAtualizar.setPrecoMaoDeObra(novoPrecoMaoDeObra);
        servicoParaAtualizar.setSetor(novoSetor);
        servicoParaAtualizar.setCodigoPeca(novoCodigoPeca);
        servicoParaAtualizar.setQuantidadePeca(novaQuantidadePeca);
        servicoParaAtualizar.setRequerPrioridade(novoRequerPrioridade);
        servicoParaAtualizar.setObservacoes(novoObservacoes);

        System.out.println("Instância de Serviço ID " + id + " atualizada. Observações: " + servicoParaAtualizar.getObservacoes());
        return true;
    }

    public Optional<Servico> buscarServicoPorId(int id) {
        return servicoRepository.buscarServicoPorId(id);
    }

    public List<Servico> listarTodosServicos() {
        return servicoRepository.listarTodosServicos();
    }

    public boolean removerServico(int id) {
        return servicoRepository.removerServico(id);
    }
    
    /**
     * Calcula o custo total de um serviço, incluindo o preço de mão de obra e o custo das peças.
     * @param servico O objeto Servico.
     * @return O custo total do serviço.
     * @throws IllegalArgumentException Se a peça não for encontrada no estoque.
     */
    public BigDecimal calcularCustoTotalServico(Servico servico) throws IllegalArgumentException {
        Objects.requireNonNull(servico, "Serviço não pode ser nulo.");
        BigDecimal custoTotal = servico.getPrecoMaoDeObra();

        if (servico.getCodigoPeca() != null && !servico.getCodigoPeca().isEmpty() && servico.getQuantidadePeca() > 0) {
            Optional<ItemEstoque> pecaOpt = itemEstoqueRepository.buscarItemPorCodigo(servico.getCodigoPeca());
            if (pecaOpt.isEmpty()) {
                throw new IllegalArgumentException("Peça com código '" + servico.getCodigoPeca() + "' não encontrada no estoque para calcular o custo.");
            }
            BigDecimal precoPecaUnitario = pecaOpt.get().getPrecoUnitario();
            BigDecimal custoPecas = precoPecaUnitario.multiply(new BigDecimal(servico.getQuantidadePeca()));
            custoTotal = custoTotal.add(custoPecas);
        }
        return custoTotal;
    }
}
