/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import models.Cliente;
import models.OrdemServico;
import models.Relatorio;
import models.Usuario;
import models.Veiculo;
import models.enums.StatusOrdem;
import models.enums.TipoRelatorio;
import repository.AgendamentoRepository;
import repository.ClienteRepository;
import repository.ItemEstoqueRepository;
import repository.OrdemServicoRepository;
import repository.PagamentoRepository;
import repository.RelatorioRepository;
import repository.UsuarioCRUD;
import repository.VeiculoRepository;

/**
 *
 * @author marcos_miller
 */
public class RelatorioService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;
    private final PagamentoRepository pagamentoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UsuarioCRUD usuarioCRUD;
    private final ServicoService servicoService;
    private final RelatorioRepository relatorioRepository;

    private final OrdemServicoService ordemServicoService;

    // Formato padrão para datas em relatórios
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // <<--- NOVO AQUI!

    /**
     * Construtor do RelatorioService.
     * Recebe os repositórios e serviços necessários para acessar e processar os dados.
     */
    public RelatorioService(OrdemServicoRepository ordemServicoRepository,
                            ItemEstoqueRepository itemEstoqueRepository,
                            PagamentoRepository pagamentoRepository,
                            AgendamentoRepository agendamentoRepository,
                            ClienteRepository clienteRepository,
                            VeiculoRepository veiculoRepository,
                            UsuarioCRUD usuarioCRUD,
                            ServicoService servicoService,
                            RelatorioRepository relatorioRepository,
                            OrdemServicoService ordemServicoService) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository, "OrdemServicoRepository não pode ser nulo.");
        this.itemEstoqueRepository = Objects.requireNonNull(itemEstoqueRepository, "ItemEstoqueRepository não pode ser nulo.");
        this.pagamentoRepository = Objects.requireNonNull(pagamentoRepository, "PagamentoRepository não pode ser nulo.");
        this.agendamentoRepository = Objects.requireNonNull(agendamentoRepository, "AgendamentoRepository não pode ser nulo.");
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository não pode ser nulo.");
        this.veiculoRepository = Objects.requireNonNull(veiculoRepository, "VeiculoRepository não pode ser nulo.");
        this.usuarioCRUD = Objects.requireNonNull(usuarioCRUD, "UsuarioCRUD não pode ser nulo.");
        this.servicoService = Objects.requireNonNull(servicoService, "ServicoService não pode ser nulo.");
        this.relatorioRepository = Objects.requireNonNull(relatorioRepository, "RelatorioRepository não pode ser nulo.");
        this.ordemServicoService = Objects.requireNonNull(ordemServicoService, "OrdemServicoService não pode ser nulo.");
    }

    /**
     * Gera e salva um relatório diário de Ordens de Serviço.
     * @param usuarioGeradorId ID do usuário que está gerando o relatório.
     * @return O objeto Relatorio salvo.
     */
    public Relatorio gerarESalvarRelatorioDiario(int usuarioGeradorId) {
        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime inicioDoDia = hoje.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimDoDia = hoje.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        String titulo = "Relatório Diário de Ordens de Serviço - " + hoje.format(DATE_FORMATTER);
        List<String> conteudoLista = gerarRelatorioOrdensServicoConteudo(inicioDoDia, fimDoDia, null);
        String conteudoTexto = String.join("\n", conteudoLista);

        Relatorio relatorio = new Relatorio(
            TipoRelatorio.DIARIO,
            LocalDateTime.now(),
            inicioDoDia,
            fimDoDia,
            titulo,
            conteudoTexto,
            usuarioGeradorId
        );
        relatorioRepository.adicionarRelatorio(relatorio);
        return relatorio;
    }

    /**
     * Gera e salva um relatório mensal de Ordens de Serviço.
     * @param usuarioGeradorId ID do usuário que está gerando o relatório.
     * @return O objeto Relatorio salvo.
     */
    public Relatorio gerarESalvarRelatorioMensal(int usuarioGeradorId) {
        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime inicioDoMes = hoje.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimDoMes = hoje.withDayOfMonth(hoje.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        String titulo = "Relatório Mensal de Ordens de Serviço - " + hoje.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault()) + "/" + hoje.getYear();
        List<String> conteudoLista = gerarRelatorioOrdensServicoConteudo(inicioDoMes, fimDoMes, null);
        String conteudoTexto = String.join("\n", conteudoLista);

        Relatorio relatorio = new Relatorio(
            TipoRelatorio.MENSAL,
            LocalDateTime.now(),
            inicioDoMes,
            fimDoMes,
            titulo,
            conteudoTexto,
            usuarioGeradorId
        ); // <<--- AGORA CHAMA O CONSTRUTOR SEM ID NOVO RELATORIO
        relatorioRepository.adicionarRelatorio(relatorio);
        return relatorio;
    }


    /**
     * Gera o CONTEÚDO de um relatório de Ordens de Serviço, filtrando por período e/ou status.
     * Este método é auxiliar e não salva o relatório.
     * @param dataInicio Data e hora de início para o filtro de criação da OS.
     * @param dataFim Data e hora de fim para o filtro de criação da OS.
     * @param status Status da OS para filtrar.
     * @return Uma lista de strings formatada representando o conteúdo das Ordens de Serviço.
     */
    private List<String> gerarRelatorioOrdensServicoConteudo(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            StatusOrdem status) {

        List<OrdemServico> todasOS = ordemServicoRepository.listarTodasOrdens();

        List<OrdemServico> filtradas = todasOS.stream()
                .filter(os -> {
                    boolean filtroData = true;
                    if (dataInicio != null && os.getDataAbertura().isBefore(dataInicio)) {
                        filtroData = false;
                    }
                    if (dataFim != null && os.getDataAbertura().isAfter(dataFim)) {
                        filtroData = false;
                    }
                    return filtroData;
                })
                .filter(os -> {
                    if (status != null) {
                        return os.getStatus() == status;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        List<String> conteudoRelatorio = new ArrayList<>();
        conteudoRelatorio.add("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        conteudoRelatorio.add(String.format("%-5s | %-15s | %-25s | %-20s | %-20s | %-20s | %-20s%n",
                                  "ID", "CÓDIGO", "CLIENTE", "VEÍCULO (PLACA)", "MECÂNICO", "STATUS", "PREÇO TOTAL"));
        conteudoRelatorio.add("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        if (filtradas.isEmpty()) {
            conteudoRelatorio.add("Nenhuma Ordem de Serviço encontrada com os critérios fornecidos.");
        } else {
            for (OrdemServico os : filtradas) {
                String nomeCliente = clienteRepository.buscarClientePorId(os.getIdCliente()).map(Cliente::getNome).orElse("Desconhecido");
                String placaVeiculo = veiculoRepository.buscarVeiculoPorId(os.getIdVeiculo()).map(Veiculo::getPlaca).orElse("Desconhecida");
                String nomeMecanico = usuarioCRUD.buscarUsuarioPorIdOptional(os.getIdMecanicoResponsavel()).map(Usuario::getNome).orElse("Desconhecido");
                
                BigDecimal precoTotalFinal = BigDecimal.ZERO;
                try {
                    precoTotalFinal = ordemServicoService.calcularPrecoTotalFinalOS(os);
                } catch (IllegalArgumentException e) {
                    precoTotalFinal = new BigDecimal("-1.00"); // Indica erro no cálculo
                }

                conteudoRelatorio.add(String.format("%-5d | %-15s | %-25s | %-20s | %-20s | %-20s | R$ %-17.2f",
                                  os.getId(),
                                  os.getCodigo(),
                                  nomeCliente,
                                  placaVeiculo,
                                  nomeMecanico,
                                  os.getStatus().getDescricao(),
                                  precoTotalFinal));
            }
        }
        conteudoRelatorio.add("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        return conteudoRelatorio;
    }

    /**
     * Lista todos os relatórios salvos no sistema.
     * @return Lista de objetos Relatorio.
     */
    public List<Relatorio> listarTodosRelatorios() {
        return relatorioRepository.listarTodosRelatorios();
    }

    /**
     * Busca um relatório salvo pelo ID.
     * @param idRelatorio ID do relatório.
     * @return Optional contendo o Relatorio, ou vazio.
     */
    public Optional<Relatorio> buscarRelatorioPorId(int idRelatorio) {
        return relatorioRepository.buscarRelatorioPorId(idRelatorio);
    }
}