package ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor central de notificações de alteração de dados.
 * Implementa os padrões <b>Singleton</b> e <b>Observer</b>.
 *
 * <p><b>Singleton:</b> garante que existe apenas uma instância deste
 * notificador em toda a aplicação, evitando notificações duplicadas.</p>
 *
 * <p><b>Observer:</b> os painéis da UI registam-se como ouvintes
 * ({@link DataChangeListener}) e são notificados automaticamente
 * quando qualquer dado é alterado.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>
 *   // Após guardar um paciente, notificar todos os ouvintes:
 *   DataChangeNotifier.getInstance().notificar(TipoAlteracao.PACIENTE);
 * </pre>
 *
 * @author Clinica
 * @version 1.0
 */
public class DataChangeNotifier {

    /** Instância única — padrão Singleton. */
    private static DataChangeNotifier instancia;

    /** Lista de ouvintes registados. */
    private final List<DataChangeListener> ouvintes = new ArrayList<>();

    /**
     * Construtor privado — impede instanciação externa (Singleton).
     */
    private DataChangeNotifier() {}

    /**
     * Retorna a instância única do notificador.
     * Cria-a na primeira chamada (lazy initialization).
     *
     * @return instância única de {@code DataChangeNotifier}
     */
    public static DataChangeNotifier getInstance() {
        if (instancia == null) {
            instancia = new DataChangeNotifier();
        }
        return instancia;
    }

    /**
     * Regista um ouvinte para receber notificações de alterações.
     *
     * @param ouvinte painel ou componente que implementa {@link DataChangeListener}
     */
    public void registar(DataChangeListener ouvinte) {
        if (!ouvintes.contains(ouvinte)) {
            ouvintes.add(ouvinte);
        }
    }

    /**
     * Remove um ouvinte da lista de notificações.
     *
     * @param ouvinte ouvinte a remover
     */
    public void remover(DataChangeListener ouvinte) {
        ouvintes.remove(ouvinte);
    }

    /**
     * Notifica todos os ouvintes registados sobre uma alteração de dados.
     *
     * @param tipo tipo de entidade que foi alterada
     */
    public void notificar(DataChangeListener.TipoAlteracao tipo) {
        for (DataChangeListener ouvinte : ouvintes) {
            ouvinte.onDadosAlterados(tipo);
        }
    }
}
