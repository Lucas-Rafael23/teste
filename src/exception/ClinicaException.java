package exception;
/**
 * Exceção base do sistema da Clínica.
 */
public class ClinicaException extends Exception {
    public ClinicaException(String mensagem) { super(mensagem); }
    public ClinicaException(String mensagem, Throwable causa) { super(mensagem,causa); }
}
