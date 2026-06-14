package ui;

import exception.ClinicaException;
import model.*;
import service.ConsultaService;
import service.MedicoService;
import service.PacienteService;
import service.ServiceFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Painel de gestão de Consultas.
 * Implementa {@link DataChangeListener} para atualizar automaticamente
 * quando os dados são alterados noutro painel (padrão Observer).
 *
 * <p>Obtém os serviços através de {@link ServiceFactory} (padrão Factory).
 * Quando um paciente ou médico é alterado noutro painel, este painel
 * atualiza-se automaticamente via Observer.</p>
 *
 */
public class ConsultaPanel extends JPanel implements DataChangeListener {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private static final Color COR_MARCADA   = new Color(210, 230, 255);
    private static final Color COR_REALIZADA = new Color(200, 240, 210);
    private static final Color COR_CANCELADA = new Color(255, 210, 210);
    private static final Color COR_SELECAO   = new Color(150, 190, 255);

    /** Serviços obtidos via ServiceFactory (Factory Pattern). */
    private final ConsultaService consultaService = ServiceFactory.getInstance().criarConsultaService();
    private final PacienteService pacienteService = ServiceFactory.getInstance().criarPacienteService();
    private final MedicoService   medicoService   = ServiceFactory.getInstance().criarMedicoService();

    private final String[] COLUNAS = {"ID", "Paciente", "Médico", "Data/Hora", "Estado", "Notas"};
    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private boolean pintarAtivo = false;
    private final JButton btnPintar = new JButton("🎨 Pintar Linhas");

    /**
     * Construtor — inicializa o painel e regista-se como ouvinte do Observer.
     */
    public ConsultaPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construir();
        carregarDados();

        // Registar no Observer para receber notificações automáticas
        DataChangeNotifier.getInstance().registar(this);
    }

    /**
     * {@inheritDoc}
     * Recarrega os dados quando consultas, pacientes ou médicos são alterados.
     * O painel de consultas depende dos três tipos de entidade.
     *
     * @param tipo tipo de entidade alterada
     */
    @Override
    public void onDadosAlterados(TipoAlteracao tipo) {
        // Atualiza para qualquer tipo de alteração, pois consultas referem pacientes e médicos
        carregarDados();
    }

    /** Constrói os componentes visuais do painel. */
    private void construir() {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(4).setMaxWidth(100);
        JScrollPane scroll = new JScrollPane(tabela);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnMarcar    = new JButton("📅 Marcar Consulta");
        JButton btnRealizada = new JButton("✅ Marcar Realizada");
        JButton btnCancelar  = new JButton("❌ Cancelar");
        JButton btnAtualizar = new JButton("🔄 Atualizar");
        
        btnPintar.setBackground(new Color(240, 240, 255));
        btnPintar.setFocusPainted(false);

        painelBotoes.add(btnPintar);
        painelBotoes.add(Box.createHorizontalStrut(8));
        painelBotoes.add(btnMarcar);
        painelBotoes.add(btnRealizada);
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnAtualizar);

        btnPintar.addActionListener(e -> togglePintar());
        btnMarcar.addActionListener(e -> abrirFormulario());
        btnRealizada.addActionListener(e -> marcarRealizada());
        btnCancelar.addActionListener(e -> cancelarSelecionada());
        btnAtualizar.addActionListener(e -> carregarDados());

        JLabel titulo = new JLabel("  Lista de Consultas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.WEST);
        topo.add(painelBotoes, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(criarLegenda(), BorderLayout.SOUTH);
    }

        /**
     * Cria o painel de legenda das cores.
     *
     * @return painel com os indicadores de cor
     */
    private JPanel criarLegenda() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        legenda.setBorder(BorderFactory.createTitledBorder("Legenda de cores"));
        legenda.add(indicadorCor(COR_MARCADA,   "Marcada"));
        legenda.add(indicadorCor(COR_REALIZADA, "Realizada"));
        legenda.add(indicadorCor(COR_CANCELADA, "Cancelada"));
        return legenda;
    }
    
    /**
     * Cria um indicador visual de cor com etiqueta.
     *
     * @param cor   cor a mostrar
     * @param label texto da etiqueta
     * @return painel com o indicador
     */
    private JPanel indicadorCor(Color cor, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel quadrado = new JLabel("    ");
        quadrado.setOpaque(true);
        quadrado.setBackground(cor);
        quadrado.setBorder(BorderFactory.createLineBorder(cor.darker(), 1));
        quadrado.setPreferredSize(new Dimension(18, 14));
        p.add(quadrado);
        p.add(new JLabel(label));
        return p;
    }
    
    /** Recarrega os dados da base de dados para a tabela. */
    private void carregarDados() {
        modelo.setRowCount(0);
        try {
            for (Consulta c : consultaService.listar()) {
                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getPaciente().getNome(),
                    c.getMedico().getNome(),
                    c.getDataHora().format(FMT),
                    c.getEstado().name(),
                    c.getNotas() != null ? c.getNotas() : ""
                });
            }       
            if (pintarAtivo) tabela.repaint();
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Ativa ou desativa a coloracao das linhas por estado.
     * Aplica o EstadoRenderer quando ativo e repoe o renderer padrao quando inativo.
     */
    private void togglePintar() {
        pintarAtivo = !pintarAtivo;

        if (pintarAtivo) {
            tabela.setDefaultRenderer(Object.class, new EstadoRenderer());
            btnPintar.setText("🎨 Remover Cores");
            btnPintar.setBackground(new Color(255, 235, 235));
        } else {
            tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
            btnPintar.setText("🎨 Pintar Linhas");
            btnPintar.setBackground(new Color(240, 240, 255));
        }

        tabela.repaint();
    }
    
    /**
     * Renderer personalizado para que colore cada linha consoante o estado da consulta.
     */
    private class EstadoRenderer extends DefaultTableCellRenderer {

        /**
         * {@inheritDoc}
         * Define a cor de fundo da celula com base no estado da linha correspondente.
         */
        @Override
        public Component getTableCellRendererComponent(
                JTable tabela, Object valor,
                boolean estaSelecionado, boolean temFoco,
                int linha, int coluna) {

            Component c = super.getTableCellRendererComponent(
                    tabela, valor, estaSelecionado, temFoco, linha, coluna);

            if (estaSelecionado) {
                c.setBackground(COR_SELECAO);
                c.setForeground(Color.BLACK);
            } else {
                String estado = (String) modelo.getValueAt(linha, 4);
                switch (estado) {
                    case "MARCADA":   c.setBackground(COR_MARCADA);   break;
                    case "REALIZADA": c.setBackground(COR_REALIZADA); break;
                    case "CANCELADA": c.setBackground(COR_CANCELADA); break;
                    default:          c.setBackground(Color.WHITE);   break;
                }
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }

    /** Abre o formulário para marcar uma nova consulta. */
    private void abrirFormulario() {
        List<Paciente> pacientes;
        List<Medico>   medicos;
        try {
            pacientes = pacienteService.listar();
            medicos   = medicoService.listar();
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage());
            return;
        }

        if (pacientes.isEmpty() || medicos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "É necessário ter pelo menos um paciente e um médico registados.");
            return;
        }

        JComboBox<Paciente> cmbPaciente = new JComboBox<>(pacientes.toArray(Paciente[]::new));
        JComboBox<Medico>   cmbMedico   = new JComboBox<>(medicos.toArray(Medico[]::new));
        JTextField txtData = new JTextField(
            LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).format(FMT), 16);
        JTextArea txtNotas = new JTextArea(3, 20);
        txtNotas.setLineWrap(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Paciente *:"));                      form.add(cmbPaciente);
        form.add(new JLabel("Médico *:"));                        form.add(cmbMedico);
        form.add(new JLabel("Data/Hora * (dd/MM/yyyy HH:mm):")); form.add(txtData);
        form.add(new JLabel("Notas:"));                          form.add(new JScrollPane(txtNotas));

        int res = JOptionPane.showConfirmDialog(this, form, "Marcar Consulta",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            LocalDateTime dataHora;
            try {
                dataHora = LocalDateTime.parse(txtData.getText().trim(), FMT);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                    "Formato de data inválido. Use dd/MM/yyyy HH:mm", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            consultaService.marcar(new Consulta(
                (Paciente) cmbPaciente.getSelectedItem(),
                (Medico)   cmbMedico.getSelectedItem(),
                dataHora, txtNotas.getText().trim()
            ));

        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Marca a consulta selecionada como realizada. */
    private void marcarRealizada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione uma consulta."); return; }
        try {
            int id = (int) modelo.getValueAt(linha, 0);
            consultaService.listar().stream().filter(x -> x.getId() == id)
                .findFirst().ifPresent(c -> {
                    try { consultaService.marcarRealizada(c); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                });
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Cancela a consulta selecionada após confirmação. */
    private void cancelarSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione uma consulta."); return; }

        int confirmar = JOptionPane.showConfirmDialog(this, "Cancelar esta consulta?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmar != JOptionPane.YES_OPTION) return;

        try {
            int id = (int) modelo.getValueAt(linha, 0);
            consultaService.listar().stream().filter(x -> x.getId() == id)
                .findFirst().ifPresent(c -> {
                    try { consultaService.cancelar(c); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                });
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}
