package service;

import dao.ConsultaDAO;
import dao.DAOFactory;
import exception.BaseDadosException;
import exception.ValidacaoException;
import model.Consulta;
import ui.DataChangeListener;
import ui.DataChangeNotifier;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de negócio para {@link Consulta}.
 * Implementa {@link IValidavel} definindo as regras de negócio das consultas.
 *
 * <p>Padrões utilizados:</p>
 * <ul>
 *   <li><b>Factory:</b> obtém o DAO através de {@link DAOFactory}</li>
 *   <li><b>Observer:</b> notifica os ouvintes após cada alteração</li>
 *   <li><b>Interface IValidavel:</b> contrato de validação explícito</li>
 * </ul>
 *
 */
public class ConsultaService implements IValidavel<Consulta> {

    /** Intervalo minimo em minutos entre consultas. */
    private static final int INTERVALO_MINIMO_MINUTOS = 60;
    
    /** DAO obtido via Factory Pattern. */
    private final ConsultaDAO dao = DAOFactory.getInstance().criarConsultaDAO();

    /**
     * Marca uma nova consulta após validação e notifica os ouvintes.
     *
     * @param consulta consulta a marcar
     * @throws ValidacaoException se os dados forem inválidos
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void marcar(Consulta consulta) throws ValidacaoException, BaseDadosException {
        validar(consulta);
        verificarConflitoMedico(consulta);
        verificarConflitoPaciente(consulta);
        dao.inserir(consulta);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.CONSULTA);
    }

    /**
     * Cancela uma consulta existente e notifica os ouvintes.
     *
     * @param consulta consulta a cancelar
     * @throws ValidacaoException se a consulta já estiver cancelada
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void cancelar(Consulta consulta) throws ValidacaoException, BaseDadosException {
        if (consulta.getEstado() == Consulta.Estado.CANCELADA)
            throw new ValidacaoException("A consulta já está cancelada.");
        consulta.setEstado(Consulta.Estado.CANCELADA);
        dao.atualizar(consulta);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.CONSULTA);
    }

    /**
     * Marca uma consulta como realizada e notifica os ouvintes.
     *
     * @param consulta consulta a marcar como realizada
     * @throws ValidacaoException se a consulta não estiver no estado MARCADA
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public void marcarRealizada(Consulta consulta) throws ValidacaoException, BaseDadosException {
        if (consulta.getEstado() != Consulta.Estado.MARCADA)
            throw new ValidacaoException("Só é possível marcar como realizada uma consulta no estado MARCADA.");
        consulta.setEstado(Consulta.Estado.REALIZADA);
        dao.atualizar(consulta);
        DataChangeNotifier.getInstance().notificar(DataChangeListener.TipoAlteracao.CONSULTA);
    }

    /**
     * Lista todas as consultas.
     *
     * @return lista de consultas ordenadas por data descendente
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public List<Consulta> listar() throws BaseDadosException {
        return dao.listarTodos();
    }

    /**
     * {@inheritDoc}
     * Valida que paciente, médico e data estão preenchidos, e que a data é futura.
     *
     * @param consulta consulta a validar
     * @throws ValidacaoException se alguma regra de negócio for violada
     */
    @Override
    public void validar(Consulta consulta) throws ValidacaoException {
        if (consulta.getPaciente() == null)
            throw new ValidacaoException("É necessário selecionar um paciente.");
        if (consulta.getMedico() == null)
            throw new ValidacaoException("É necessário selecionar um médico.");
        if (consulta.getDataHora() == null)
            throw new ValidacaoException("A data e hora da consulta são obrigatórias.");
        if (consulta.getDataHora().isBefore(LocalDateTime.now()))
            throw new ValidacaoException("A data da consulta não pode ser no passado.");
        
    }
    
    /**
     * Verifica se o medico da consulta tem disponibilidade no horario pretendido.
     * Lanca {@link ValidacaoException} se existir outra consulta ativa do mesmo
     * medico a menos de {@value #INTERVALO_MINIMO_MINUTOS} minutos.
     *
     * @param consulta consulta a verificar
     * @throws ValidacaoException se o medico estiver ocupado nesse intervalo
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    private void verificarConflitoMedico(Consulta consulta)
            throws ValidacaoException, BaseDadosException {

        boolean conflito = dao.existeConflitoMedico(
            consulta.getMedico().getId(),
            consulta.getDataHora(),
            consulta.getId()
        );

        if (conflito) {
            throw new ValidacaoException(
                "O medico " + consulta.getMedico().getNome() +
                " ja tem uma consulta nesse intervalo de tempo.\n" +
                "E necessario pelo menos " + INTERVALO_MINIMO_MINUTOS +
                " minutos de diferenca entre consultas."
            );
        }
    }

    /**
     * Verifica se o paciente da consulta tem disponibilidade no horario pretendido.
     * Lanca {@link ValidacaoException} se existir outra consulta ativa do mesmo
     * paciente a menos de {@value #INTERVALO_MINIMO_MINUTOS} minutos.
     *
     * @param consulta consulta a verificar
     * @throws ValidacaoException se o paciente ja tiver consulta nesse intervalo
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    private void verificarConflitoPaciente(Consulta consulta)
            throws ValidacaoException, BaseDadosException {

        boolean conflito = dao.existeConflitoPaciente(
            consulta.getPaciente().getId(),
            consulta.getDataHora(),
            consulta.getId()
        );

        if (conflito) {
            throw new ValidacaoException(
                "O paciente " + consulta.getPaciente().getNome() +
                " ja tem uma consulta nesse intervalo de tempo.\n" +
                "E necessario pelo menos " + INTERVALO_MINIMO_MINUTOS +
                " minutos de diferenca entre consultas."
            );
        }
    }
}
