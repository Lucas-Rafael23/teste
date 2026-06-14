package model;
/**
 * Representa um Médico da clínica. Estende {@link Pessoa}.
 */
public class Medico extends Pessoa {
    private String numeroCedula;
    private Especialidade especialidade;
    public Medico(int id, String nome, String email, String telefone, String numeroCedula, Especialidade especialidade) {
        super(id,nome,email,telefone); this.numeroCedula=numeroCedula; this.especialidade=especialidade;
    }
    public Medico(String nome, String email, String telefone, String numeroCedula, Especialidade especialidade) {
        super(nome,email,telefone); this.numeroCedula=numeroCedula; this.especialidade=especialidade;
    }
    @Override public String getDescricao() {
        String esp=(especialidade!=null)?especialidade.getNome():"Sem especialidade";
        return getNome()+" — "+esp;
    }
    public String getNumeroCedula() { return numeroCedula; } public void setNumeroCedula(String n) { this.numeroCedula=n; }
    public Especialidade getEspecialidade() { return especialidade; } public void setEspecialidade(Especialidade e) { this.especialidade=e; }
}
