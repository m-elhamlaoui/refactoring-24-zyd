import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MatchsPanel extends JPanel {
    private static final Color TABLE_HEADER_BG     = new Color(63, 81, 181);
    private static final Color TABLE_HEADER_FG     = Color.WHITE;
    private static final Color TABLE_SELECTION_BG  = new Color(232, 240, 254);

    private UIController controller;
    private Fenetre parentFrame;
    private JTable table;
    private MatchTableModel tableModel;
    private JLabel lblStatus;

    public MatchsPanel(UIController controller, Fenetre parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;

        // Overall layout & styling
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header (title)
        createHeader();

        // Table of matches
        createTable();

        // Status label at the bottom
        createStatusPanel();
    }

    /**
     * Creates a title label at the top of the panel.
     */
    private void createHeader() {
        JLabel titleLabel = new JLabel("Matchs du Tournoi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
    }

    /**
     * Sets up the matches table with styling, a custom renderer, etc.
     */
    private void createTable() {
        tableModel = new MatchTableModel(controller);
        table = new JTable(tableModel);

        // Table appearance
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setGridColor(new Color(230, 230, 230));
        table.setIntercellSpacing(new Dimension(10, 5));

        // Header appearance
        table.getTableHeader().setBackground(TABLE_HEADER_BG);
        table.getTableHeader().setForeground(TABLE_HEADER_FG);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Custom cell renderer for all columns
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, 
                    int row, int column) {
                
                // Let the superclass do default rendering
                Component c = super.getTableCellRendererComponent(table, value, 
                                            isSelected, hasFocus, row, column);
                
                // Center-align the score columns
                if (column == 3 || column == 4) {
                    setHorizontalAlignment(JLabel.CENTER);
                    // Light green-ish background if there's a score
                    if (value != null && !value.toString().isEmpty()) {
                        setBackground(new Color(232, 245, 233));
                    }
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                return c;
            }
        });

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the panel at the bottom containing the status label (e.g., "0/0 matchs terminés").
     */
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);

        lblStatus = new JLabel("0/0 matchs terminés");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(new Color(100, 100, 100));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        statusPanel.add(lblStatus);
        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes the match data and updates the status label, as well as the parent frame status.
     */
    public void refreshData() {
        tableModel.refreshData();
        lblStatus.setText(controller.getMatchStatus());
        parentFrame.setStatus("Gestion des Matchs");
    }

    /**
     * Inner class: the table model that defines the columns, data retrieval, and editing logic.
     */
    private static class MatchTableModel extends AbstractTableModel {
        private UIController controller;
        private java.util.List<Match> matches;

        private String[] columns = { "Tour", "Équipe 1", "Équipe 2", "Score 1", "Score 2" };

        public MatchTableModel(UIController controller) {
            this.controller = controller;
            refreshData();
        }

        public void refreshData() {
            matches = controller.getMatchesOfCurrentTournoi();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return (matches == null) ? 0 : matches.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Match m = matches.get(rowIndex);
            switch (columnIndex) {
                case 0: return m.getTournamentNumber();
                case 1: return m.getTeam1();
                case 2: return m.getTeam2();
                case 3: return m.getScore1();
                case 4: return m.getScore2();
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Only allow editing of score columns
            return (columnIndex == 3 || columnIndex == 4);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Match m = matches.get(rowIndex);
            try {
                int score = Integer.parseInt(aValue.toString());
                if (columnIndex == 3) {
                    m.setScore1(score);
                } else if (columnIndex == 4) {
                    m.setScore2(score);
                }
                // Update the match in DB
                controller.updateMatch(m);
                fireTableDataChanged();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Entrez un score numérique valide.");
            }
        }
    }
}
