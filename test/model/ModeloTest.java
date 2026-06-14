package model;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
/** Testes para a hierarquia de modelos. @author Clinica @version 1.0 */
public class ModeloTest {
    private Paciente paciente;
    private Medico medico;
    private Especialidade especialidade;
    @Before public void setUp() {
        especialidade = new Especialidade(1,"Cardiologia");
        paciente = new Paciente(1,"Carlos Silva","carlos@email.pt","910000001",LocalDate.of(1990,5,15),"U-12345");
        medico = new Medico(1,"Dr. António Costa","antonio@clinica.pt","253000001","C-99001",especialidade);
    }
    @Test public void pacienteDeveSerInstanciaDePessoa() { assertTrue(paciente instanceof Pessoa); }
    @Test public void medicoDeveSerInstanciaDePessoa() { assertTrue(medico instanceof Pessoa); }
//    @Test public void pacienteEMedicoSaoTiposDiferentes() {
//        assertFalse(paciente instanceof Medico); assertFalse(medico instanceof Paciente);
//    }
    @Test public void pacienteDeveGuardarDadosCorretamente() {
        assertEquals(1,paciente.getId()); assertEquals("Carlos Silva",paciente.getNome());
        assertEquals("U-12345",paciente.getNumeroUtente()); assertEquals(LocalDate.of(1990,5,15),paciente.getDataNascimento());
    }
    @Test public void pacienteDescricaoDeveIncluirNumeroUtente() {
        assertTrue(paciente.getDescricao().contains("Carlos Silva")); assertTrue(paciente.getDescricao().contains("U-12345"));
    }
    @Test public void medicoDescricaoDeveIncluirEspecialidade() {
        assertTrue(medico.getDescricao().contains("Dr. António Costa")); assertTrue(medico.getDescricao().contains("Cardiologia"));
    }
    @Test public void medicoSemEspecialidadeNaoDeveLancarErro() {
        Medico semEsp = new Medico("Dr. X","x@clinica.pt","","C-00001",null);
        assertNull(semEsp.getEspecialidade()); assertNotNull(semEsp.getDescricao());
    }
    @Test public void novaConsultaDeveEstarNoEstadoMarcada() {
        Consulta c = new Consulta(paciente,medico,LocalDateTime.now().plusDays(1),"Teste");
        assertEquals(Consulta.Estado.MARCADA,c.getEstado());
    }
    @Test public void consultaEstadoPodeSerAlterado() {
        Consulta c = new Consulta(paciente,medico,LocalDateTime.now().plusDays(2),"");
        c.setEstado(Consulta.Estado.REALIZADA); assertEquals(Consulta.Estado.REALIZADA,c.getEstado());
        c.setEstado(Consulta.Estado.CANCELADA); assertEquals(Consulta.Estado.CANCELADA,c.getEstado());
    }
}
