package dao;

import exception.BaseDadosException;
import model.Especialidade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade {@link Especialidade}.
 * Responsável pela leitura das especialidades médicas disponíveis.
 *
 * <p>Utiliza {@link ConnectionFactory} (Singleton) para obter conexões
 * e é criado através de {@link DAOFactory} (Factory Pattern).</p>
**/
public class EspecialidadeDAO {

    /** Fábrica de conexões (Singleton). */
    private final ConnectionFactory factory = ConnectionFactory.getInstance();

    /**
     * Lista todas as especialidades ordenadas por nome.
     *
     * @return lista de especialidades
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public List<Especialidade> listarTodas() throws BaseDadosException {
        List<Especialidade> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM especialidade ORDER BY nome";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Especialidade(rs.getInt("id"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao listar especialidades.", e);
        }
        return lista;
    }

    /**
     * Obtém uma especialidade pelo seu id.
     *
     * @param id identificador
     * @return especialidade encontrada, ou {@code null} se não existir
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public Especialidade obterPorId(int id) throws BaseDadosException {
        String sql = "SELECT id, nome FROM especialidade WHERE id = ?";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Especialidade(rs.getInt("id"), rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao obter especialidade com id " + id, e);
        }
        return null;
    }
}
