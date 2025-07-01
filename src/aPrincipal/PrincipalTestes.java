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
 * Esta classe, `PrincipalTestes`, serve como um módulo de demonstração e teste
 * para as funcionalidades desenvolvidas no sistema de gerenciamento da oficina.
 * Ela é responsável por inicializar os repositórios e serviços, e executar
 * métodos de demonstração específicos para validar a implementação de
 * estruturas de dados e APIs de Coleções do Java.
 *
 * @author marcos_miller
 */
public class PrincipalTestes {

    // Atributos estáticos para as instâncias dos Repositórios.
    // Serão inicializados no método `rodarTodasDemonstracoes`.
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
    private static Scanner scanner; // Instância de Scanner injetada da classe Principal.

    // Atributos estáticos para as instâncias dos Serviços de negócio.
    // Serão inicializados no método `rodarTodasDemonstracoes`.
    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static RegistroPontoService pontoService; // Adicionado para consistência, se necessário em testes futuros.
    private static ItemEstoqueService itemEstoqueService;
    private static ServicoService servicoService;
    private static OrdemServicoService ordemServicoService;
    private static PagamentoService pagamentoService;
    private static AgendamentoService agendamentoService;
    private static ElevadorService elevadorService;
    private static RelatorioService relatorioService;


    /**
     * Método principal desta classe de testes, responsável por iniciar e executar
     * todas as demonstrações de funcionalidades do projeto.
     * Este método é projetado para ser invocado a partir do método `main`
     * da classe `Principal`, recebendo a instância do `Scanner` para interações
     * com o usuário durante as demonstrações.
     *
     * @param scannerInstancia Uma instância de `Scanner` para entrada de dados do usuário.
     */
    public static void rodarTodasDemonstracoes(Scanner scannerInstancia) {
        System.out.println("\n--- INICIANDO DEMONSTRAÇÕES DO PROJETO FINAL ---");

        // Inicializa o Scanner com a instância fornecida pela classe Principal.
        scanner = scannerInstancia;

        // --- INICIALIZAÇÃO DE REPOSITÓRIOS ---
        // Instancia todos os repositórios que serão utilizados pelos serviços.
        usuarioCRUD = new UsuarioCRUD();
        clienteRepository = new ClienteRepository();
        veiculoRepository = new VeiculoRepository();
        itemEstoqueRepository = new ItemEstoqueRepository();
        servicoRepository = new ServicoRepository();
        ordemServicoRepository = new OrdemServicoRepository();
        pagamentoRepository = new PagamentoRepository();
        agendamentoRepository = new AgendamentoRepository();
        elevadorRepository = ElevadorRepository.getInstance(); // Obtém a instância única do Singleton.
        relatorioRepository = new RelatorioRepository();

        //--- INICIALIZAÇÃO DOS SERVIÇOS ---
        // Instancia os serviços de negócio, injetando as dependências de repositórios e outros serviços.
        // A ordem de inicialização é crucial devido às dependências entre os serviços.
        usuarioService = new UsuarioService(usuarioCRUD);
        clienteService = new ClienteService(clienteRepository, veiculoRepository);
        veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
        // pontoService = new RegistroPontoService(pontoRepository); // Descomentar se o serviço de ponto for necessário para testes.
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

        // --- CHAMADA DOS MÉTODOS DE DEMONSTRAÇÃO ---
        // Invoca os métodos que demonstram funcionalidades específicas do projeto.
        demonstrarQuestao15(clienteService);
        demonstrarQuestao16(clienteService);
        demonstrarQuestao17(clienteService, scanner);
        

        System.out.println("\n--- FIM DE TODAS AS DEMONSTRAÇÕES DO PROJETO FINAL ---\n");
    }

    
    /**
     * Demonstra o uso da interface `Iterator` e do laço `for-each` para percorrer coleções.
     * Este método ilustra a iteração explícita via `Iterator` e a sintaxe simplificada
     * do `for-each`, explicando a relação subjacente entre ambos.
     *
     * @param clienteService O serviço de clientes, utilizado para obter a lista de clientes para a demonstração.
     */
    private static void demonstrarQuestao15(ClienteService clienteService) {
        System.out.println("\n--- DEMONSTRAÇÃO - QUESTÃO 15: ITERATOR E FOR-EACH ---");

        List<Cliente> clientes = clienteService.listarTodosClientes();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado para demonstração da Questão 15.");
            return;
        }

        System.out.println("\n--- Usando Iterator (while(iterator.hasNext()) ---");
        // Instancia um Iterator para percorrer a coleção.
        // O Iterator oferece controle explícito sobre a iteração, incluindo a capacidade de remover elementos.
        Iterator<Cliente> iterator = clientes.iterator();
        int countIterator = 0;
        while (iterator.hasNext()) { // Verifica se há mais elementos na coleção.
            Cliente cliente = iterator.next(); // Obtém o próximo elemento.
            System.out.println("Cliente (Iterator): " + cliente.getNome() + " (ID: " + cliente.getId() + ")");
            countIterator++;
        }
        System.out.println("Total de clientes percorridos com Iterator: " + countIterator);

        System.out.println("\n--- Explicação da relação entre Iterator e for-each ---");
        System.out.println("A interface `Iterator` é um componente fundamental em Java para a travessia de coleções. Ela expõe métodos como `hasNext()` para verificar a existência de elementos subsequentes e `next()` para recuperar o próximo elemento. Representa a abordagem de iteração manual e o mecanismo subjacente para percorrer coleções.");
        System.out.println("\nO laço 'for-each' (também conhecido como *enhanced for loop*), introduzido no Java 5, oferece uma sintaxe mais concisa e legível para iterar sobre coleções e arrays. Para coleções que implementam a interface `Iterable` (como `ArrayList`), o compilador Java transpila o `for-each` para um código que utiliza internamente um `Iterator`.");
        System.out.println("Isso significa que o `for-each` atua como um 'açúcar sintático' para a iteração baseada em `Iterator`, resultando em um código mais limpo e menos propenso a erros de índice, ao encapsular a complexidade da manipulação direta do `Iterator`.");

        System.out.println("\n--- Testando o loop for-each ---");
        int countForEach = 0;
        for (Cliente cliente : clientes) { // Sintaxe simplificada para iteração.
            System.out.println("Cliente (for-each): " + cliente.getNome() + " (ID: " + cliente.getId() + ")");
            countForEach++;
        }
        System.out.println("Total de clientes percorridos com for-each: " + countForEach);

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO - QUESTÃO 15 ---\n");
    }

    /**
     * Demonstra o uso da interface `Comparator` e do método estático `Collections.sort()`.
     * Este método ilustra como definir múltiplas estratégias de ordenação para a mesma classe
     * (`Cliente`) sem modificar a classe em si, utilizando implementações de `Comparator`
     * e aplicando-as com `Collections.sort()`.
     *
     * @param clienteService O serviço de clientes, utilizado para obter a lista de clientes.
     */
    private static void demonstrarQuestao16(ClienteService clienteService) {
        System.out.println("\n--- DEMONSTRAÇÃO - QUESTÃO 16: COMPARATOR E COLLECTIONS.SORT() ---");

        List<Cliente> clientesOriginais = clienteService.listarTodosClientes();
        if (clientesOriginais.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado para demonstração da Questão 16.");
            return;
        }

        System.out.println("\n--- Clientes Originais (sem ordenação garantida) ---");
        // Imprime a lista de clientes na ordem em que foram recuperados.
        clientesOriginais.forEach(System.out::println);

        // Cria cópias da lista original para permitir ordenações independentes sem alterar a lista base.
        List<Cliente> clientesOrdenadosNome = new ArrayList<>(clientesOriginais);
        List<Cliente> clientesOrdenadosEmail = new ArrayList<>(clientesOriginais);

        System.out.println("\n--- Ordenando Clientes por Nome (usando ClienteComparatorByName) ---");
        // Ordena a lista de clientes utilizando um Comparator que define a ordenação por nome.
        Collections.sort(clientesOrdenadosNome, new ClienteComparatorPorNome());
        clientesOrdenadosNome.forEach(System.out::println);

        System.out.println("\n--- Ordenando Clientes por Email (usando ClienteComparatorByEmail) ---");
        // Ordena a lista de clientes utilizando um Comparator que define a ordenação por email.
        Collections.sort(clientesOrdenadosEmail, new ClienteComparatorPorEmail());
        clientesOrdenadosEmail.forEach(System.out::println);

        System.out.println("\n--- Explicação ---");
        System.out.println("A interface `Comparator` (que segue o padrão de projeto Strategy) permite a definição de múltiplas estratégias de ordenação para uma única classe (`Cliente`), sem a necessidade de modificar o código da própria classe `Cliente`.");
        System.out.println("O método estático `Collections.sort()`, pertencente à classe `java.util.Collections`, aceita uma `List` e uma instância de `Comparator`. Ele emprega o `Comparator` fornecido para determinar como os elementos da lista devem ser comparados e, consequentemente, ordenados.");
        System.out.println("Essa abordagem confere grande flexibilidade, possibilitando a ordenação da mesma lista de clientes por diferentes critérios (como nome, email, telefone, etc.) simplesmente fornecendo um `Comparator` distinto.");

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO - QUESTÃO 16 ---\n");
    }

    /**
     * Demonstra um método de busca (`find`) personalizado utilizando `Iterator` e `Comparator`,
     * e compara sua funcionalidade com o método `Collections.binarySearch()`.
     * Este método ilustra a busca linear didática e a busca binária otimizada em listas ordenadas.
     *
     * @param clienteService O serviço de clientes, utilizado para obter a lista de clientes para busca.
     * @param scannerInstancia A instância de `Scanner` para coletar a entrada do usuário durante a demonstração.
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
        // Para a utilização eficaz de Collections.binarySearch(), a lista deve estar ordenada.
        // Portanto, a lista de clientes é ordenada por nome antes da demonstração.
        List<Cliente> clientesOrdenadosPorNome = clienteService.listarClientesOrdenados(new ClienteComparatorPorNome());
        System.out.println("Lista de clientes ordenada por nome (necessário para binarySearch):");
        clientesOrdenadosPorNome.forEach(c -> System.out.println("- " + c.getNome() + " | " + c.getEmail()));

        System.out.print("Digite o nome do cliente a buscar (ex: Ana Beatriz): ");
        String nomeBusca = scannerInstancia.nextLine();
        // Invoca o método de busca personalizado.
        Optional<Cliente> encontrado = findClientePorNome(clientesOrdenadosPorNome, nomeBusca); 
        if (encontrado.isPresent()) {
            System.out.println("Cliente '" + nomeBusca + "' encontrado com 'findClientePorNome': " + encontrado.get());
        } else {
            System.out.println("Cliente '" + nomeBusca + "' NÃO encontrado com 'findClientePorNome'.");
        }

        System.out.println("\n--- Comparação com Collections.binarySearch() ---");
        System.out.println("`Collections.binarySearch()` é um método altamente eficiente para a busca de elementos em listas que já se encontram ordenadas. Ele emprega um algoritmo de busca binária, que oferece um desempenho significativamente superior em comparação com a busca linear (iteração elemento por elemento), especialmente em listas de grande volume.");
        System.out.println("A sua utilização requer que a lista esteja previamente ordenada e que um `Comparator` seja fornecido (ou que os elementos da lista implementem a interface `Comparable`).");

        // Utiliza o ComparatorByName para comparar o nome de um Cliente.
        System.out.print("Digite o nome do cliente a buscar com binarySearch (ex: Zeca Urubu): ");
        String nomeBuscaBinary = scannerInstancia.nextLine();
        // Cria uma instância de Cliente com o nome de busca para ser utilizada como chave na busca binária.
        Cliente clienteParaBuscar = new Cliente(nomeBuscaBinary, "", "");

        // Realiza a busca binária utilizando o mesmo Comparator empregado na ordenação da lista.
        int indice = Collections.binarySearch(clientesOrdenadosPorNome, clienteParaBuscar, new ClienteComparatorPorNome());
        if (indice >= 0) { // Se o índice retornado for não-negativo, o elemento foi encontrado.
            System.out.println("Cliente '" + clienteParaBuscar.getNome() + "' encontrado com binarySearch no índice: " + indice);
            System.out.println("Cliente encontrado: " + clientesOrdenadosPorNome.get(indice));
        } else { // Se o índice for negativo, o elemento não foi encontrado. O valor indica o ponto de inserção.
            System.out.println("Cliente '" + clienteParaBuscar.getNome() + "' NÃO encontrado com binarySearch. Retornou índice negativo: " + indice);
        }

        System.out.println("\n--- Explicação da Aplicação ---");
        System.out.println("O método `findClientePorNome` implementado nesta classe serve primariamente para fins didáticos, demonstrando o uso conjunto de `Iterator` e `Comparator` em uma busca linear. Em contextos de produção, para listas extensas e ordenadas, a abordagem de `Collections.binarySearch` é a preferível devido à sua performance superior, utilizando o mesmo conceito de `Comparator` para a definição do critério de busca.");

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO - QUESTÃO 17 ---\n");
    }

    /**
     * Método auxiliar privado para buscar um cliente por nome em uma lista de clientes.
     * Esta implementação utiliza `Iterator` para percorrer a lista e um `Comparator`
     * para realizar a comparação dos nomes.
     * <p>
     * **Nota Importante:** Embora funcional para demonstração, para cenários de produção
     * com grandes volumes de dados, é mais eficiente utilizar `Collections.binarySearch()`
     * (se a lista estiver ordenada) ou as APIs de Stream do Java para operações de busca.
     *
     * @param clientes A lista de clientes na qual a busca será realizada. Recomenda-se que a lista
     * esteja ordenada pelo nome para otimizar a busca com `Comparator`,
     * especialmente se o objetivo fosse uma busca binária. Para esta busca linear,
     * a ordenação não é estritamente necessária, mas é uma boa prática para alinhamento
     * com métodos de busca mais performáticos.
     * @param nome O nome do cliente a ser buscado.
     * @return Um `Optional<Cliente>`: Retorna um `Optional` contendo o `Cliente` se encontrado,
     * ou um `Optional` vazio (`Optional.empty()`) caso o cliente não seja localizado.
     */
    private static Optional<Cliente> findClientePorNome(List<Cliente> clientes, String nome) {
        // Instancia um Comparator para definir a lógica de comparação de clientes por nome.
        Comparator<Cliente> comparator = new ClienteComparatorPorNome();
        // Obtém um Iterator para percorrer a coleção de clientes.
        Iterator<Cliente> iterator = clientes.iterator();

        // Cria uma instância de Cliente temporária com o nome de busca.
        // Os demais campos são preenchidos com valores vazios, pois não serão usados na comparação de nomes.
        Cliente clienteBusca = new Cliente(nome, "", "");

        // Itera sobre a lista de clientes utilizando o Iterator.
        while (iterator.hasNext()) {
            Cliente clienteAtual = iterator.next();
            // Compara o cliente atual com o cliente de busca. Se o resultado for 0, os nomes são idênticos.
            if (comparator.compare(clienteAtual, clienteBusca) == 0) {
                return Optional.of(clienteAtual); // Retorna o cliente encontrado encapsulado em um Optional.
            }
        }
        return Optional.empty(); // Retorna um Optional vazio se nenhum cliente com o nome especificado for encontrado.
    }
}