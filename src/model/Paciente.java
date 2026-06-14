package model;
import java.time.LocalDate;
/**
 * Representa um Paciente da clínica. Estende {@link Pessoa}.
 */
public class Paciente extends Pessoa {
    private LocalDate dataNascimento;
    private String numeroUtente;
    public Paciente(int id, String nome, String email, String telefone, LocalDate dataNascimento, String numeroUtente) {
        super(id,nome,email,telefone); this.dataNascimento=dataNascimento; this.numeroUtente=numeroUtente;
    }
    public Paciente(String nome, String email, String telefone, LocalDate dataNascimento, String numeroUtente) {
        super(nome,email,telefone); this.dataNascimento=dataNascimento; this.numeroUtente=numeroUtente;
    }
    @Override public String getDescricao() { return getNome()+" (Utente: "+numeroUtente+")"; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate d) { this.dataNascimento=d; }
    public String getNumeroUtente() { return numeroUtente; }
    public void setNumeroUtente(String n) { this.numeroUtente=n; }
}
