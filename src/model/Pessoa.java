package model;
/**
 * Classe abstrata que representa uma Pessoa no sistema.
 * Serve de base para {@link Paciente} e {@link Medico}.
 */
public abstract class Pessoa {
    private int id;
    private String nome;
    private String email;
    private String telefone;
    public Pessoa(int id, String nome, String email, String telefone) { this.id=id; this.nome=nome; this.email=email; this.telefone=telefone; }
    public Pessoa(String nome, String email, String telefone) { this(0,nome,email,telefone); }
    /** Retorna uma descrição resumida da pessoa. */
    public abstract String getDescricao();
    public int getId() { return id; } public void setId(int id) { this.id=id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome=nome; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email=email; }
    public String getTelefone() { return telefone; } public void setTelefone(String t) { this.telefone=t; }
    @Override public String toString() { return nome; }
}
