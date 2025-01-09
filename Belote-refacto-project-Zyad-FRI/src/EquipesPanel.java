import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EquipesPanel extends JPanel {
    private static final Color BUTTON_BG = new Color(63, 81, 181);
    private static final Color BUTTON_FG = Color.WHITE;
    
    private UIController controller;
    private Fenetre parentFrame;
    private JTable table;
    private EquipeTableModel tableModel;
    private JButton btnAdd, btnRemove, btnValidate;

    public EquipesPanel(UIController controller, Fenetre parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Gestion des Équipes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        // Initialize table with custom styling
        tableModel = new EquipeTableModel(controller);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setGridColor(new Color(230, 230, 230));

        // Customize table header
        table.getTableHeader().setBackground(new Color(63, 81, 181));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnAdd = createStyledButton("Ajouter Équipe", "/icons/add_team.png");
        btnRemove = createStyledButton("Supprimer Équipe", "/icons/remove_team.png");
        btnValidate = createStyledButton("Valider Équipes", "/icons/validate.png");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnValidate);

        // Wire up button actions
        btnAdd.addActionListener(e -> addEquipe());
        btnRemove.addActionListener(e -> removeEquipe());
        btnValidate.addActionListener(e -> validateEquipes());

        return buttonPanel;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(48, 63, 159));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BG);
            }
        });

        return button;
    }

    private void addEquipe() {
        String j1 = JOptionPane.showInputDialog(this, "Nom du joueur 1?");
        String j2 = JOptionPane.showInputDialog(this, "Nom du joueur 2?");
        if (j1 != null && j2 != null && !j1.isEmpty() && !j2.isEmpty()) {
            controller.addEquipe(j1, j2);
            tableModel.refreshData();
        }
    }

    private void removeEquipe() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            controller.removeEquipe(row);
            tableModel.refreshData();
        }
    }

    private void validateEquipes() {
        controller.validateEquipes();
        parentFrame.updateButtons();
        parentFrame.setStatus("Matchs générés, statut tournoi mis à jour");
    }

    public void refreshData() {
        tableModel.refreshData();
        parentFrame.setStatus("Gestion des équipes");
    }

    // TableModel for Equipes
    private static class EquipeTableModel extends AbstractTableModel {
        private UIController controller;
        private java.util.List<Equipe> equipes;

        private String[] cols = {"Numéro", "Joueur 1", "Joueur 2"};

        public EquipeTableModel(UIController controller) {
            this.controller = controller;
            refreshData();
        }

        public void refreshData() {
            equipes = controller.getEquipesOfCurrentTournoi();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return equipes != null ? equipes.size() : 0;
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Equipe e = equipes.get(rowIndex);
            switch(columnIndex) {
                case 0: return e.getNum();
                case 1: return e.getPlayer1();
                case 2: return e.getPlayer2();
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 1 || columnIndex == 2);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Equipe e = equipes.get(rowIndex);
            if (columnIndex == 1) {
                e.setPlayer1(aValue.toString());
            } else if (columnIndex == 2) {
                e.setPlayer2(aValue.toString());
            }
            controller.updateEquipe(e);
            fireTableDataChanged();
        }
    }
}
