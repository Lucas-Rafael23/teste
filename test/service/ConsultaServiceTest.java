package service;

import exception.ValidacaoException;
import model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Testes unitários para {@link ConsultaService}.
 * Verifica regras de negócio de marcação, cancelamento e transições de estado.
 *
 * @author Clinica
 * @version 1.0
 */
public class ConsultaServiceTest {

    private ConsultaService service;
    private Paciente        paciente;
    private Medico          medico;

    /** Inicializa objetos de teste antes de cada caso. */
    @Before
    public void setUp() {
        service  = new ConsultaService();
        paciente = new Paciente(1, "Carlos Silva", "carlos@email.pt",
            "910000001", LocalDate.of(1990, 1, 1), "U-11111");
        medico = new Medico(1, "Dr. António Costa", "antonio@clinica.pt",
            "253000001", "C-99001", new Especialidade(1, "Clínica Geral"));
    }

    // ── Validação ao Marcar ─────────────────────────────────────────

    /** Marcar sem paciente deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarSemPacienteDeveLancarExcecao() throws Exception {
        service.marcar(new Consulta(null, medico,
            LocalDateTime.now().plusDays(1), "Teste"));
    }

    /** Marcar sem médico deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarSemMedicoDeveLancarExcecao() throws Exception {
        service.marcar(new Consulta(paciente, null,
            LocalDateTime.now().plusDays(1), "Teste"));
    }

    /** Marcar sem data deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarSemDataDeveLancarExcecao() throws Exception {
        service.marcar(new Consulta(paciente, medico, null, "Teste"));
    }

    /** Marcar com data no passado deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarComDataNoPassadoDeveLancarExcecao() throws Exception {
        service.marcar(new Consulta(paciente, medico,
            LocalDateTime.now().minusDays(1), "Teste"));
    }

    /** Marcar com data de há 1 minuto deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarComDataDeAgoraDeveLancarExcecao() throws Exception {
        service.marcar(new Consulta(paciente, medico,
            LocalDateTime.now().minusMinutes(1), ""));
    }

    // ── Cancelamento ───────────────────────────────────────────────

    /** Cancelar consulta MARCADA deve alterar estado para CANCELADA. */
    @Test
    public void cancelarConsultaMarcadaDeveAlterarEstado() throws Exception {
        Consulta c = new Consulta(1, paciente, medico,
            LocalDateTime.now().plusDays(2), Consulta.Estado.MARCADA, "");
        try {
            service.cancelar(c);
        } catch (exception.BaseDadosException e) {
            // BD não disponível nos testes unitários — estado já foi alterado
        }
        assertEquals(Consulta.Estado.CANCELADA, c.getEstado());
    }

    /** Cancelar consulta já CANCELADA deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void cancelarConsultaJaCanceladaDeveLancarExcecao() throws Exception {
        service.cancelar(new Consulta(1, paciente, medico,
            LocalDateTime.now().plusDays(2), Consulta.Estado.CANCELADA, ""));
    }

    // ── Marcar como Realizada ───────────────────────────────────────

    /** Marcar como realizada consulta MARCADA deve alterar estado. */
    @Test
    public void marcarRealizadaDeveAlterarEstado() throws Exception {
        Consulta c = new Consulta(1, paciente, medico,
            LocalDateTime.now().plusDays(1), Consulta.Estado.MARCADA, "");
        try {
            service.marcarRealizada(c);
        } catch (exception.BaseDadosException e) {
            // BD não disponível nos testes unitários — estado já foi alterado
        }
        assertEquals(Consulta.Estado.REALIZADA, c.getEstado());
    }

    /** Marcar como realizada consulta CANCELADA deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarRealizadaDeConsultaCanceladaDeveLancarExcecao() throws Exception {
        service.marcarRealizada(new Consulta(1, paciente, medico,
            LocalDateTime.now().plusDays(1), Consulta.Estado.CANCELADA, ""));
    }

    /** Marcar como realizada consulta já REALIZADA deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void marcarRealizadaDeConsultaJaRealizadaDeveLancarExcecao() throws Exception {
        service.marcarRealizada(new Consulta(1, paciente, medico,
            LocalDateTime.now().plusDays(1), Consulta.Estado.REALIZADA, ""));
    }

    // ── Interface IValidavel ────────────────────────────────────────

    /** ConsultaService deve implementar IValidavel. */
    @Test
    public void servicoDeveImplementarIValidavel() {
        assertTrue("ConsultaService deve implementar IValidavel",
            service instanceof IValidavel);
    }

    /** validar() deve rejeitar consulta sem paciente. */
    @Test(expected = ValidacaoException.class)
    public void validarDeveRejeitarConsultaSemPaciente() throws Exception {
        service.validar(new Consulta(null, medico,
            LocalDateTime.now().plusDays(1), ""));
    }
}
