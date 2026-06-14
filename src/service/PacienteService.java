package service;

import dao.DAOFactory;
import dao.PacienteDAO;
import exception.BaseDadosException;
import exception.ValidacaoException;
import model.Paciente;
import ui.DataChangeListener;
import ui.DataChangeNotifier;

import java.util.List;

/**
 * Serviço de negócio para {@link Paciente}.
 * Implementa {@link IValidavel} definindo as regras de validação do paciente.
 *
 * <p>Padrões utilizados:</p>
 * <ul>
 *   <li><b>Factory:</b> obtém o DAO através de {@link DAOFactory}</li>
 *   <li><b>Observer:</b> notifica os ouvintes após cada alteração</li>
 *   <li><b>Interface IValidavel:</b> contrato de validação explícito</li>
 * </ul>
 */
public class PacienteService implements IValidavel<Paciente> {

    /** DAO obtido via Factory Pattern. */
    private final PacienteDAO dao = DAOFactory.getInstance().criarPacienteDAO();

    /**
     * Regista um novo paciente após validação e notifica os ouvintes.
     *
     * @param paciente paciente a registar
     * @throws ValidacaoException se os dados forem inválidos
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void registar(Paciente paciente) throws ValidacaoException, BaseDadosException {
        validar(paciente);
        dao.inserir(paciente);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.PACIENTE);
    }

    /**
     * Atualiza os dados de um paciente após validação e notifica os ouvintes.
     *
     * @param paciente paciente a atualizar
     * @throws ValidacaoException se os dados forem inválidos
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void atualizar(Paciente paciente) throws ValidacaoException, BaseDadosException {
        validar(paciente);
        dao.atualizar(paciente);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.PACIENTE);
    }

    /**
     * Remove um paciente e notifica os ouvintes.
     *
     * @param id identificador do paciente
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void remover(int id) throws BaseDadosException {
        dao.remover(id);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.PACIENTE);
    }

    /**
     * Lista todos os pacientes.
     *
     * @return lista de pacientes
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public List<Paciente> listar() throws BaseDadosException {
        return dao.listarTodos();
    }

    /**
     * Pesquisa pacientes por nome.
     *
     * @param nome texto a pesquisar
     * @return lista de pacientes correspondentes
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public List<Paciente> pesquisar(String nome) throws BaseDadosException {
        return dao.pesquisarPorNome(nome);
    }

    /**
     * {@inheritDoc}
     * Valida que o nome e número de utente estão preenchidos.
     *
     * @param paciente paciente a validar
     * @throws ValidacaoException se algum campo obrigatório estiver em falta
     */
    @Override
    public void validar(Paciente paciente) throws ValidacaoException {
        if (paciente.getNome() == null || paciente.getNome().isBlank())
            throw new ValidacaoException("O nome do paciente é obrigatório.");
        if (paciente.getNumeroUtente() == null || paciente.getNumeroUtente().isBlank())
            throw new ValidacaoException("O número de utente é obrigatório.");
    }
}
