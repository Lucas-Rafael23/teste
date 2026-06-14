package exception;
/**
 * Exceção lançada quando ocorre um erro ao aceder à base de dados.
 */
public class BaseDadosException extends ClinicaException {
    public BaseDadosException(String mensagem, Throwable causa) { super(mensagem,causa); }
}
