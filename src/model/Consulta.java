package model;
import java.time.LocalDateTime;
/**
 * Representa uma Consulta médica agendada na clínica.
 */
public class Consulta {
    public enum Estado { MARCADA, REALIZADA, CANCELADA }
    private int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDateTime dataHora;
    private Estado estado;
    private String notas;
    public Consulta(int id, Paciente paciente, Medico medico, LocalDateTime dataHora, Estado estado, String notas) {
        this.id=id; this.paciente=paciente; this.medico=medico; this.dataHora=dataHora; this.estado=estado; this.notas=notas;
    }
    public Consulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String notas) {
        this(0,paciente,medico,dataHora,Estado.MARCADA,notas);
    }
    public int getId() { return id; } public void setId(int id) { this.id=id; }
    public Paciente getPaciente() { return paciente; } public void setPaciente(Paciente p) { this.paciente=p; }
    public Medico getMedico() { return medico; } public void setMedico(Medico m) { this.medico=m; }
    public LocalDateTime getDataHora() { return dataHora; } public void setDataHora(LocalDateTime d) { this.dataHora=d; }
    public Estado getEstado() { return estado; } public void setEstado(Estado e) { this.estado=e; }
    public String getNotas() { return notas; } public void setNotas(String n) { this.notas=n; }
}
