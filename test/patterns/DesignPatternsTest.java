package patterns;

import dao.ConnectionFactory;
import dao.DAOFactory;
import service.ServiceFactory;
import ui.DataChangeListener;
import ui.DataChangeNotifier;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Testes unitários para os Design Patterns implementados no sistema.
 * Verifica a correta implementação dos padrões Singleton, Factory e Observer.
 *
 * @author Clinica
 * @version 1.0
 */
public class DesignPatternsTest {

    // ══════════════════════════════════════════════════════════════
    //  PADRÃO SINGLETON
    // ══════════════════════════════════════════════════════════════

    /**
     * ConnectionFactory deve retornar sempre a mesma instância (Singleton).
     */
    @Test
    public void connectionFactoryDeveSerSingleton() {
        ConnectionFactory instancia1 = ConnectionFactory.getInstance();
        ConnectionFactory instancia2 = ConnectionFactory.getInstance();
        assertSame("ConnectionFactory deve ser Singleton — mesma instância",
            instancia1, instancia2);
    }

    /**
     * DAOFactory deve retornar sempre a mesma instância (Singleton).
     */
    @Test
    public void daoFactoryDeveSerSingleton() {
        DAOFactory instancia1 = DAOFactory.getInstance();
        DAOFactory instancia2 = DAOFactory.getInstance();
        assertSame("DAOFactory deve ser Singleton — mesma instância",
            instancia1, instancia2);
    }

    /**
     * ServiceFactory deve retornar sempre a mesma instância (Singleton).
     */
    @Test
    public void serviceFactoryDeveSerSingleton() {
        ServiceFactory instancia1 = ServiceFactory.getInstance();
        ServiceFactory instancia2 = ServiceFactory.getInstance();
        assertSame("ServiceFactory deve ser Singleton — mesma instância",
            instancia1, instancia2);
    }

    /**
     * DataChangeNotifier deve retornar sempre a mesma instância (Singleton).
     */
    @Test
    public void dataChangeNotifierDeveSerSingleton() {
        DataChangeNotifier instancia1 = DataChangeNotifier.getInstance();
        DataChangeNotifier instancia2 = DataChangeNotifier.getInstance();
        assertSame("DataChangeNotifier deve ser Singleton — mesma instância",
            instancia1, instancia2);
    }

    /**
     * Nenhum Singleton deve retornar null.
     */
    @Test
    public void singletonNuncaDeveRetornarNull() {
        assertNotNull("ConnectionFactory não deve ser null",  ConnectionFactory.getInstance());
        assertNotNull("DAOFactory não deve ser null",         DAOFactory.getInstance());
        assertNotNull("ServiceFactory não deve ser null",     ServiceFactory.getInstance());
        assertNotNull("DataChangeNotifier não deve ser null", DataChangeNotifier.getInstance());
    }

    // ══════════════════════════════════════════════════════════════
    //  PADRÃO FACTORY
    // ══════════════════════════════════════════════════════════════

    /**
     * DAOFactory deve criar instâncias não-null dos DAOs.
     */
    @Test
    public void daoFactoryDeveCriarDaosNaoNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull("PacienteDAO não deve ser null",     factory.criarPacienteDAO());
        assertNotNull("MedicoDAO não deve ser null",       factory.criarMedicoDAO());
        assertNotNull("ConsultaDAO não deve ser null",     factory.criarConsultaDAO());
        assertNotNull("EspecialidadeDAO não deve ser null", factory.criarEspecialidadeDAO());
    }

    /**
     * DAOFactory deve criar uma nova instância em cada chamada.
     */
    @Test
    public void daoFactoryDeveCriarNovaInstanciaACadaChamada() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotSame("Cada chamada deve criar uma nova instância de PacienteDAO",
            factory.criarPacienteDAO(), factory.criarPacienteDAO());
    }

    /**
     * ServiceFactory deve criar instâncias não-null dos serviços.
     */
    @Test
    public void serviceFactoryDeveCriarServicosNaoNull() {
        ServiceFactory factory = ServiceFactory.getInstance();
        assertNotNull("PacienteService não deve ser null",  factory.criarPacienteService());
        assertNotNull("MedicoService não deve ser null",    factory.criarMedicoService());
        assertNotNull("ConsultaService não deve ser null",  factory.criarConsultaService());
    }

    // ══════════════════════════════════════════════════════════════
    //  PADRÃO OBSERVER
    // ══════════════════════════════════════════════════════════════

    /**
     * Implementação de teste do DataChangeListener para verificar notificações.
     */
    private static class OuvinteTestavel implements DataChangeListener {
        final List<DataChangeListener.TipoAlteracao> tiposRecebidos = new ArrayList<>();

        @Override
        public void onDadosAlterados(DataChangeListener.TipoAlteracao tipo) {
            tiposRecebidos.add(tipo);
        }
    }

    /**
     * Ouvinte registado deve receber notificações.
     */
    @Test
    public void ouvintegRegistadoDeveReceberNotificacoes() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();
        OuvinteTestavel ouvinte = new OuvinteTestavel();
        notifier.registar(ouvinte);

        notifier.notificar(DataChangeListener.TipoAlteracao.PACIENTE);

        assertTrue("Ouvinte devia ter recebido pelo menos 1 notificação",
            ouvinte.tiposRecebidos.size() >= 1);
        assertTrue("Devia ter recebido notificação do tipo PACIENTE",
            ouvinte.tiposRecebidos.contains(DataChangeListener.TipoAlteracao.PACIENTE));

        notifier.remover(ouvinte);
    }

    /**
     * Ouvinte removido não deve receber mais notificações.
     */
    @Test
    public void ouvintegRemovidoNaoDeveReceberNotificacoes() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();
        OuvinteTestavel ouvinte = new OuvinteTestavel();

        notifier.registar(ouvinte);
        notifier.remover(ouvinte);

        int contadorAntes = ouvinte.tiposRecebidos.size();
        notifier.notificar(DataChangeListener.TipoAlteracao.MEDICO);

        assertEquals("Ouvinte removido não devia receber notificações",
            contadorAntes, ouvinte.tiposRecebidos.size());
    }

    /**
     * Múltiplos ouvintes devem receber a mesma notificação.
     */
    @Test
    public void multiplosOuvintesDevemReceberNotificacao() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();
        OuvinteTestavel ouvinte1 = new OuvinteTestavel();
        OuvinteTestavel ouvinte2 = new OuvinteTestavel();
        OuvinteTestavel ouvinte3 = new OuvinteTestavel();

        notifier.registar(ouvinte1);
        notifier.registar(ouvinte2);
        notifier.registar(ouvinte3);

        notifier.notificar(DataChangeListener.TipoAlteracao.CONSULTA);

        assertTrue("Ouvinte 1 devia ter recebido notificação",
            ouvinte1.tiposRecebidos.contains(DataChangeListener.TipoAlteracao.CONSULTA));
        assertTrue("Ouvinte 2 devia ter recebido notificação",
            ouvinte2.tiposRecebidos.contains(DataChangeListener.TipoAlteracao.CONSULTA));
        assertTrue("Ouvinte 3 devia ter recebido notificação",
            ouvinte3.tiposRecebidos.contains(DataChangeListener.TipoAlteracao.CONSULTA));

        notifier.remover(ouvinte1);
        notifier.remover(ouvinte2);
        notifier.remover(ouvinte3);
    }

    /**
     * Registar o mesmo ouvinte duas vezes não deve duplicar notificações.
     */
    @Test
    public void registarOuvinteRepetidoNaoDeveDuplicar() {
        DataChangeNotifier notifier = DataChangeNotifier.getInstance();
        OuvinteTestavel ouvinte = new OuvinteTestavel();

        notifier.registar(ouvinte);
        notifier.registar(ouvinte); // registo duplicado

        int contadorAntes = ouvinte.tiposRecebidos.size();
        notifier.notificar(DataChangeListener.TipoAlteracao.PACIENTE);

        assertEquals("Registo duplicado não deve causar notificação dupla",
            contadorAntes + 1, ouvinte.tiposRecebidos.size());

        notifier.remover(ouvinte);
    }

    /**
     * TipoAlteracao deve ter os três tipos esperados.
     */
    @Test
    public void tipoAlteracaoDeveConterTodosTipos() {
        DataChangeListener.TipoAlteracao[] tipos = DataChangeListener.TipoAlteracao.values();
        assertEquals("Deve haver 3 tipos de alteração", 3, tipos.length);

        boolean temPaciente  = false;
        boolean temMedico    = false;
        boolean temConsulta  = false;

        for (DataChangeListener.TipoAlteracao t : tipos) {
            if (t == DataChangeListener.TipoAlteracao.PACIENTE)  temPaciente  = true;
            if (t == DataChangeListener.TipoAlteracao.MEDICO)    temMedico    = true;
            if (t == DataChangeListener.TipoAlteracao.CONSULTA)  temConsulta  = true;
        }

        assertTrue("Deve existir tipo PACIENTE",  temPaciente);
        assertTrue("Deve existir tipo MEDICO",    temMedico);
        assertTrue("Deve existir tipo CONSULTA",  temConsulta);
    }
}
