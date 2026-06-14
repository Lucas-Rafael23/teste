package dao;

import exception.BaseDadosException;
import model.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade {@link Paciente}.
 * Implementa {@link IDAO} garantindo as operações CRUD na tabela {@code paciente}.
 *
 */
public class PacienteDAO implements IDAO<Paciente> {

    /** Fábrica de conexões (Singleton). */
    private final ConnectionFactory factory = ConnectionFactory.getInstance();

    /**
     * {@inheritDoc}
     * Insere um novo paciente e preenche o id gerado automaticamente.
     */
    @Override
    public void inserir(Paciente paciente) throws BaseDadosException {
        String sql = "INSERT INTO paciente (nome, email, telefone, data_nascimento, numero_utente) VALUES (?,?,?,?,?)";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, paciente.getNome());
            ps.setString(2, paciente.getEmail());
            ps.setString(3, paciente.getTelefone());
            ps.setDate(4, paciente.getDataNascimento() != null ? Date.valueOf(paciente.getDataNascimento()) : null);
            ps.setString(5, paciente.getNumeroUtente());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) paciente.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao inserir paciente.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Atualiza todos os campos do paciente identificado pelo seu id.
     */
    @Override
    public void atualizar(Paciente paciente) throws BaseDadosException {
        String sql = "UPDATE paciente SET nome=?, email=?, telefone=?, data_nascimento=?, numero_utente=? WHERE id=?";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, paciente.getNome());
            ps.setString(2, paciente.getEmail());
            ps.setString(3, paciente.getTelefone());
            ps.setDate(4, paciente.getDataNascimento() != null ? Date.valueOf(paciente.getDataNascimento()) : null);
            ps.setString(5, paciente.getNumeroUtente());
            ps.setInt(6, paciente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao atualizar paciente.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Remove o paciente com o id fornecido (e as suas consultas, por CASCADE).
     */
    @Override
    public void remover(int id) throws BaseDadosException {
        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM paciente WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao remover paciente.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Lista todos os pacientes ordenados por nome.
     */
    @Override
    public List<Paciente> listarTodos() throws BaseDadosException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente ORDER BY nome";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao listar pacientes.", e);
        }
        return lista;
    }

    /**
     * Pesquisa pacientes cujo nome contenha o termo fornecido.
     *
     * @param nome texto a pesquisar (case-insensitive)
     * @return lista de pacientes correspondentes
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    public List<Paciente> pesquisarPorNome(String nome) throws BaseDadosException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente WHERE nome LIKE ? ORDER BY nome";

        try (Connection conn = factory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new BaseDadosException("Erro ao pesquisar pacientes.", e);
        }
        return lista;
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto {@link Paciente}.
     *
     * @param resultado resultado da query
     * @return objeto Paciente preenchido
     * @throws SQLException se ocorrer erro ao ler colunas
     */
    private Paciente mapear(ResultSet resultado) throws SQLException {
        Date dataSql = resultado.getDate("data_nascimento");
        LocalDate dataNasc = (dataSql != null) ? dataSql.toLocalDate() : null;

        return new Paciente(
            resultado.getInt("id"),    resultado.getString("nome"),
            resultado.getString("email"), resultado.getString("telefone"),
            dataNasc,           resultado.getString("numero_utente")
        );
    }
}
