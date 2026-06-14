package dao;

/**
 * Fábrica centralizada de objetos DAO.
 *
 * <p>Padrão utilizado: <b>Factory Pattern</b> — centraliza a criação
 * de todos os DAOs, desacoplando o resto do sistema das implementações
 * concretas. Se no futuro se mudar a implementação (ex: de MySQL para
 * ficheiros JSON), basta alterar esta classe.</p>
 *
 */
public class DAOFactory {

    /** Instância única — padrão Singleton combinado com Factory. */
    private static DAOFactory instancia;

    /**
     * Construtor privado — impede instanciação externa.
     */
    private DAOFactory() {}

    /**
     * Retorna a instância única da fábrica de DAOs.
     *
     * @return instância única de {@code DAOFactory}
     */
    public static DAOFactory getInstance() {
        if (instancia == null) {
            instancia = new DAOFactory();
        }
        return instancia;
    }

    /**
     * Cria e retorna uma nova instância de {@link PacienteDAO}.
     *
     * @return instância de PacienteDAO
     */
    public PacienteDAO criarPacienteDAO() {
        return new PacienteDAO();
    }

    /**
     * Cria e retorna uma nova instância de {@link MedicoDAO}.
     *
     * @return instância de MedicoDAO
     */
    public MedicoDAO criarMedicoDAO() {
        return new MedicoDAO();
    }

    /**
     * Cria e retorna uma nova instância de {@link ConsultaDAO}.
     *
     * @return instância de ConsultaDAO
     */
    public ConsultaDAO criarConsultaDAO() {
        return new ConsultaDAO();
    }

    /**
     * Cria e retorna uma nova instância de {@link EspecialidadeDAO}.
     *
     * @return instância de EspecialidadeDAO
     */
    public EspecialidadeDAO criarEspecialidadeDAO() {
        return new EspecialidadeDAO();
    }
}
