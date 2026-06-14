package service;

/**
 * Fábrica centralizada de objetos Service.
 *
 * <p>Padrão utilizado: <b>Factory Pattern</b> — centraliza a criação
 * de todos os serviços de negócio. A interface gráfica nunca instancia
 * serviços diretamente, usando sempre esta fábrica.</p>
 *
 * <p>Benefícios:</p>
 * <ul>
 *   <li>Desacoplamento entre UI e lógica de negócio</li>
 *   <li>Facilita testes (pode-se substituir por mock services)</li>
 *   <li>Ponto único de controlo para criação de serviços</li>
 * </ul>
 *
 * <p>Exemplo de uso:</p>
 * <pre>
 *   PacienteService svc = ServiceFactory.getInstance().criarPacienteService();
 * </pre>
 *
 * @author Clinica
 * @version 1.0
 */
public class ServiceFactory {

    /** Instância única — padrão Singleton combinado com Factory. */
    private static ServiceFactory instancia;

    /**
     * Construtor privado — impede instanciação externa.
     */
    private ServiceFactory() {}

    /**
     * Retorna a instância única da fábrica de serviços.
     *
     * @return instância única de {@code ServiceFactory}
     */
    public static ServiceFactory getInstance() {
        if (instancia == null) {
            instancia = new ServiceFactory();
        }
        return instancia;
    }

    /**
     * Cria e retorna uma nova instância de {@link PacienteService}.
     *
     * @return instância de PacienteService
     */
    public PacienteService criarPacienteService() {
        return new PacienteService();
    }

    /**
     * Cria e retorna uma nova instância de {@link MedicoService}.
     *
     * @return instância de MedicoService
     */
    public MedicoService criarMedicoService() {
        return new MedicoService();
    }

    /**
     * Cria e retorna uma nova instância de {@link ConsultaService}.
     *
     * @return instância de ConsultaService
     */
    public ConsultaService criarConsultaService() {
        return new ConsultaService();
    }
}
