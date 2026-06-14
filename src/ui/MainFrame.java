package ui;
import javax.swing.*;
import java.awt.*;
/**
 * Janela principal da aplicação com abas para Pacientes, Médicos e Consultas.
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        super("Sistema de Gestão de Clínica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950,650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800,550));
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(41,128,185));
        topo.setBorder(BorderFactory.createEmptyBorder(12,20,12,20));
        JLabel titulo = new JLabel("Clínica — Sistema de Gestão");
        titulo.setFont(new Font("Segoe UI",Font.BOLD,18));
        titulo.setForeground(Color.WHITE);
        topo.add(titulo, BorderLayout.WEST);
        add(topo, BorderLayout.NORTH);
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI",Font.PLAIN,13));
        abas.addTab("Pacientes",  new PacientePanel());
        abas.addTab("Médicos",    new MedicoPanel());
        abas.addTab("Consultas",  new ConsultaPanel());
        add(abas, BorderLayout.CENTER);
    }
}
