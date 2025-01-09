import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * DetailsPanel class that merges enhanced styling (shadow border, color scheme,
 * GridBagLayout) with the refreshData() method from the previous version.
 */
public class DetailsPanel extends JPanel {
    // Custom colors for labels and values
    private static final Color LABEL_FG = new Color(100, 100, 100);
    private static final Color VALUE_FG = new Color(33, 33, 33);

    private UIController controller;
    private Fenetre parentFrame;

    // Data labels
    private JLabel lblNameValue;
    private JLabel lblStatutValue;
    private JLabel lblNbToursValue;

    public DetailsPanel(UIController controller, Fenetre parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;

        // Main layout for this panel
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Détails du Tournoi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel with shadow border
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create and add detail rows
        addDetailRow(contentPanel, "Nom du tournoi:",   lblNameValue   = createValueLabel(), gbc, 0);
        addDetailRow(contentPanel, "Statut:",           lblStatutValue = createValueLabel(), gbc, 1);
        addDetailRow(contentPanel, "Nombre de tours:",  lblNbToursValue= createValueLabel(), gbc, 2);

        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to add a row in the content panel with a label and a corresponding value label.
     */
    private void addDetailRow(JPanel panel, String labelText, JLabel valueLabel,
                              GridBagConstraints gbc, int row) {
        gbc.gridy = row;

        // Label cell
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(LABEL_FG);
        panel.add(label, gbc);

        // Value cell
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(valueLabel, gbc);
    }

    /**
     * Creates a JLabel for displaying value text (e.g., the name, status, or tours).
     */
    private JLabel createValueLabel() {
        JLabel label = new JLabel("...");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(VALUE_FG);
        return label;
    }

    /**
     * Refreshes the data shown in the panel based on the currently selected tournament.
     */
    public void refreshData() {
        if (controller.getCurrentTournoi() != null) {
            Tournoi t = controller.getCurrentTournoi();
            lblNameValue.setText(t.getNom());
            lblStatutValue.setText(t.getNStatut());
            lblNbToursValue.setText(String.valueOf(t.getNbTours()));
            parentFrame.setStatus("Paramètres du tournoi: " + t.getNom());
        } else {
            lblNameValue.setText("...");
            lblStatutValue.setText("...");
            lblNbToursValue.setText("0");
        }
    }

    /**
     * A simple custom border to simulate a soft "shadow" around the panel.
     */
    private static class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 20));
            for (int i = 0; i < 3; i++) {
                g2.drawRoundRect(
                    x + i, y + i,
                    width - 2*i - 1,
                    height - 2*i - 1,
                    10, 10
                );
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Provide a small margin around the panel
            return new Insets(4, 4, 4, 4);
        }
    }
}
