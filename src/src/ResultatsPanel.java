import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ResultatsPanel extends JPanel {
    private static final Color HEADER_BG  = new Color(63, 81, 181);
    private static final Color WINNER_BG  = new Color(232, 245, 233);
    private static final Color WINNER_FG  = new Color(46, 125, 50);

    private UIController controller;
    private Fenetre parentFrame;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblWinner;

    public ResultatsPanel(UIController controller, Fenetre parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;

        // Use a BorderLayout with some spacing and a white background
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createHeader();
        createResultsTable();
        createWinnerPanel();
    }

    /**
     * Creates the top header section with the "Résultats du Tournoi" title.
     */
    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Résultats du Tournoi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Creates and styles the table where results are displayed.
     */
    private void createResultsTable() {
        // Column headers for the results
        String[] columns = {
            "Num Équipe", 
            "Joueur 1", 
            "Joueur 2", 
            "Score", 
            "Matchs gagnés", 
            "Matchs joués"
        };

        // Non-editable table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setGridColor(new Color(230, 230, 230));
        table.setIntercellSpacing(new Dimension(10, 5));

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));

        // Custom renderer to highlight the top row (winner) in green background
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                // For the last columns (Score, Matchs Gagnés, Matchs Joués), center the text
                if (column >= 3) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                // If this is the top row, highlight it as the "winner" row
                if (row == 0) {
                    // Only do this if there's actually a row 0
                    // So we highlight the best result
                    setBackground(isSelected ? new Color(200, 230, 201) : WINNER_BG);
                    setForeground(WINNER_FG);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    // Standard row coloring
                    setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    setForeground(table.getForeground());
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the bottom panel that displays the "Gagnant: ..." label, 
     * plus a trophy icon and a rounded border for a subtle accent.
     */
    private void createWinnerPanel() {
        JPanel winnerPanel = new JPanel(new BorderLayout());
        winnerPanel.setBackground(WINNER_BG);
        winnerPanel.setBorder(BorderFactory.createCompoundBorder(
            // Some top margin
            BorderFactory.createEmptyBorder(20, 0, 0, 0),
            BorderFactory.createCompoundBorder(
                new RoundedBorder(WINNER_BG, 10),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));

        lblWinner = new JLabel("Gagnant: ");
        lblWinner.setFont(new Font("Arial", Font.BOLD, 16));
        lblWinner.setForeground(WINNER_FG);

        // Attempt to load a trophy icon
        try {
            ImageIcon trophyIcon = new ImageIcon(getClass().getResource("/icons/trophy.png"));
            Image img = trophyIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            lblWinner.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Could not load trophy icon: " + e.getMessage());
        }

        winnerPanel.add(lblWinner, BorderLayout.CENTER);
        add(winnerPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes the data in the table by retrieving results from the controller,
     * then updates the winner label accordingly.
     */
    public void refreshData() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Populate new data
        List<EquipeResult> results = controller.computeResults();
        for (EquipeResult r : results) {
            tableModel.addRow(new Object[]{
                r.equipeNum, 
                r.player1, 
                r.player2, 
                r.score, 
                r.wins, 
                r.played
            });
        }

        // Display the top winner
        String topTeam = controller.getWinner();
        if (topTeam != null) {
            lblWinner.setText("Gagnant: " + topTeam);
            lblWinner.setVisible(true);
        } else {
            lblWinner.setText("Gagnant: (Aucun)");
            lblWinner.setVisible(false);
        }

        // Update the parent's status bar
        parentFrame.setStatus("Résultats du tournoi");
    }

    /**
     * Custom rounded border for the bottom "winner" panel.
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Provide padding so the round shape is clearly visible
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }
}
