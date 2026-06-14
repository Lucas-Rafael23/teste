package exception;
/**
 * Exceção lançada quando os dados introduzidos são inválidos.
 */
public class ValidacaoException extends ClinicaException {
    public ValidacaoException(String mensagem) { super(mensagem); }
}
