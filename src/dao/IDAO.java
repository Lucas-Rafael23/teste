package dao;

import exception.BaseDadosException;
import java.util.List;

/**
 * Interface genérica para o padrão DAO (Data Access Object).
 * Define as operações CRUD comuns a todos os DAOs do sistema.
 *
 * <p>Padrão utilizado: <b>DAO Pattern</b> — separa a lógica de acesso
 * a dados da lógica de negócio, facilitando substituição da fonte de dados.</p>
 *
 * @param <T> tipo da entidade gerida por este DAO
 *
 */
public interface IDAO<T> {

    /**
     * Insere uma nova entidade na base de dados.
     *
     * @param entidade objeto a inserir
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    void inserir(T entidade) throws BaseDadosException;

    /**
     * Atualiza uma entidade existente na base de dados.
     *
     * @param entidade objeto com dados atualizados
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    void atualizar(T entidade) throws BaseDadosException;

    /**
     * Remove uma entidade da base de dados pelo seu identificador.
     *
     * @param id identificador da entidade a remover
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    void remover(int id) throws BaseDadosException;

    /**
     * Lista todas as entidades existentes na base de dados.
     *
     * @return lista com todas as entidades
     * @throws BaseDadosException se ocorrer erro na base de dados
     */
    List<T> listarTodos() throws BaseDadosException;
}
