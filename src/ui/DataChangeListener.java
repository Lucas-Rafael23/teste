package ui;

/**
 * Interface do padrão Observer para notificação de alterações de dados.
 *
 * <p>Padrão utilizado: <b>Observer Pattern</b> — permite que os painéis
 * da interface gráfica sejam notificados automaticamente quando os dados
 * são alterados noutro painel, mantendo a UI sempre sincronizada.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>
 *   // O painel regista-se como ouvinte
 *   DataChangeNotifier.getInstance().registar(this);
 *
 *   // Quando os dados mudam, este método é chamado automaticamente
 *   {@literal @}Override
 *   public void onDadosAlterados(TipoAlteracao tipo) {
 *       if (tipo == TipoAlteracao.PACIENTE) carregarDados();
 *   }
 * </pre>
 *
 */
public interface DataChangeListener {

    /**
     * Enumeração dos tipos de alteração possíveis no sistema.
     */
    enum TipoAlteracao {
        /** Dados de pacientes foram alterados. */
        PACIENTE,
        /** Dados de médicos foram alterados. */
        MEDICO,
        /** Dados de consultas foram alterados. */
        CONSULTA
    }

    /**
     * Chamado automaticamente quando os dados do sistema são alterados.
     *
     * @param tipo tipo de entidade que foi alterada
     */
    void onDadosAlterados(TipoAlteracao tipo);
}
