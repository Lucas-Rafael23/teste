package service;

import dao.DAOFactory;
import dao.MedicoDAO;
import exception.BaseDadosException;
import exception.ValidacaoException;
import model.Medico;
import ui.DataChangeListener;
import ui.DataChangeNotifier;

import java.util.List;

/**
 * Serviço de negócio para {@link Medico}.
 * Implementa {@link IValidavel} definindo as regras de validação do médico.
 *
 * <p>Padrões utilizados:</p>
 * <ul>
 *   <li><b>Factory:</b> obtém o DAO através de {@link DAOFactory}</li>
 *   <li><b>Observer:</b> notifica os ouvintes após cada alteração</li>
 *   <li><b>Interface IValidavel:</b> contrato de validação explícito</li>
 * </ul>
 *
 */
public class MedicoService implements IValidavel<Medico> {

    /** DAO obtido via Factory Pattern. */
    private final MedicoDAO dao = DAOFactory.getInstance().criarMedicoDAO();

    /**
     * Regista um novo médico após validação e notifica os ouvintes.
     *
     * @param medico médico a registar
     * @throws ValidacaoException se os dados forem inválidos
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void registar(Medico medico) throws ValidacaoException, BaseDadosException {
        validar(medico);
        dao.inserir(medico);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.MEDICO);
    }

    /**
     * Atualiza os dados de um médico após validação e notifica os ouvintes.
     *
     * @param medico médico a atualizar
     * @throws ValidacaoException se os dados forem inválidos
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void atualizar(Medico medico) throws ValidacaoException, BaseDadosException {
        validar(medico);
        dao.atualizar(medico);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.MEDICO);
    }

    /**
     * Remove um médico e notifica os ouvintes.
     *
     * @param id identificador do médico
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void remover(int id) throws BaseDadosException {
        dao.remover(id);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.MEDICO);
    }

    /**
     * Lista todos os médicos.
     *
     * @return lista de médicos
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public List<Medico> listar() throws BaseDadosException {
        return dao.listarTodos();
    }

    /**
     * {@inheritDoc}
     * Valida que o nome e número de cédula estão preenchidos.
     *
     * @param medico médico a validar
     * @throws ValidacaoException se algum campo obrigatório estiver em falta
     */
    @Override
    public void validar(Medico medico) throws ValidacaoException {
        if (medico.getNome() == null || medico.getNome().isBlank())
            throw new ValidacaoException("O nome do médico é obrigatório.");
        if (medico.getNumeroCedula() == null || medico.getNumeroCedula().isBlank())
            throw new ValidacaoException("O número de cédula é obrigatório.");
    }
}
