package dao;

import exception.BaseDadosException;
import exception.ValidacaoException;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade {@link Consulta}. Implementa {@link IDAO} garantindo as
 * operações CRUD na tabela {@code consulta}.
 *
 * <p>
 * Utiliza {@link ConnectionFactory} (Singleton) para obter conexões e é criado
 * através de {@link DAOFactory} (Factory Pattern).</p>
 */
public class ConsultaDAO implements IDAO<Consulta> {

    /**
     * Fábrica de conexões (Singleton).
     */
    private final ConnectionFactory factory = ConnectionFactory.getInstance();

    /**
     * {@inheritDoc} Insere uma nova consulta e preenche o id gerado
     * automaticamente.
     */
    @Override
    public void inserir(Consulta consulta) throws BaseDadosException {
        String sql = "INSERT INTO consulta (paciente_id, medico_id, data_hora, estado, notas) VALUES (?,?,?,?,?)";

        try (Connection conn = factory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, consulta.getPaciente().getId());
            ps.setInt(2, consulta.getMedico().getId());
            ps.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            ps.setString(4, consulta.getEstado().name());
            ps.setString(5, consulta.getNotas());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    consulta.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao inserir consulta.", e);
        }
    }

    /**
     * {@inheritDoc} Atualiza todos os campos da consulta identificada pelo seu
     * id.
     */
    @Override
    public void atualizar(Consulta consulta) throws BaseDadosException {
        String sql = "UPDATE consulta SET paciente_id=?, medico_id=?, data_hora=?, estado=?, notas=? WHERE id=?";

        try (Connection conn = factory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, consulta.getPaciente().getId());
            ps.setInt(2, consulta.getMedico().getId());
            ps.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            ps.setString(4, consulta.getEstado().name());
            ps.setString(5, consulta.getNotas());
            ps.setInt(6, consulta.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao atualizar consulta.", e);
        }
    }

    /**
     * {@inheritDoc} Remove a consulta com o id fornecido.
     */
    @Override
    public void remover(int id) throws BaseDadosException {
        try (Connection conn = factory.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM consulta WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao remover consulta.", e);
        }
    }

    /**
     * {@inheritDoc} Lista todas as consultas com dados completos de paciente e
     * médico, ordenadas por data descendente.
     */
    @Override
    public List<Consulta> listarTodos() throws BaseDadosException {
        List<Consulta> lista = new ArrayList<>();
        String sql
                = "SELECT c.*, "
                + "  p.id AS p_id, p.nome AS p_nome, p.email AS p_email, p.telefone AS p_tel, "
                + "  p.data_nascimento, p.numero_utente, "
                + "  m.id AS m_id, m.nome AS m_nome, m.email AS m_email, m.telefone AS m_tel, "
                + "  m.numero_cedula, e.id AS e_id, e.nome AS e_nome "
                + "FROM consulta c "
                + "JOIN paciente p ON c.paciente_id = p.id "
                + "JOIN medico m   ON c.medico_id   = m.id "
                + "LEFT JOIN especialidade e ON m.especialidade_id = e.id "
                + "ORDER BY c.data_hora DESC";

        try (Connection conn = factory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao listar consultas.", e);
        }
        return lista;
    }
    
        /**
     * Verifica se um medico ja tem consulta ativa a menos de 60 minutos
     * da data/hora fornecida.
     *
     * @param medicoId   id do medico a verificar
     * @param dataHora   data e hora da nova consulta
     * @param excluirId  id a ignorar na pesquisa (0 para novas consultas)
     * @return true se existir conflito
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public boolean existeConflitoMedico(int medicoId, LocalDateTime dataHora, int excluirId)
            throws BaseDadosException {
        String sql =
            "SELECT COUNT(*) FROM consulta " +
            "WHERE medico_id = ? " +
            "AND estado != 'CANCELADA' " +
            "AND id != ? " +
            "AND ABS(TIMESTAMPDIFF(MINUTE, data_hora, ?)) < 60";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicoId);
            ps.setInt(2, excluirId);
            ps.setTimestamp(3, Timestamp.valueOf(dataHora));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao verificar conflito de horario do medico.", e);
        }
    }

    /**
     * Verifica se um paciente ja tem consulta ativa a menos de 60 minutos
     * da data/hora fornecida.
     *
     * @param pacienteId id do paciente a verificar
     * @param dataHora   data e hora da nova consulta
     * @param excluirId  id a ignorar na pesquisa (0 para novas consultas)
     * @return true se existir conflito
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public boolean existeConflitoPaciente(int pacienteId, LocalDateTime dataHora, int excluirId)
            throws BaseDadosException {
        String sql =
            "SELECT COUNT(*) FROM consulta " +
            "WHERE paciente_id = ? " +
            "AND estado != 'CANCELADA' " +
            "AND id != ? " +
            "AND ABS(TIMESTAMPDIFF(MINUTE, data_hora, ?)) < 60";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            ps.setInt(2, excluirId);
            ps.setTimestamp(3, Timestamp.valueOf(dataHora));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao verificar conflito de horario do paciente.", e);
        }
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto {@link Consulta}.
     *
     * @param resultado resultado da query
     * @return objeto Consulta preenchido
     * @throws SQLException se ocorrer erro ao ler colunas
     */
    private Consulta mapear(ResultSet resultado) throws SQLException {
        Especialidade esp = null;
        int eId = resultado.getInt("e_id");
        if (!resultado.wasNull()) {
            esp = new Especialidade(eId, resultado.getString("e_nome"));
        }

        Date dataNascSql = resultado.getDate("data_nascimento");
        Paciente paciente = new Paciente(
                resultado.getInt("p_id"), resultado.getString("p_nome"),
                resultado.getString("p_email"), resultado.getString("p_tel"),
                dataNascSql != null ? dataNascSql.toLocalDate() : null,
                resultado.getString("numero_utente")
        );

        Medico medico = new Medico(
                resultado.getInt("m_id"), resultado.getString("m_nome"),
                resultado.getString("m_email"), resultado.getString("m_tel"),
                resultado.getString("numero_cedula"), esp
        );

        LocalDateTime dataHora = resultado.getTimestamp("data_hora").toLocalDateTime();
        Consulta.Estado estado = Consulta.Estado.valueOf(resultado.getString("estado"));

        return new Consulta(resultado.getInt("id"), paciente, medico, dataHora, estado, resultado.getString("notas"));
    }
}
