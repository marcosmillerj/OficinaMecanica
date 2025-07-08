/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aPrincipal;

import comparator.ClienteComparatorPorEmail;
import comparator.ClienteComparatorPorNome;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Cliente;
import models.enums.TipoUsuario;
import repository.*;
import service.*;

/**
 *
 * @author marcos_miller
 */
public class PrincipalTestes {

    private static UsuarioCRUD usuarioCRUD;
    private static ClienteRepository clienteRepository;
    private static VeiculoRepository veiculoRepository;
    private static ItemEstoqueRepository itemEstoqueRepository;
    private static ServicoRepository servicoRepository;
    private static OrdemServicoRepository ordemServicoRepository;
    private static PagamentoRepository pagamentoRepository;
    private static AgendamentoRepository agendamentoRepository;
    private static ElevadorRepository elevadorRepository;
    private static RelatorioRepository relatorioRepository;
    private static Scanner scanner;

    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static ItemEstoqueService itemEstoqueService;
    private static ServicoService servicoService;
    private static OrdemServicoService ordemServicoService;
    private static PagamentoService pagamentoService;
    private static AgendamentoService agendamentoService;
    private static ElevadorService elevadorService;
    private static RelatorioService relatorioService;


    /**
     * Método principal para iniciar e executar todas as demonstrações.
     * Este método será chamado do main da classe Principal.
     * @param args Argumentos de linha de comando.
     * @param scannerInstancia Instância do Scanner da classe Principal.
     */
    public static void rodarTodasDemonstracoes(Scanner scannerInstancia) {
        System.out.println("\n--- INICIANDO DEMONSTRAÇÕES DO PROJETO FINAL ---");

        // Inicializa o Scanner (usando o da Principal)
        scanner = scannerInstancia;

        // --- INICIALIZAÇÃO DE REPOSITÓRIOS ---
        usuarioCRUD = new UsuarioCRUD();
        clienteRepository = new ClienteRepository();
        veiculoRepository = new VeiculoRepository();
        itemEstoqueRepository = new ItemEstoqueRepository();
        servicoRepository = new ServicoRepository();
        ordemServicoRepository = new OrdemServicoRepository();
        pagamentoRepository = new PagamentoRepository();
        agendamentoRepository = new AgendamentoRepository();
        elevadorRepository = ElevadorRepository.getInstance();
        relatorioRepository = new RelatorioRepository();

        //--- INICIALIZAÇÃO DOS SERVICOS ---
        usuarioService = new UsuarioService(usuarioCRUD);
        clienteService = new ClienteService(clienteRepository, veiculoRepository);
        veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
        itemEstoqueService = new ItemEstoqueService(itemEstoqueRepository);
        servicoService = new ServicoService(servicoRepository, itemEstoqueRepository);
        elevadorService = new ElevadorService(elevadorRepository, veiculoRepository);
        ordemServicoService = new OrdemServicoService(
            ordemServicoRepository, usuarioCRUD, clienteService, veiculoService, servicoService, elevadorService
        );
        pagamentoService = new PagamentoService(pagamentoRepository, ordemServicoService);
        agendamentoService = new AgendamentoService(agendamentoRepository, clienteRepository, veiculoRepository);
        relatorioService = new RelatorioService(
            ordemServicoRepository, itemEstoqueRepository, pagamentoRepository, agendamentoRepository,
            clienteRepository, veiculoRepository, usuarioCRUD, servicoService, relatorioRepository,
            ordemServicoService
        );

        
        demonstrarQuestao15(clienteService);
        demonstrarQuestao16(clienteService);
        demonstrarQuestao17(clienteService, scanner);
        

        System.out.println("\n--- FIM DE TODAS AS DEMONSTRAÇÕES DO PROJETO FINAL ---\n");
    }

    
    /**
     * Método para demonstrar o uso de Iterator e o loop for-each (Questão 15).
     * @param clienteService O serviço de clientes para obter a lista.
     */
    private static void demonstrarQuestao15(ClienteService clienteService) {
        System.out.println("\n--- DEMONSTRAÇÃO - QUESTÃO 15: ITERATOR E FOR-EACH ---");

        List<Cliente> clientes = clienteService.listarTodosClientes();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado para demonstração da Questão 15.");
            return;
        }

        System.out.println("\n--- Usando Iterator (while(iterator.hasNext()) ---");
        // 1. Instanciar um Iterator
        Iterator<Cliente> iterator = clientes.iterator();
        int countIterator = 0;
        while (iterator.hasNext()) {
            Cliente cliente = iterator.next();
            System.out.println("Cliente (Iterator): " + cliente.getNome() + " (ID: " + cliente.getId() + ")");
            countIterator++;
        }
        System.out.println("Total de clientes percorridos com Iterator: " + countIterator);

        System.out.println("\n--- Explicação da relação entre Iterator e for-each ---");
        System.out.println("O Iterator é uma interface fundamental em Java para percorrer coleções. Ele fornece métodos como `hasNext()` para verificar se há mais elementos e `next()` para obter o próximo elemento. É a forma 'manual' de iteração e o mecanismo subjacente.");
        System.out.println("\nO loop 'for-each' (também conhecido como enhanced for loop) introduzido no Java 5 é uma sintaxe mais concisa e legível para percorrer coleções e arrays. Por baixo dos panos, para coleções que implementam a interface `Iterable` (como `ArrayList`), o compilador Java transforma o for-each em código que utiliza um `Iterator`.");
        System.out.println("Isso significa que o for-each é essencialmente um 'açúcar sintático' para a iteração com Iterator, tornando o código mais limpo e menos propenso a erros de índice, encapsulando a complexidade do Iterator.");

        System.out.println("\n--- Testando o loop for-each ---");
        int countForEach = 0;
        for (Cliente cliente : clientes) {
            System.out.println("Cliente (for-each): " + cliente.getNome() + " (ID: " + cliente.getId() + ")");
            countForEach++;
        }
        System.out.println("Total de clientes percorridos com for-each: " + countForEach);

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO - QUESTÃO 15 ---\n");
    }

    /**
     * Demonstra o uso de Comparator e Collections.sort() (Questão 16).
     * @param clienteService O serviço de clientes para obter a lista.
     */
    private static void demonstrarQuestao16(ClienteService clienteService) {
        System.out.println("\n--- DEMONSTRAÇÃO - QUESTÃO 16: COMPARATOR E COLLECTIONS.SORT() ---");

        List<Cliente> clientesOriginais = clienteService.listarTodosClientes();
        if (clientesOriginais.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado para demonstração da Questão 16.");
            return;
        }

        System.out.println("\n--- Clientes Originais (sem ordenação garantida) ---");
        clientesOriginais.forEach(System.out::println);

        // Clona a lista para não modificar a original ao ordenar
        List<Cliente> clientesOrdenadosNome = new ArrayList<>(clientesOriginais);
        List<Cliente> clientesOrdenadosEmail = new ArrayList<>(clientesOriginais);

        System.out.println("\n--- Ordenando Clientes por Nome (usando ClienteComparatorByName) ---");
        Collections.sort(clientesOrdenadosNome, new ClienteComparatorPorNome());
        clientesOrdenadosNome.forEach(System.out::println);

        System.out.println("\n--- Ordenando Clientes por Email (usando ClienteComparatorByEmail) ---");
        Collections.sort(clientesOrdenadosEmail, new ClienteComparatorPorEmail());
        clientesOrdenadosEmail.forEach(System.out::println);

        System.out.println("\n--- Explicação ---");
        System.out.println("A interface `Comparator` (padrão Strategy) permite definir múltiplas estratégias de ordenação para uma mesma classe (`Cliente`), sem modificar a classe `Cliente` em si.");
        System.out.println("`Collections.sort()` é um método estático da classe `java.util.Collections` que aceita uma `List` e um `Comparator`. Ele utiliza o `Comparator` fornecido para saber como comparar os elementos da lista e, assim, ordená-la.");
        System.out.println("Isso proporciona flexibilidade: podemos ordenar a mesma lista de clientes por diferentes critérios (nome, email, telefone, etc.) apenas passando um `Comparator` diferente.");

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO - QUESTÃO 16 ---\n");
    }

    /**
     * Demonstra um método find utilizando Iterator e Comparator,
     * e compara com Collections.binarySearch() (Questão 17).
     * @param clienteService O serviço de clientes.
     * @param scannerInstancia Scanner para input do usuário na demonstração.
     */
    private static void demonstrarQuestao17(ClienteService clienteService, Scanner scannerInstancia) {
        System.out.println("\n--- DEMONSTRAÇÃO - QUESTÃO 17: FIND COM ITERATOR/COMPARATOR E BINARYSEARCH ---");

        List<Cliente> clientes = clienteService.listarTodosClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado para demonstração da Questão 17.");
            return;
        }

        // --- Implementação de um método 'find' personalizado usando Iterator e Comparator ---
        System.out.println("\n--- Método 'findClientePorNome' personalizado (usando Iterator e Comparator) ---");
        // Para usar o Comparator, primeiro ordenamos a lista.
        List<Cliente> clientesOrdenadosPorNome = clienteService.listarClientesOrdenados(new ClienteComparatorPorNome());
        System.out.println("Lista de clientes ordenada por nome (necessário para binarySearch):");
        clientesOrdenadosPorNome.forEach(c -> System.out.println("- " + c.getNome() + " | " + c.getEmail()));

        System.out.print("Digite o nome do cliente a buscar (ex: Ana Beatriz): ");
        String nomeBusca = scannerInstancia.nextLine();
        Optional<Cliente> encontrado = findClientePorNome(clientesOrdenadosPorNome, nomeBusca);
        if (encontrado.isPresent()) {
            System.out.println("Cliente '" + nomeBusca + "' encontrado com 'findClientePorNome': " + encontrado.get());
        } else {
            System.out.println("Cliente '" + nomeBusca + "' NÃO encontrado com 'findClientePorNome'.");
        }

        System.out.println("\n--- Comparação com Collections.binarySearch() ---");
        System.out.println("`Collections.binarySearch()` é um método eficiente para buscar elementos em listas JÁ ORDENADAS. Ele usa um algoritmo de busca binária, que é muito mais rápido que a busca linear (iterar elemento por elemento) para grandes listas.");
        System.out.println("Ele exige que a lista esteja ordenada e que seja fornecido um `Comparator` (ou que os elementos sejam `Comparable`).");

        // O ComparatorByName compara o nome de um Cliente.
        System.out.print("Digite o nome do cliente a buscar com binarySearch (ex: Zeca Urubu): ");
        String nomeBuscaBinary = scannerInstancia.nextLine();
        Cliente clienteParaBuscar = new Cliente(nomeBuscaBinary, "", "");

        // Usando binarySearch com o mesmo Comparator usado para ordenar
        int indice = Collections.binarySearch(clientesOrdenadosPorNome, clienteParaBuscar, new ClienteComparatorPorNome());
        if (indice >= 0) {
            System.out.println("Cliente '" + clienteParaBuscar.getNome() + "' encontrado com binarySearch no índice: " + indice);
            System.out.println("Cliente encontrado: " + clientesOrdenadosPorNome.get(indice));
        } else {
            System.out.println("Cliente '" + clienteParaBuscar.getNome() + "' NÃO encontrado com binarySearch. Retornou índice negativo: " + indice);
        }

        System.out.println("\n--- Explicação da Aplicação ---");
        System.out.println("Nosso método `findClientePorNome` simula uma busca linear iterativa para fins didáticos (mostrando o uso do Iterator e Comparator juntos). O `Collections.binarySearch` é a abordagem performática para listas grandes e ordenadas, utilizando o mesmo conceito de `Comparator` para o critério de busca.");

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO - QUESTÃO 17 ---\n");
    }

    /**
     * Método auxiliar personalizado para buscar um cliente por nome em uma lista ordenada.
     * Utiliza Iterator e Comparator (para fins de demonstração da Questão 17).
     * Nota: Para produção, Collections.binarySearch() ou um método de busca no Service seria preferível.
     * @param clientes A lista de clientes (DEVE ESTAR ORDENADA PELO NOME).
     * @param nome O nome do cliente a ser buscado.
     * @return Um Optional contendo o Cliente se encontrado, ou um Optional vazio.
     */
    private static Optional<Cliente> findClientePorNome(List<Cliente> clientes, String nome) {
        Comparator<Cliente> comparator = new ClienteComparatorPorNome();
        Iterator<Cliente> iterator = clientes.iterator();

        Cliente clienteBusca = new Cliente(nome, "", "");

        while (iterator.hasNext()) {
            Cliente clienteAtual = iterator.next();
            if (comparator.compare(clienteAtual, clienteBusca) == 0) {
                return Optional.of(clienteAtual);
            }
        }
        return Optional.empty();
    }
}