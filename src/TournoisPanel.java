 // <-- Adjust to your own package

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TournoisPanel extends JPanel {
    private static final Color BUTTON_BG = new Color(63, 81, 181);
    private static final Color BUTTON_FG = Color.WHITE;

    private UIController controller;
    private Fenetre parentFrame;

    private JList<String> listTournois;
    private DefaultListModel<String> listModel;
    private JButton btnCreate, btnSelect, btnDelete;

    public TournoisPanel(UIController controller, Fenetre parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create tournament list
        JPanel listPanel = createListPanel();
        add(listPanel, BorderLayout.CENTER);

        // Create buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Liste des Tournois");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.WHITE);

        listModel = new DefaultListModel<>();
        listTournois = new JList<>(listModel);
        listTournois.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTournois.setFont(new Font("Arial", Font.PLAIN, 14));
        listTournois.setFixedCellHeight(40);

        // Custom cell renderer for better-looking list items
        listTournois.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    label.setBackground(new Color(232, 240, 254));
                    label.setForeground(new Color(63, 81, 181));
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(listTournois);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        listPanel.add(scrollPane, BorderLayout.CENTER);

        return listPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnCreate = createStyledButton("Créer un tournoi", "/icons/create_tournament.png");
        btnSelect = createStyledButton("Sélectionner", "/icons/select.png");
        btnDelete = createStyledButton("Supprimer", "/icons/delete.png");

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnSelect);
        buttonPanel.add(btnDelete);

        // Wire up button actions
        btnCreate.addActionListener(e -> createTournoi());
        btnSelect.addActionListener(e -> selectTournoi());
        btnDelete.addActionListener(e -> deleteTournoi());

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
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(48, 63, 159));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BG);
            }
        });

        return button;
    }

    public void refreshData() {
        // Load the list of available tournois from the controller
        List<String> allNames = controller.getAllTournoiNames();
        listModel.clear();
        for (String tName : allNames) {
            listModel.addElement(tName);
        }
        if (listModel.size() > 0) {
            listTournois.setSelectedIndex(0);
            btnSelect.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            btnSelect.setEnabled(false);
            btnDelete.setEnabled(false);
        }
        parentFrame.setStatus("Sélection d'un tournoi");
    }

    private void createTournoi() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(new JLabel("Nom du nouveau tournoi:"), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Créer un tournoi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION && !field.getText().trim().isEmpty()) {
            try {
                controller.createTournoi(field.getText().trim());
                refreshData();
            } catch (Exception ex) {
                showErrorDialog("Erreur", ex.getMessage());
            }
        }
    }

    private void selectTournoi() {
        String selected = listTournois.getSelectedValue();
        if (selected != null) {
            controller.selectTournoiByName(selected);
            parentFrame.setStatus("Tournoi \"" + selected + "\" sélectionné");
            parentFrame.updateButtons();
            // Optionally switch to details panel or repaint
            parentFrame.repaint();
        }
    }

    private void deleteTournoi() {
        String selected = listTournois.getSelectedValue();
        if (selected != null) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment supprimer le tournoi: " + selected + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                controller.deleteTournoiByName(selected);
                refreshData();
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
