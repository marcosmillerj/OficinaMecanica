package models;

import java.util.Objects;

/**
 * Classe que representa veículo
 * @author barbo
 */
public class Veiculo {
    public static int proximoId = 1;

    // --- VARIÁVEIS PARA CONTAR INSTÂNCIAS (REQUISITO) ---
    // Estratégia 1: Encapsulamento (private static com getters)
    private static int contadorInstanciasEncapsulado = 0;

    // Estratégia 2: Controle de acesso "protected"
    protected static int contadorInstanciasProtegido = 0;

    private int id;
    private String placa;
    private String modelo;
    private String cor;
    private int idCliente;

    /**
     * Construtor para criar um novo veículo.
     * Incrementa os contadores de instâncias.
     */
    public Veiculo (String placa, String modelo, String cor, int idCliente){
        this.id = proximoId++;
        this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula.");
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo.");
        this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula.");
        this.idCliente = idCliente;

        // Incrementa os contadores ao criar uma nova instância
        contadorInstanciasEncapsulado++;
        contadorInstanciasProtegido++;
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * Não incrementa contadores, pois não é uma nova criação.
     */
    public Veiculo(int id, String placa, String modelo, String cor, int idCliente) {
        this.id = id;
        this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula.");
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo.");
        this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula.");
        this.idCliente = idCliente;
        // Não incrementa os contadores aqui pq n vamos persisitr no gson
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public String getPlaca(){ return placa; }
    public void setPlaca(String placa){ this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula."); }
    public String getModelo(){ return modelo; }
    public void setModelo(String modelo){ this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo."); }
    public String getCor(){ return cor; }
    public void setCor(String cor){ this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula."); }
    public int getIdCliente(){ return idCliente; }
    public void setIdCliente(int idCliente){ this.idCliente = idCliente; }

    // --- MÉTODOS PARA ACESSAR OS CONTADORES (REQUISITO) ---
    /**
     * Retorna o número total de instâncias de Veículo criadas.
     * (Estratégia: private static com getter - Encapsulamento)
     * @return O número de veículos criados.
     */
    public static int getTotalVeiculosCriadosEncapsulado() {
        return contadorInstanciasEncapsulado;
    }

    /**
     * Retorna o número total de instâncias de Veículo criadas.
     * (Estratégia: protected static - acessível por subclasses e classes do mesmo pacote)
     * @return O número de veículos criados.
     */
    public static int getTotalVeiculosCriadosProtegido() {
        return contadorInstanciasProtegido;
    }

    @Override
    public String toString() {
        return "Veiculo {" +
                "ID=" + id +
                ", placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", cor='" + cor + '\'' +
                ", clienteID=" + idCliente +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return id == veiculo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}