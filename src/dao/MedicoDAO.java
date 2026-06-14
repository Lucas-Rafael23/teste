package dao;

import exception.BaseDadosException;
import model.Especialidade;
import model.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade {@link Medico}.
 * Implementa {@link IDAO} garantindo as operações CRUD na tabela {@code medico}.
 *
 * <p>Utiliza {@link ConnectionFactory} (Singleton) para obter conexões
 * e é criado através de {@link DAOFactory} (Factory Pattern).</p>
 *
 */
public class MedicoDAO implements IDAO<Medico> {

    /** Fábrica de conexões (Singleton). */
    private final ConnectionFactory factory = ConnectionFactory.getInstance();

    /**
     * {@inheritDoc}
     * Insere um novo médico e preenche o id gerado automaticamente.
     */
    @Override
    public void inserir(Medico medico) throws BaseDadosException {
        String sql = "INSERT INTO medico (nome, email, telefone, numero_cedula, especialidade_id) VALUES (?,?,?,?,?)";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, medico.getNome());
            ps.setString(2, medico.getEmail());
            ps.setString(3, medico.getTelefone());
            ps.setString(4, medico.getNumeroCedula());
            if (medico.getEspecialidade() != null) ps.setInt(5, medico.getEspecialidade().getId());
            else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) medico.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao inserir médico.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Atualiza todos os campos do médico identificado pelo seu id.
     */
    @Override
    public void atualizar(Medico medico) throws BaseDadosException {
        String sql = "UPDATE medico SET nome=?, email=?, telefone=?, numero_cedula=?, especialidade_id=? WHERE id=?";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, medico.getNome());
            ps.setString(2, medico.getEmail());
            ps.setString(3, medico.getTelefone());
            ps.setString(4, medico.getNumeroCedula());
            if (medico.getEspecialidade() != null) ps.setInt(5, medico.getEspecialidade().getId());
            else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, medico.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao atualizar médico.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Remove o médico com o id fornecido.
     */
    @Override
    public void remover(int id) throws BaseDadosException {
        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM medico WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao remover médico.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Lista todos os médicos com a sua especialidade, ordenados por nome.
     */
    @Override
    public List<Medico> listarTodos() throws BaseDadosException {
        List<Medico> lista = new ArrayList<>();
        String sql =
            "SELECT m.*, e.id AS esp_id, e.nome AS esp_nome " +
            "FROM medico m LEFT JOIN especialidade e ON m.especialidade_id = e.id " +
            "ORDER BY m.nome";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao listar médicos.", e);
        }
        return lista;
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto {@link Medico}.
     *
     * @param resultado resultado da query
     * @return objeto Medico preenchido
     * @throws SQLException se ocorrer erro ao ler colunas
     */
    private Medico mapear(ResultSet resultado) throws SQLException {
        Especialidade esp = null;
        int espId = resultado.getInt("esp_id");
        if (!resultado.wasNull()) {
            esp = new Especialidade(espId, resultado.getString("esp_nome"));
        }
        return new Medico(
            resultado.getInt("id"),       resultado.getString("nome"),
            resultado.getString("email"), resultado.getString("telefone"),
            resultado.getString("numero_cedula"), esp
        );
    }
}
