package ui;

import exception.ClinicaException;
import model.Paciente;
import service.PacienteService;
import service.ServiceFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Painel de gestão de Pacientes.
 * Implementa {@link DataChangeListener} para atualizar automaticamente
 * quando os dados são alterados noutro painel (padrão Observer).
 *
 * <p>Obtém o serviço através de {@link ServiceFactory} (padrão Factory).</p>
 *
 */
public class PacientePanel extends JPanel implements DataChangeListener {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Serviço obtido via ServiceFactory (Factory Pattern). */
    private final PacienteService service = ServiceFactory.getInstance().criarPacienteService();

    private final String[] COLUNAS = {"ID", "Nome", "Nº Utente", "Telefone", "Email", "Data Nasc."};
    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable     tabela  = new JTable(modelo);
    private final JTextField txtPesq = new JTextField(20);

    /**
     * Construtor — inicializa o painel e regista-se como ouvinte do Observer.
     */
    public PacientePanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construir();
        carregarDados(null);

        // Registar no Observer para receber notificações automáticas
        DataChangeNotifier.getInstance().registar(this);
    }

    /**
     * {@inheritDoc}
     * Recarrega os dados quando pacientes ou consultas são alterados.
     *
     * @param tipo tipo de entidade alterada
     */
    @Override
    public void onDadosAlterados(TipoAlteracao tipo) {
        if (tipo == TipoAlteracao.PACIENTE || tipo == TipoAlteracao.CONSULTA) {
            carregarDados(null);
        }
    }

    /** Constrói os componentes visuais do painel. */
    private void construir() {
        JPanel painelPesq = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelPesq.add(new JLabel("Pesquisar:"));
        painelPesq.add(txtPesq);
        JButton btnPesq   = new JButton("🔍 Pesquisar");
        JButton btnLimpar = new JButton("✖ Limpar");
        painelPesq.add(btnPesq);
        painelPesq.add(btnLimpar);

        btnPesq.addActionListener(e -> carregarDados(txtPesq.getText().trim()));
        btnLimpar.addActionListener(e -> { txtPesq.setText(""); carregarDados(null); });

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scroll = new JScrollPane(tabela);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo      = new JButton("➕ Novo");
        JButton btnEditar    = new JButton("✏️ Editar");
        JButton btnRemover   = new JButton("🗑 Remover");
        JButton btnAtualizar = new JButton("🔄 Atualizar");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnAtualizar);

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnRemover.addActionListener(e -> removerSelecionado());
        btnAtualizar.addActionListener(e -> carregarDados(null));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(painelPesq, BorderLayout.WEST);
        topo.add(painelBotoes, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Carrega (ou filtra) os dados na tabela.
     *
     * @param termo texto de pesquisa, ou {@code null} para listar todos
     */
    private void carregarDados(String termo) {
        modelo.setRowCount(0);
        try {
            List<Paciente> lista = (termo == null || termo.isEmpty())
                ? service.listar() : service.pesquisar(termo);

            for (Paciente p : lista) {
                String dataNasc = (p.getDataNascimento() != null)
                    ? p.getDataNascimento().format(FMT) : "";
                modelo.addRow(new Object[]{
                    p.getId(), p.getNome(), p.getNumeroUtente(),
                    p.getTelefone(), p.getEmail(), dataNasc
                });
            }
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Abre o formulário para criar ou editar um paciente. */
    private void abrirFormulario(Paciente paciente) {
        JTextField txtNome     = new JTextField(paciente != null ? paciente.getNome() : "", 20);
        JTextField txtUtente   = new JTextField(paciente != null ? paciente.getNumeroUtente() : "", 15);
        JTextField txtTel      = new JTextField(paciente != null ? paciente.getTelefone() : "", 15);
        JTextField txtEmail    = new JTextField(paciente != null ? paciente.getEmail() : "", 20);
        JTextField txtDataNasc = new JTextField(
            (paciente != null && paciente.getDataNascimento() != null)
                ? paciente.getDataNascimento().format(FMT) : "", 12);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nome *:"));               form.add(txtNome);
        form.add(new JLabel("Nº Utente *:"));          form.add(txtUtente);
        form.add(new JLabel("Telefone:"));             form.add(txtTel);
        form.add(new JLabel("Email:"));                form.add(txtEmail);
        form.add(new JLabel("Data Nasc. (dd/MM/yyyy):")); form.add(txtDataNasc);

        String titulo = (paciente == null) ? "Novo Paciente" : "Editar Paciente";
        int res = JOptionPane.showConfirmDialog(this, form, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            LocalDate dataNasc = null;
            if (!txtDataNasc.getText().isBlank()) {
                try {
                    dataNasc = LocalDate.parse(txtDataNasc.getText().trim(), FMT);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Formato de data inválido. Use dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (paciente == null) {
                service.registar(new Paciente(
                    txtNome.getText().trim(), txtEmail.getText().trim(),
                    txtTel.getText().trim(), dataNasc, txtUtente.getText().trim()));
            } else {
                paciente.setNome(txtNome.getText().trim());
                paciente.setEmail(txtEmail.getText().trim());
                paciente.setTelefone(txtTel.getText().trim());
                paciente.setDataNascimento(dataNasc);
                paciente.setNumeroUtente(txtUtente.getText().trim());
                service.atualizar(paciente);
            }

        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Edita o paciente da linha selecionada. */
    private void editarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um paciente para editar."); return; }
        try {
            int id = (int) modelo.getValueAt(linha, 0);
            Paciente p = service.listar().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
            abrirFormulario(p);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Remove o paciente selecionado após confirmação. */
    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um paciente para remover."); return; }

        int    id   = (int)    modelo.getValueAt(linha, 0);
        String nome = (String) modelo.getValueAt(linha, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem a certeza que deseja remover o paciente \"" + nome + "\"?\n" +
            "As suas consultas também serão removidas.",
            "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try { service.remover(id); }
            catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
