package service;

import exception.ValidacaoException;
import model.Especialidade;
import model.Medico;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Testes unitários para {@link MedicoService}.
 * Valida as regras de negócio sem necessidade de ligação à base de dados.
 *
 * @author Clinica
 * @version 1.0
 */
public class MedicoServiceTest {

    private MedicoService service;
    private Especialidade especialidade;

    /** Inicializa o serviço e dados antes de cada teste. */
    @Before
    public void setUp() {
        service       = new MedicoService();
        especialidade = new Especialidade(1, "Pediatria");
    }

    // ── Validação: campos obrigatórios ──────────────────────────────

    /** Nome vazio deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComNomeVazioDeveLancarExcecao() throws Exception {
        service.registar(new Medico("", "medico@clinica.pt", "253000001",
            "C-10001", especialidade));
    }

    /** Nome null deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComNomeNullDeveLancarExcecao() throws Exception {
        service.registar(new Medico(null, "medico@clinica.pt", "253000002",
            "C-10002", especialidade));
    }

    /** Cédula vazia deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarSemCedulaDeveLancarExcecao() throws Exception {
        service.registar(new Medico("Dra. Maria Santos", "maria@clinica.pt",
            "253000003", "", especialidade));
    }

    /** Cédula null deve lançar ValidacaoException. */
    @Test(expected = ValidacaoException.class)
    public void registarComCedulaNullDeveLancarExcecao() throws Exception {
        service.registar(new Medico("Dr. João Ferreira", "joao@clinica.pt",
            "253000004", null, especialidade));
    }

    /** Especialidade é opcional — não deve lançar ValidacaoException. */
    @Test
    public void registarSemEspecialidadeNaoDeveErrarNaValidacao() {
        try {
            service.registar(new Medico("Dr. Rui Pereira", "rui@clinica.pt",
                "253000005", "C-10005", null));
        } catch (ValidacaoException e) {
            fail("Especialidade é opcional — não devia lançar: " + e.getMessage());
        } catch (Exception e) {
            // BaseDadosException é aceitável sem BD nos testes unitários
        }
    }

    // ── Validação: mensagens de erro ────────────────────────────────

    /** Mensagem de erro para nome vazio deve ser informativa. */
    @Test
    public void mensagemErroNomeDeveSerInformativa() {
        try {
            service.registar(new Medico("", "", "", "C-TESTE", null));
            fail("Devia ter lançado ValidacaoException");
        } catch (ValidacaoException e) {
            assertNotNull(e.getMessage());
            assertFalse(e.getMessage().isBlank());
        } catch (Exception e) {
            fail("Tipo errado: " + e.getClass().getName());
        }
    }

    // ── Interface IValidavel ────────────────────────────────────────

    /** MedicoService deve implementar IValidavel. */
    @Test
    public void servicoDeveImplementarIValidavel() {
        assertTrue("MedicoService deve implementar IValidavel",
            service instanceof IValidavel);
    }

    /** validar() deve rejeitar médico com cédula vazia. */
    @Test(expected = ValidacaoException.class)
    public void validarDeveRejeitarMedicoComCedulaVazia() throws Exception {
        service.validar(new Medico("Dr. Teste", "", "", "", null));
    }

    /** validar() deve aceitar médico com dados válidos. */
    @Test
    public void validarDeveAceitarMedicoValido() {
        try {
            service.validar(new Medico("Dr. Válido", "valido@clinica.pt",
                "253000099", "C-99999", especialidade));
        } catch (ValidacaoException e) {
            fail("Não devia lançar exceção para médico válido: " + e.getMessage());
        }
    }
}
