package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões à base de dados MySQL.
 *
 * <p>Padrão utilizado: <b>Singleton</b> — garante que as configurações
 * de ligação estão centralizadas num único ponto, evitando duplicação
 * e facilitando alterações futuras (ex: mudar de MySQL para PostgreSQL).</p>
 *
 * <p>A instância é criada apenas uma vez (lazy initialization) e
 * reutilizada em todas as chamadas subsequentes.</p>
 *
 */
public class ConnectionFactory {

    /** Instância única — padrão Singleton. */
    private static ConnectionFactory instancia;

    /** URL de ligação ao MySQL. */
    private final String url;

    /** Utilizador da base de dados. */
    private final String user;

    /** Palavra-passe da base de dados. */
    private final String password;

    /**
     * Construtor privado — impede instanciação externa (Singleton).
     * Inicializa as configurações de ligação.
     */
    private ConnectionFactory() {
        this.url      = "jdbc:mysql://localhost:3306/clinica?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        this.user     = "root";
        this.password = "";
    }

    /**
     * Retorna a instância única da fábrica de conexões.
     * Cria-a na primeira chamada (lazy initialization).
     *
     * @return instância única de {@code ConnectionFactory}
     */
    public static ConnectionFactory getInstance() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    /**
     * Cria e retorna uma nova conexão à base de dados.
     *
     * @return conexão ativa ao MySQL
     * @throws SQLException se não for possível estabelecer a ligação
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
