package service;

import exception.ValidacaoException;
import model.Paciente;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;

/**
 * Testes unitários para {@link PacienteService}.
 * Valida as regras de negócio sem necessidade de ligação à base de dados.
 *
 * @author Clinica
 * @version 1.0
 */
public class PacienteServiceTest {

    private PacienteService service;

    /** Inicializa o serviço antes de cada teste. */
    @Before
    public void setUp() {
        service = new PacienteService();
    }

    // ── Validação: campos obrigatórios ──────────────────────────────

    /** Nome vazio deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComNomeVazioDeveLancarExcecao() throws Exception {
        service.registar(new Paciente("", "email@test.pt", "910000000",
            LocalDate.of(1990, 1, 1), "U-00001"));
    }

    /** Nome null deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComNomeNullDeveLancarExcecao() throws Exception {
        service.registar(new Paciente(null, "email@test.pt", "910000000",
            LocalDate.of(1990, 1, 1), "U-00002"));
    }

    /** Número de utente vazio deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarSemNumeroUtenteDeveLancarExcecao() throws Exception {
        service.registar(new Paciente("João Silva", "joao@email.pt", "910000001",
            LocalDate.of(1985, 3, 20), ""));
    }

    /** Número de utente null deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComNumeroUtenteNullDeveLancarExcecao() throws Exception {
        service.registar(new Paciente("Ana Costa", "ana@email.pt", "910000002",
            LocalDate.of(1992, 7, 10), null));
    }

    /** Nome com apenas espaços deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComNomeApenasEspacosDeveLancarExcecao() throws Exception {
        service.registar(new Paciente("   ", "email@test.pt", "", null, "U-00003"));
    }

    // ── Validação: mensagens de erro ────────────────────────────────

    /** Mensagem de erro para nome vazio deve ser informativa. */
    @Test
    public void mensagemErroNomeDeveSerInformativa() {
        try {
            service.registar(new Paciente("", "", "", null, "U-00004"));
            fail("Devia ter lançado ValidacaoException");
        } catch (ValidacaoException e) {
            assertNotNull("Mensagem não deve ser null", e.getMessage());
            assertFalse("Mensagem não deve estar vazia", e.getMessage().isBlank());
        } catch (Exception e) {
            fail("Tipo errado de exceção: " + e.getClass().getName());
        }
    }

    /** Mensagem de erro para utente vazio deve ser informativa. */
    @Test
    public void mensagemErroUtenteDeveSerInformativa() {
        try {
            service.registar(new Paciente("Nome Válido", "", "", null, ""));
            fail("Devia ter lançado ValidacaoException");
        } catch (ValidacaoException e) {
            assertNotNull("Mensagem não deve ser null", e.getMessage());
            assertFalse("Mensagem não deve estar vazia", e.getMessage().isBlank());
        } catch (Exception e) {
            fail("Tipo errado de exceção: " + e.getClass().getName());
        }
    }

    // ── Interface IValidavel ────────────────────────────────────────

    /** PacienteService deve implementar IValidavel. */
    @Test
    public void servicoDeveImplementarIValidavel() {
        assertTrue("PacienteService deve implementar IValidavel",
            service instanceof IValidavel);
    }

    /** validar() deve lançar exceção para paciente com nome vazio. */
    @Test(expected = ValidacaoException.class)
    public void validarDeveRejeitarPacienteComNomeVazio() throws Exception {
        service.validar(new Paciente("", "email@test.pt", "", null, "U-99"));
    }

    /** validar() não deve lançar exceção para paciente válido. */
    @Test
    public void validarDeveAceitarPacienteValido() {
        try {
            service.validar(new Paciente("Nome Correto", "email@test.pt",
                "910000000", LocalDate.of(1990, 1, 1), "U-12345"));
        } catch (ValidacaoException e) {
            fail("Não devia lançar exceção para paciente válido: " + e.getMessage());
        }
    }
}
