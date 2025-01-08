import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class WelcomePage extends JPanel {
    private static final Color BELOTE_GREEN = new Color(0, 102, 51);
    private static final Color GOLD = new Color(255, 215, 0);
    private JButton startButton;
    private Fenetre mainWindow;
    private BufferedImage cardImage;

    public WelcomePage(Fenetre mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        loadImages();
        initializeComponents();
    }

    private void loadImages() {
        try {
            // Load card images from resources
            ImageIcon spadeKing = new ImageIcon(getClass().getResource("/icons/king_spades.png"));
            ImageIcon spadeQueen = new ImageIcon(getClass().getResource("/icons/queen_spades.png"));
            ImageIcon spadeJack = new ImageIcon(getClass().getResource("/icons/jack_spades.png"));
            
            // Create a buffered image to draw the cards
            cardImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cardImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the cards with slight rotation and overlap
            drawRotatedCard(g2d, spadeKing.getImage(), 50, 50, -15);
            drawRotatedCard(g2d, spadeQueen.getImage(), 150, 50, 0);
            drawRotatedCard(g2d, spadeJack.getImage(), 250, 50, 15);
            
            g2d.dispose();
        } catch (Exception e) {
            System.err.println("Error loading card images: " + e.getMessage());
        }
    }

    private void drawRotatedCard(Graphics2D g2d, Image card, int x, int y, double angle) {
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(Math.toRadians(angle), card.getWidth(null)/2, card.getHeight(null)/2);
        g2d.drawImage(card, at, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw green felt background
        GradientPaint greenGradient = new GradientPaint(
            0, 0, BELOTE_GREEN.darker(),
            0, getHeight(), BELOTE_GREEN
        );
        g2d.setPaint(greenGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add felt texture
        drawFeltTexture(g2d);

        // Draw subtle spotlight effect
        drawSpotlight(g2d);

        // Draw cards if image was loaded
        if (cardImage != null) {
            g2d.drawImage(cardImage, 
                (getWidth() - cardImage.getWidth())/2,
                getHeight() - cardImage.getHeight() - 100,
                null);
        }
    }

    private void drawFeltTexture(Graphics2D g2d) {
        for (int i = 0; i < getWidth(); i += 4) {
            for (int j = 0; j < getHeight(); j += 4) {
                if ((i + j) % 8 == 0) {
                    g2d.setColor(new Color(0, 0, 0, 10));
                    g2d.fillRect(i, j, 2, 2);
                }
            }
        }
    }

    private void drawSpotlight(Graphics2D g2d) {
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;
        int radius = Math.max(getWidth(), getHeight());
        
        RadialGradientPaint spotlight = new RadialGradientPaint(
            centerX, centerY, radius,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 50),
                new Color(255, 255, 255, 20),
                new Color(0, 0, 0, 30)
            }
        );
        g2d.setPaint(spotlight);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title "Belote"
        JLabel titleLabel = createStyledLabel("Belote", 72);
        titleLabel.setForeground(Color.WHITE);
        add(Box.createVerticalStrut(50), gbc);
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = createStyledLabel("Gestion de Tournois", 32);
        subtitleLabel.setForeground(GOLD);
        add(Box.createVerticalStrut(20), gbc);
        add(subtitleLabel, gbc);

        // Add spacing before button
        add(Box.createVerticalStrut(100), gbc);

        // Styled start button
        startButton = new JButton("COMMENCER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(GOLD.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(GOLD.brighter());
                } else {
                    g2d.setColor(GOLD);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                
                g2d.setColor(BELOTE_GREEN.darker());
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
            }
        };
        
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setForeground(BELOTE_GREEN.darker());
        startButton.setPreferredSize(new Dimension(250, 60));
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> mainWindow.showMainInterface());
        
        add(startButton, gbc);
    }

    private JLabel createStyledLabel(String text, int size) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), 3, getHeight() - 3);
                
                // Draw main text
                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, getHeight() - 6);
            }
        };
        label.setFont(new Font("Serif", Font.BOLD, size));
        return label;
    }
}