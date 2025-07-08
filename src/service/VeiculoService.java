package service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors; // Adicionado para o método listarVeiculosPorCliente
import models.Cliente; // Necessário para gerenciar a associação com o cliente
import models.Veiculo;
import repository.ClienteRepository;
import repository.VeiculoRepository;

/**
 * Serviço de negócio para a entidade Veiculo.
 * Gerencia as regras de negócio e operações CRUD para veículos.
 *
 * @author marcos_miller
 */
public class VeiculoService {

    private VeiculoRepository veiculoRepository;
    private ClienteRepository clienteRepository;

    public VeiculoService(VeiculoRepository veiculoRepository, ClienteRepository clienteRepository) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Adiciona um novo veículo ao sistema.
     * @param placa Placa do veículo.
     * @param modelo Modelo do veículo.
     * @param cor Cor do veículo.
     * @param idCliente ID do cliente proprietário.
     * @return O Veiculo criado.
     * @throws IllegalStateException Se a placa já estiver cadastrada.
     * @throws IllegalArgumentException Se o cliente proprietário não for encontrado.
     */
    public Veiculo adicionarVeiculo(String placa, String modelo, String cor, int idCliente) throws IllegalStateException, IllegalArgumentException {
        Objects.requireNonNull(placa, "Placa do veículo não pode ser nula.");
        Objects.requireNonNull(modelo, "Modelo do veículo não pode ser nulo.");
        Objects.requireNonNull(cor, "Cor do veículo não pode ser nula.");
        
        if (placa.trim().isEmpty()) throw new IllegalArgumentException("Placa não pode ser vazia.");
        if (modelo.trim().isEmpty()) throw new IllegalArgumentException("Modelo não pode ser vazio.");
        if (cor.trim().isEmpty()) throw new IllegalArgumentException("Cor não pode ser vazia.");

        if (veiculoRepository.buscarVeiculoPorPlaca(placa).isPresent()) {
            throw new IllegalStateException("Erro: Placa '" + placa + "' já cadastrada.");
        }

        if (clienteRepository.buscarClientePorId(idCliente).isEmpty()) {
            throw new IllegalArgumentException("Erro: Cliente com ID " + idCliente + " não encontrado para associar ao veículo.");
        }

        Veiculo novoVeiculo = new Veiculo(placa, modelo, cor, idCliente);
        veiculoRepository.adicionarVeiculo(novoVeiculo);
        // A associação com o cliente (adicionar idVeiculo ao Cliente) será feita na camada de View/Componente
        // para manter a responsabilidade dividida, conforme a sua estrutura atual.
        return novoVeiculo;
    }
    
    /**
     * Atualiza os dados de um veículo existente, incluindo a possibilidade de mudar o cliente proprietário.
     * @param idVeiculo ID do veículo a ser atualizado.
     * @param novaPlaca Nova placa.
     * @param novoModelo Novo modelo.
     * @param novaCor Nova cor.
     * @param novoIdCliente Novo ID do cliente proprietário. (NOVO PARÂMETRO)
     * @return true se atualizado, false se não encontrado.
     * @throws IllegalStateException Se a nova placa já pertencer a outro veículo ou o novo cliente não for encontrado.
     * @throws IllegalArgumentException Se dados de entrada forem nulos/vazios.
     */
    public boolean atualizarVeiculo(int idVeiculo, String novaPlaca, String novoModelo, String novaCor, int novoIdCliente) throws IllegalStateException, IllegalArgumentException {
        Objects.requireNonNull(novaPlaca, "Nova placa do veículo não pode ser nula.");
        Objects.requireNonNull(novoModelo, "Novo modelo do veículo não pode ser nulo.");
        Objects.requireNonNull(novaCor, "Nova cor do veículo não pode ser nula.");
        
        if (novaPlaca.trim().isEmpty()) throw new IllegalArgumentException("Nova placa não pode ser vazia.");
        if (novoModelo.trim().isEmpty()) throw new IllegalArgumentException("Novo modelo não pode ser vazio.");
        if (novaCor.trim().isEmpty()) throw new IllegalArgumentException("Nova cor não pode ser vazia.");

        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            return false; // Veículo não encontrado
        }
        Veiculo veiculoParaAtualizar = veiculoOpt.get();

        // Validação de unicidade para placa (se a placa foi alterada)
        if (!veiculoParaAtualizar.getPlaca().equalsIgnoreCase(novaPlaca)) {
            Optional<Veiculo> existentePorPlaca = veiculoRepository.buscarVeiculoPorPlaca(novaPlaca);
            if (existentePorPlaca.isPresent() && existentePorPlaca.get().getId() != idVeiculo) {
                throw new IllegalStateException("Erro: Nova placa '" + novaPlaca + "' já cadastrada para outro veículo.");
            }
        }
        
        // Verifica se o novo cliente proprietário existe (NOVA VALIDAÇÃO)
        Optional<Cliente> novoClienteOpt = clienteRepository.buscarClientePorId(novoIdCliente);
        if (novoClienteOpt.isEmpty()) {
            throw new IllegalArgumentException("Erro: Novo cliente proprietário com ID " + novoIdCliente + " não encontrado.");
        }

        // Lógica para atualizar a lista de veículos nos clientes se o proprietário mudou (NOVA LÓGICA)
        if (veiculoParaAtualizar.getIdCliente() != novoIdCliente) {
            // Remove o veículo do cliente antigo
            clienteRepository.buscarClientePorId(veiculoParaAtualizar.getIdCliente())
                             .ifPresent(clienteAntigo -> clienteAntigo.removerIdVeiculo(idVeiculo));
            
            // Adiciona o veículo ao novo cliente
            novoClienteOpt.get().adicionarIdVeiculo(idVeiculo);
        }

        veiculoParaAtualizar.setPlaca(novaPlaca);
        veiculoParaAtualizar.setModelo(novoModelo);
        veiculoParaAtualizar.setCor(novaCor);
        veiculoParaAtualizar.setIdCliente(novoIdCliente); // Atualiza o ID do cliente no Veículo
        veiculoRepository.atualizarVeiculo(veiculoParaAtualizar);
        return true;
    }

    // Métodos de busca e listagem (mantidos como estão)
    public Optional<Veiculo> buscarVeiculoPorId(int id) {
        return veiculoRepository.buscarVeiculoPorId(id);
    }
    
    public Optional<Veiculo> buscarVeiculoPorPlaca(String placa) {
        // Adicionado verificação de nulo/vazio para placa, para evitar NullPointerException no trim()
        if (placa == null || placa.trim().isEmpty()) {
            return Optional.empty();
        }
        return veiculoRepository.buscarVeiculoPorPlaca(placa);
    }

    public List<Veiculo> listarTodosVeiculos() {
        return veiculoRepository.listarTodosVeiculos();
    }

    /**
     * Remove um veículo do sistema e também da lista de veículos do cliente proprietário. (LÓGICA ATUALIZADA)
     * @param id ID do veículo a ser removido.
     * @return true se removido, false se não encontrado.
     */
    public boolean removerVeiculo(int id) {
        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(id);
        if (veiculoOpt.isEmpty()) {
            return false;
        }
        
        Veiculo veiculoParaRemover = veiculoOpt.get();
        // Remove o ID do veículo da lista do cliente proprietário
        clienteRepository.buscarClientePorId(veiculoParaRemover.getIdCliente())
                         .ifPresent(cliente -> cliente.removerIdVeiculo(id));

        return veiculoRepository.removerVeiculo(id);
    }

    /**
     * Lista veículos de um cliente específico. (MÉTODO ADICIONAL ÚTIL)
     * @param idCliente ID do cliente.
     * @return Lista de veículos do cliente.
     */
    public List<Veiculo> listarVeiculosPorCliente(int idCliente) {
        return veiculoRepository.listarTodosVeiculos().stream()
                .filter(v -> v.getIdCliente() == idCliente)
                .collect(Collectors.toList());
    }
}