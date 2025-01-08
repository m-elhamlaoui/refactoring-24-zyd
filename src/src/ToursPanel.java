	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.util.List;
	
	public class ToursPanel extends JPanel {
	    private UIController controller;
	    private Fenetre parentFrame;
	
	    private JTable table;
	    private DefaultTableModelEx tableModel;
	    private JButton btnAddTour, btnRemoveTour;
	
	    public ToursPanel(UIController controller, Fenetre parentFrame) {
	        this.controller = controller;
	        this.parentFrame = parentFrame;
	        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
	
	        add(new JLabel("Tours du tournoi:"));
	        tableModel = new DefaultTableModelEx(new String[]{"Tour #", "Nb Matchs", "Matchs Joués"}, 0);
	        table = new JTable(tableModel);
	        JScrollPane sp = new JScrollPane(table);
	        add(sp);
	
	        JPanel btnPanel = new JPanel(new FlowLayout());
	        btnAddTour = new JButton("Ajouter un tour");
	        btnRemoveTour = new JButton("Supprimer le dernier tour");
	        btnPanel.add(btnAddTour);
	        btnPanel.add(btnRemoveTour);
	        add(btnPanel);
	
	        btnAddTour.addActionListener(e -> addTour());
	        btnRemoveTour.addActionListener(e -> removeTour());
	    }
	
	    public void refreshData() {
	        tableModel.setRowCount(0);
	        List<TourInfo> tourInfos = controller.getToursInfo();
	        for (TourInfo ti : tourInfos) {
	            tableModel.addRow(new Object[]{ti.tourNumber, ti.totalMatches, ti.finishedMatches});
	        }
	        parentFrame.setStatus("Gestion des Tours");
	    }
	
	    private void addTour() {
	        boolean added = controller.addTour();
	        if(!added) {
	            JOptionPane.showMessageDialog(this, "Impossible d'ajouter un tour (max atteint ou conditions non remplies)");
	        }
	        refreshData();
	    }
	
	    private void removeTour() {
	        controller.removeTour();
	        refreshData();
	    }
	
	    // Simple extension of DefaultTableModel to allow convenience
	    private static class DefaultTableModelEx extends javax.swing.table.DefaultTableModel {
	        public DefaultTableModelEx(Object[] columnNames, int rowCount) {
	            super(columnNames, rowCount);
	        }
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return false;
	        }
	    }
	}
