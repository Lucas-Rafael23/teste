import ui.MainFrame;
import javax.swing.*;
/**
 * Ponto de entrada da aplicação.
 */
public class Main {
    /**
     * Método principal.
     * @param args argumentos (não utilizados)
     */
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
    