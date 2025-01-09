import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Fenetre extends JFrame {
    private static final long serialVersionUID = 1L;

    // Color constants
    private static final Color SIDEBAR_BG = new Color(48, 63, 159); // Material Indigo
    private static final Color SIDEBAR_FG = Color.WHITE;
    private static final Color HEADER_BG = new Color(63, 81, 181);  // Lighter Indigo
    private static final Color CONTENT_BG = new Color(250, 250, 250);

    private UIController controller;
    private JButton btnTournois, btnParametres, btnEquipes, btnTours, btnMatchs, btnResultats;
    private JLabel lblStatus;
    private final String defaultStatus = "Gestion de tournois de Belote v2.0 - ";
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private WelcomePage welcomePage; // Added WelcomePage

    // Panels
    private TournoisPanel tournoisPanel;
    private DetailsPanel detailsPanel;
    private EquipesPanel equipesPanel;
    private ToursPanel toursPanel;
    private MatchsPanel matchsPanel;
    private ResultatsPanel resultatsPanel;

    // View names for CardLayout
    public static final String TOURNOIS_VIEW = "Tournois";
    public static final String DETAILS_VIEW = "Parametres";
    public static final String EQUIPES_VIEW = "Equipes";
    public static final String TOURS_VIEW = "Tours";
    public static final String MATCHS_VIEW = "Matchs";
    public static final String RESULTATS_VIEW = "Resultats";

    public Fenetre(UIController controller) {
        this.controller = controller;

        // Setup JFrame properties
        setTitle("Gestion de Tournois de Belote");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the content pane
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(CONTENT_BG);
        setContentPane(contentPane);

        // Initialize WelcomePage
        welcomePage = new WelcomePage(this);
        showWelcomePage(); // Show WelcomePage initially

        // Add functionality for dragging undecorated windows
        addWindowMoveSupport();
    }

    public void showWelcomePage() {
        // Clear content pane and show WelcomePage
        getContentPane().removeAll();
        getContentPane().add(welcomePage, BorderLayout.CENTER);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public void showMainInterface() {
        // Clear content pane and initialize the main interface
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.removeAll();

        // Create modern header
        createHeader(contentPane);

        // Create stylish sidebar
        createSidebar(contentPane);

        // Create main content area with CardLayout
        createMainContent(contentPane);

        contentPane.revalidate();
        contentPane.repaint();
    }

    private void createHeader(JPanel contentPane) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(0, 60));

        // App title
        JLabel titleLabel = new JLabel("Gestion de Tournois de Belote", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Status label
        lblStatus = new JLabel(defaultStatus + "Aucun tournoi sélectionné");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        headerPanel.add(lblStatus, BorderLayout.EAST);

        contentPane.add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebar(JPanel contentPane) {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Create sidebar buttons
        btnTournois = createSidebarButton("Tournois", "/icons/trophy.png");
        btnParametres = createSidebarButton("Paramètres", "/icons/settings.png");
        btnEquipes = createSidebarButton("Équipes", "/icons/teams.png");
        btnTours = createSidebarButton("Tours", "/icons/rounds.png");
        btnMatchs = createSidebarButton("Matchs", "/icons/games.png");
        btnResultats = createSidebarButton("Résultats", "/icons/results.png");

        // Add buttons to sidebar
        sidebarPanel.add(btnTournois);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnParametres);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnEquipes);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnTours);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnMatchs);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnResultats);

        contentPane.add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            // Load the icon
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }

        // Button styling
        button.setForeground(SIDEBAR_FG);
        button.setBackground(SIDEBAR_BG);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(180, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(63, 81, 181));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(SIDEBAR_BG);
            }
        });

        return button;
    }

    private void createMainContent(JPanel contentPane) {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(CONTENT_BG);

        // Initialize panels
        tournoisPanel = new TournoisPanel(controller, this);
        detailsPanel = new DetailsPanel(controller, this);
        equipesPanel = new EquipesPanel(controller, this);
        toursPanel = new ToursPanel(controller, this);
        matchsPanel = new MatchsPanel(controller, this);
        resultatsPanel = new ResultatsPanel(controller, this);

        // Add panels to CardLayout
        mainPanel.add(tournoisPanel, TOURNOIS_VIEW);
        mainPanel.add(detailsPanel, DETAILS_VIEW);
        mainPanel.add(equipesPanel, EQUIPES_VIEW);
        mainPanel.add(toursPanel, TOURS_VIEW);
        mainPanel.add(matchsPanel, MATCHS_VIEW);
        mainPanel.add(resultatsPanel, RESULTATS_VIEW);

        // Button listeners to switch views
        btnTournois.addActionListener(e -> showView(TOURNOIS_VIEW));
        btnParametres.addActionListener(e -> showView(DETAILS_VIEW));
        btnEquipes.addActionListener(e -> showView(EQUIPES_VIEW));
        btnTours.addActionListener(e -> showView(TOURS_VIEW));
        btnMatchs.addActionListener(e -> showView(MATCHS_VIEW));
        btnResultats.addActionListener(e -> showView(RESULTATS_VIEW));

        contentPane.add(mainPanel, BorderLayout.CENTER);
    }

    private void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
        switch (viewName) {
            case TOURNOIS_VIEW: 
                tournoisPanel.refreshData();
                break;
            case DETAILS_VIEW: 
                detailsPanel.refreshData();
                break;
            case EQUIPES_VIEW: 
                equipesPanel.refreshData();
                break;
            case TOURS_VIEW: 
                toursPanel.refreshData();
                break;
            case MATCHS_VIEW: 
                matchsPanel.refreshData();
                break;
            case RESULTATS_VIEW: 
                resultatsPanel.refreshData();
                break;
            default:
                // Optional: Handle unknown views
                System.err.println("Unknown view: " + viewName);
        }
    }

    public void setStatus(String msg) {
        lblStatus.setText(defaultStatus + msg);
    }

    public void updateButtons() {
        boolean tournoiSelected = controller.getCurrentTournoi() != null;
        btnParametres.setEnabled(tournoiSelected);
        btnEquipes.setEnabled(tournoiSelected);
        btnTours.setEnabled(tournoiSelected);
        btnMatchs.setEnabled(tournoiSelected);
        btnResultats.setEnabled(tournoiSelected);
    }

    private void addWindowMoveSupport() {
        ComponentMover componentMover = new ComponentMover();
        componentMover.registerComponent(this);
    }
}
