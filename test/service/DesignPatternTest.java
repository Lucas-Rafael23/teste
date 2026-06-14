package service;

import dao.ConnectionFactory;
import dao.DAOFactory;
import org.junit.Test;
import ui.DataChangeListener;
import ui.DataChangeNotifier;

import static org.junit.Assert.*;

/**
 * Testes unitários para os Design Patterns implementados no sistema.
 * Verifica o comportamento do Singleton, Factory e Observer.
 *
 * @author Clinica
 * @version 1.0
 */
public class DesignPatternTest {

    // ── Singleton — ConnectionFactory ─────────────────────────────

    /**
     * Verifica que ConnectionFactory retorna sempre a mesma instância (Singleton).
     */
    @Test
    public void connectionFactoryDeveSerSingleton() {
        ConnectionFactory instancia1 = ConnectionFactory.getInstance();
        ConnectionFactory instancia2 = ConnectionFactory.getInstance();

        assertNotNull("A instância não deve ser null", instancia1);
        assertSame("Devem ser exatamente a mesma instância (Singleton)",
            instancia1, instancia2);
    }

    /**
     * Verifica que múltiplas chamadas ao Singleton retornam o mesmo objeto.
     */
    @Test
    public void connectionFactorySingletonDeveSerConsistente() {
        ConnectionFactory a = ConnectionFactory.getInstance();
        ConnectionFactory b = ConnectionFactory.getInstance();
        ConnectionFactory c = ConnectionFactory.getInstance();

        assertSame(a, b);
        assertSame(b, c);
    }

    // ── Singleton — DAOFactory ─────────────────────────────────────

    /**
     * Verifica que DAOFactory é um Singleton.
     */
    @Test
    public void daoFactoryDeveSerSingleton() {
        DAOFactory f1 = DAOFactory.getInstance();
        DAOFactory f2 = DAOFactory.getInstance();

        assertNotNull(f1);
        assertSame("DAOFactory deve ser Singleton", f1, f2);
    }

    /**
     * Verifica que DAOFactory é um Singleton.
     */
    @Test
    public void serviceFactoryDeveSerSingleton() {
        ServiceFactory f1 = ServiceFactory.getInstance();
        ServiceFactory f2 = ServiceFactory.getInstance();

        assertNotNull(f1);
        assertSame("ServiceFactory deve ser Singleton", f1, f2);
    }

    // ── Factory — DAOFactory ───────────────────────────────────────

    /**
     * Verifica que DAOFactory cria instâncias novas de PacienteDAO em cada chamada.
     */
    @Test
    public void daoFactoryDeveCriarNovaInstanciaACadaChamada() {
        dao.PacienteDAO dao1 = DAOFactory.getInstance().criarPacienteDAO();
        dao.PacienteDAO dao2 = DAOFactory.getInstance().criarPacienteDAO();

        assertNotNull(dao1);
        assertNotNull(dao2);
        assertNotSame("Factory deve criar instâncias diferentes a cada chamada", dao1, dao2);
    }

    /**
     * Verifica que DAOFactory cria instâncias de todos os DAOs sem erro.
     */
    @Test
    public void daoFactoryDeveCriarTodosOsDAOs() {
        DAOFactory factory = DAOFactory.getInstance();

        assertNotNull("PacienteDAO não deve ser null",    factory.criarPacienteDAO());
        assertNotNull("MedicoDAO não deve ser null",      factory.criarMedicoDAO());
        assertNotNull("ConsultaDAO não deve ser null",    factory.criarConsultaDAO());
        assertNotNull("EspecialidadeDAO não deve ser null", factory.criarEspecialidadeDAO());
    }

    /**
     * Verifica que ServiceFactory cria instâncias de todos os Services sem erro.
     */
    @Test
    public void serviceFactoryDeveCriarTodosOsServicos() {
        ServiceFactory factory = ServiceFactory.getInstance();

        assertNotNull("PacienteService não deve ser null", factory.criarPacienteService());
        assertNotNull("MedicoService não deve ser null",   factory.criarMedicoService());
        assertNotNull("ConsultaService não deve ser null", factory.criarConsultaService());
    }

    // ── Observer — DataChangeNotifier ─────────────────────────────

    /**
     * Verifica que DataChangeNotifier é um Singleton.
     */
    @Test
    public void dataChangeNotifierDeveSerSingleton() {
        DataChangeNotifier n1 = DataChangeNotifier.getInstance();
        DataChangeNotifier n2 = DataChangeNotifier.getInstance();

        assertNotNull(n1);
        assertSame("DataChangeNotifier deve ser Singleton", n1, n2);
    }

    /**
     * Verifica que um ouvinte registado recebe a notificação correta.
     */
    @Test
    public void observerDeveNotificarOuvintesRegistados() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();

        // Ouvinte de teste que regista o tipo recebido
        final DataChangeListener.TipoAlteracao[] tipoRecebido =
            {null};

        DataChangeListener ouvinte = tipo -> tipoRecebido[0] = tipo;

        notifier.registar(ouvinte);
        notifier.notificar(DataChangeListener.TipoAlteracao.PACIENTE);

        assertEquals("O ouvinte deve ter recebido o tipo PACIENTE",
            DataChangeListener.TipoAlteracao.PACIENTE, tipoRecebido[0]);

        // Limpar para não interferir com outros testes
        notifier.remover(ouvinte);
    }

    /**
     * Verifica que após remoção o ouvinte não recebe mais notificações.
     */
    @Test
    public void observerNaoDeveNotificarOuvinteRemovido() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();

        final int[] contagemChamadas = {0};
        DataChangeListener ouvinte = tipo -> contagemChamadas[0]++;

        notifier.registar(ouvinte);
        notifier.notificar(DataChangeListener.TipoAlteracao.MEDICO);
        assertEquals("Deve ter sido notificado 1 vez", 1, contagemChamadas[0]);

        notifier.remover(ouvinte);
        notifier.notificar(DataChangeListener.TipoAlteracao.MEDICO);
        assertEquals("Após remoção não deve ser notificado", 1, contagemChamadas[0]);
    }

    /**
     * Verifica que o mesmo ouvinte não é registado duas vezes.
     */
    @Test
    public void observerNaoDeveRegistarOuvinteDuplicado() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();

        final int[] contagemChamadas = {0};
        DataChangeListener ouvinte = tipo -> contagemChamadas[0]++;

        notifier.registar(ouvinte);
        notifier.registar(ouvinte); // segunda vez — deve ignorar
        notifier.notificar(DataChangeListener.TipoAlteracao.CONSULTA);

        assertEquals("Ouvinte duplicado não deve ser notificado duas vezes",
            1, contagemChamadas[0]);

        notifier.remover(ouvinte);
    }

    /**
     * Verifica que todos os tipos de alteração existem no enum.
     */
    @Test
    public void tiposDeAlteracaoDevemExistir() {
        assertNotNull(DataChangeListener.TipoAlteracao.PACIENTE);
        assertNotNull(DataChangeListener.TipoAlteracao.MEDICO);
        assertNotNull(DataChangeListener.TipoAlteracao.CONSULTA);
        assertEquals(3, DataChangeListener.TipoAlteracao.values().length);
    }
}
