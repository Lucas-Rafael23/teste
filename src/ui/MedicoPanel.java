package ui;

import dao.DAOFactory;
import dao.EspecialidadeDAO;
import exception.BaseDadosException;
import exception.ClinicaException;
import model.Especialidade;
import model.Medico;
import service.MedicoService;
import service.ServiceFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel de gestão de Médicos.
 * Implementa {@link DataChangeListener} para atualizar automaticamente
 * quando os dados são alterados noutro painel (padrão Observer).
 *
 * <p>Obtém o serviço através de {@link ServiceFactory} (padrão Factory).</p>
 */
public class MedicoPanel extends JPanel implements DataChangeListener {

    /** Serviço obtido via ServiceFactory (Factory Pattern). */
    private final MedicoService    service = ServiceFactory.getInstance().criarMedicoService();

    /** DAO de especialidades obtido via DAOFactory (Factory Pattern). */
    private final EspecialidadeDAO espDAO  = DAOFactory.getInstance().criarEspecialidadeDAO();

    private final String[] COLUNAS = {"ID", "Nome", "Cédula", "Especialidade", "Telefone", "Email"};
    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    /**
     * Construtor — inicializa o painel e regista-se como ouvinte do Observer.
     */
    public MedicoPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construir();
        carregarDados();

        // Registar no Observer para receber notificações automáticas
        DataChangeNotifier.getInstance().registar(this);
    }

    /**
     * {@inheritDoc}
     * Recarrega os dados quando médicos são alterados.
     *
     * @param tipo tipo de entidade alterada
     */
    @Override
    public void onDadosAlterados(TipoAlteracao tipo) {
        if (tipo == TipoAlteracao.MEDICO) {
            carregarDados();
        }
    }

    /** Constrói os componentes visuais do painel. */
    private void construir() {
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
        btnAtualizar.addActionListener(e -> carregarDados());

        JLabel titulo = new JLabel("  Lista de Médicos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.WEST);
        topo.add(painelBotoes, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /** Recarrega os dados da base de dados para a tabela. */
    private void carregarDados() {
        modelo.setRowCount(0);
        try {
            for (Medico m : service.listar()) {
                String esp = (m.getEspecialidade() != null) ? m.getEspecialidade().getNome() : "";
                modelo.addRow(new Object[]{
                    m.getId(), m.getNome(), m.getNumeroCedula(),
                    esp, m.getTelefone(), m.getEmail()
                });
            }
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Abre o formulário de criação ou edição de médico. */
    private void abrirFormulario(Medico medico) {
        List<Especialidade> especialidades;
        try {
            especialidades = espDAO.listarTodas();
        } catch (BaseDadosException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar especialidades: " + ex.getMessage());
            return;
        }

        JTextField txtNome   = new JTextField(medico != null ? medico.getNome() : "", 20);
        JTextField txtCedula = new JTextField(medico != null ? medico.getNumeroCedula() : "", 15);
        JTextField txtTel    = new JTextField(medico != null ? medico.getTelefone() : "", 15);
        JTextField txtEmail  = new JTextField(medico != null ? medico.getEmail() : "", 20);

        JComboBox<Especialidade> cmbEsp = new JComboBox<>();
        cmbEsp.addItem(new Especialidade(0, "-- Sem Especialidade --"));
        for (Especialidade e : especialidades) cmbEsp.addItem(e);

        if (medico != null && medico.getEspecialidade() != null) {
            for (int i = 0; i < cmbEsp.getItemCount(); i++) {
                if (cmbEsp.getItemAt(i).getId() == medico.getEspecialidade().getId()) {
                    cmbEsp.setSelectedIndex(i); break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nome *:"));        form.add(txtNome);
        form.add(new JLabel("Nº Cédula *:"));   form.add(txtCedula);
        form.add(new JLabel("Especialidade:")); form.add(cmbEsp);
        form.add(new JLabel("Telefone:"));      form.add(txtTel);
        form.add(new JLabel("Email:"));         form.add(txtEmail);

        String titulo = (medico == null) ? "Novo Médico" : "Editar Médico";
        int res = JOptionPane.showConfirmDialog(this, form, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Especialidade espSel = (Especialidade) cmbEsp.getSelectedItem();
            if (espSel != null && espSel.getId() == 0) espSel = null;

            if (medico == null) {
                service.registar(new Medico(
                    txtNome.getText().trim(), txtEmail.getText().trim(),
                    txtTel.getText().trim(), txtCedula.getText().trim(), espSel));
            } else {
                medico.setNome(txtNome.getText().trim());
                medico.setEmail(txtEmail.getText().trim());
                medico.setTelefone(txtTel.getText().trim());
                medico.setNumeroCedula(txtCedula.getText().trim());
                medico.setEspecialidade(espSel);
                service.atualizar(medico);
            }

        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Edita o médico da linha selecionada. */
    private void editarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um médico para editar."); return; }
        try {
            int id = (int) modelo.getValueAt(linha, 0);
            Medico m = service.listar().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
            abrirFormulario(m);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Remove o médico selecionado após confirmação. */
    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um médico para remover."); return; }

        int    id   = (int)    modelo.getValueAt(linha, 0);
        String nome = (String) modelo.getValueAt(linha, 1);

        int confirmar = JOptionPane.showConfirmDialog(this, "Remover o médico \"" + nome + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            try { service.remover(id); }
            catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
