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
        // Observacoes pode ser nulo, não precisa de requireNonNull

        if (precoMaoDeObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço de mão de obra não pode ser negativo.");
        }
        if (quantidadePeca < 0) {
            throw new IllegalArgumentException("Quantidade de peça não pode ser negativa.");
        }

        // 1. Validação de existência da peça, se códigoPeca for fornecido
        if (codigoPeca != null && !codigoPeca.isEmpty()) {
            if (itemEstoqueRepository.buscarItemPorCodigo(codigoPeca).isEmpty()) {
                throw new IllegalArgumentException("Erro: Peça de estoque com código '" + codigoPeca + "' não encontrada.");
            }
        } else { // Se não tem código de peça, quantidade de peça deve ser 0
            quantidadePeca = 0;
        }

        // 2. Determinar se 'requerPrioridade' automaticamente com base no setor
        boolean requerPrioridade = (setor == SetorServico.PNEUS_RODAS); // Exemplo: apenas PNEUS_RODAS requer

        // 3. Cria e retorna a instância de Serviço
        Servico novoServico = new Servico(precoMaoDeObra, setor, codigoPeca, quantidadePeca, requerPrioridade, observacoes);
        
        // Importante: Este serviço NÃO é adicionado ao ServicoRepository aqui.
        // Ele será adicionado à OrdemServico, e a OrdemServico que será persistida.
        // Se você precisar que Servicos sejam listados 'globalmente' no ServicoRepository,
        // então teríamos um método adicionarServico(Servico) no ServicoRepository
        // e adicionaríamos aqui. Mas sua visão é que eles são específicos da OS.
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

        // Buscar a instância de Serviço para atualizar (assumindo que ela já existe em algum lugar)
        // Nota: Se Servicos só existem dentro de OSs e não no ServicoRepository, essa busca é mais complexa.
        // Para este serviço, vamos assumir que ele pode buscar no repositório de Servicos (se Servicos também fossem persistidos globalmente).
        // Por agora, este método seria chamado COM UM OBJETO Servico QUE JÁ ESTÁ NA OS.
        // Ele vai validar os dados e atualizar o objeto. A persistência da OS será feita por OrdemServicoService.
        
        Optional<Servico> servicoOpt = servicoRepository.buscarServicoPorId(id); // Busca no repositório (se Servico for globalmente persistido)
        if(servicoOpt.isEmpty()){
            System.err.println("Serviço ID " + id + " não encontrado para atualização de instância. (Ele deve estar na OS).");
            return false;
        }
        Servico servicoParaAtualizar = servicoOpt.get(); // Objeto para atualizar

        // 1. Validação de peça, se códigoPeca for fornecido
        if (novoCodigoPeca != null && !novoCodigoPeca.isEmpty()) {
            if (itemEstoqueRepository.buscarItemPorCodigo(novoCodigoPeca).isEmpty()) {
                throw new IllegalArgumentException("Erro: Nova peça de estoque com código '" + novoCodigoPeca + "' não encontrada.");
            }
        } else {
            novaQuantidadePeca = 0; // Se não tem código, quantidade é 0
        }

        // 2. Determinar se 'requerPrioridade' automaticamente
        boolean novoRequerPrioridade = (novoSetor == SetorServico.PNEUS_RODAS);

        // 3. Atualizar os atributos do objeto Servico
        servicoParaAtualizar.setPrecoMaoDeObra(novoPrecoMaoDeObra);
        servicoParaAtualizar.setSetor(novoSetor);
        servicoParaAtualizar.setCodigoPeca(novoCodigoPeca);
        servicoParaAtualizar.setQuantidadePeca(novaQuantidadePeca);
        servicoParaAtualizar.setRequerPrioridade(novoRequerPrioridade);
        servicoParaAtualizar.setObservacoes(novoObservacoes);

        // A persistência deste Servico será feita quando a OrdemServico que o contém for atualizada.
        // Se Servico for persistido globalmente, descomentar a linha abaixo.
        // servicoRepository.atualizarServico(servicoParaAtualizar); 
        System.out.println("Instância de Serviço ID " + id + " atualizada. Observações: " + servicoParaAtualizar.getObservacoes());
        return true;
    }

    // Métodos de busca e listagem (delegam para o ServicoRepository)
    public Optional<Servico> buscarServicoPorId(int id) {
        return servicoRepository.buscarServicoPorId(id);
    }

    // REMOVIDOS buscarServicoPorCodigo e buscarServicoPorNome
    // Pois Serviços são instâncias únicas e não têm códigos/nomes globais.

    public List<Servico> listarTodosServicos() {
        return servicoRepository.listarTodosServicos(); // Lista todas as instâncias de serviço já criadas e persistidas
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
