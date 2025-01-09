import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class WelcomePage extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(255, 99, 132);    // Pink accent
    private static final Color SECONDARY_COLOR = new Color(248, 249, 250); // Light gray
    private static final Color CARD_BG = new Color(63, 81, 181);          // Material Indigo
    private static final Color TEXT_COLOR = new Color(33, 37, 41);        // Dark text

    private JButton startButton;
    private Fenetre mainWindow;
    private BufferedImage cardImage;
    private BufferedImage backgroundPattern;

    public WelcomePage(Fenetre mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        loadImages();
        initializeComponents();
    }

    private void loadImages() {
        try {
            // Load card images
            ImageIcon spadeKing = new ImageIcon(getClass().getResource("/icons/king_spades.png"));
            ImageIcon heartQueen = new ImageIcon(getClass().getResource("/icons/queen_hearts.png"));
            ImageIcon diamondJack = new ImageIcon(getClass().getResource("/icons/jack_diamonds.png"));
            ImageIcon clubAce = new ImageIcon(getClass().getResource("/icons/ace_clubs.png"));

            // Create buffered image for cards
            cardImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cardImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw cards with shadow and rotation
            drawRotatedCard(g2d, spadeKing.getImage(), 50, 50, -15);
            drawRotatedCard(g2d, heartQueen.getImage(), 150, 50, -5);
            drawRotatedCard(g2d, diamondJack.getImage(), 250, 50, 5);
            drawRotatedCard(g2d, clubAce.getImage(), 350, 50, 15);

            g2d.dispose();

            // Create pattern background
            createBackgroundPattern();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void createBackgroundPattern() {
        backgroundPattern = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = backgroundPattern.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw pattern elements
        g2d.setColor(new Color(255, 255, 255, 20));
        int[] suits = {9824, 9827, 9829, 9830}; // Unicode for card suits
        Font suitFont = new Font("Serif", Font.PLAIN, 20);
        g2d.setFont(suitFont);

        for (int i = 0; i < suits.length; i++) {
            g2d.drawString(String.valueOf((char)suits[i]), 
                          (i % 2) * 30 + 5, 
                          (i / 2) * 30 + 25);
        }

        g2d.dispose();
    }

    private void drawRotatedCard(Graphics2D g2d, Image card, int x, int y, double angle) {
        // Draw shadow
        AffineTransform shadowAt = new AffineTransform();
        shadowAt.translate(x + 5, y + 5);
        shadowAt.rotate(Math.toRadians(angle), card.getWidth(null) / 2, card.getHeight(null) / 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(0, 0, card.getWidth(null), card.getHeight(null), 10, 10);

        // Draw card
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(Math.toRadians(angle), card.getWidth(null) / 2, card.getHeight(null) / 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.drawImage(card, at, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, CARD_BG,
            0, getHeight(), CARD_BG.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw pattern
        if (backgroundPattern != null) {
            for (int x = 0; x < getWidth(); x += backgroundPattern.getWidth()) {
                for (int y = 0; y < getHeight(); y += backgroundPattern.getHeight()) {
                    g2d.drawImage(backgroundPattern, x, y, null);
                }
            }
        }

        // Draw spotlight effect
        drawSpotlight(g2d);

        // Draw cards
        if (cardImage != null) {
            g2d.drawImage(cardImage,
                (getWidth() - cardImage.getWidth()) / 2,
                getHeight() - cardImage.getHeight() - 100,
                null);
        }
    }

    private void drawSpotlight(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.max(getWidth(), getHeight()) / 2;

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

        // Title
        JLabel titleLabel = createStyledLabel("Belote", 72);
        titleLabel.setForeground(Color.WHITE);
        add(Box.createVerticalStrut(50), gbc);
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = createStyledLabel("Gestion de Tournois", 32);
        subtitleLabel.setForeground(PRIMARY_COLOR);
        add(Box.createVerticalStrut(20), gbc);
        add(subtitleLabel, gbc);

        // Spacing
        add(Box.createVerticalStrut(100), gbc);

        // Start button
        startButton = createStartButton();
        add(startButton, gbc);
    }

    private JLabel createStyledLabel(String text, int size) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), 3, getHeight() - 3);

                // Draw text
                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, getHeight() - 6);
            }
        };
        label.setFont(new Font("Serif", Font.BOLD, size));
        return label;
    }

    private JButton createStartButton() {
        JButton button = new JButton("COMMENCER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background
                if (getModel().isPressed()) {
                    g2d.setColor(PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(PRIMARY_COLOR.brighter());
                } else {
                    g2d.setColor(PRIMARY_COLOR);
                }

                // Draw rounded rectangle background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Draw text with shadow
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();

                // Draw text shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(getText(), x + 1, y + 1);

                // Draw main text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);

                // Add subtle shine effect
                GradientPaint shine = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 50),
                    0, getHeight()/2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(shine);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 25, 25);
            }
        };

        // Button styling
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(250, 60));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover animation effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setSize(260, 65); // Slightly larger on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setSize(250, 60); // Return to original size
            }
        });

        // Add click action
        button.addActionListener(e -> mainWindow.showMainInterface());

        return button;
    }

    /**
     * Creates a modern card effect with shadow and hover animation
     */
    private class ModernCard extends JPanel {
        private String suit;
        private String rank;
        private Color color;
        private boolean isHovered = false;

        public ModernCard(String suit, String rank, Color color) {
            this.suit = suit;
            this.rank = rank;
            this.color = color;
            setPreferredSize(new Dimension(100, 140));
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw shadow
            int shadowOffset = isHovered ? 8 : 5;
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(shadowOffset, shadowOffset, 
                            getWidth() - 10, getHeight() - 10, 15, 15);

            // Draw card background
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 15, 15);

            // Draw card content
            g2d.setColor(color);
            g2d.setFont(new Font("Serif", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            
            // Draw rank
            g2d.drawString(rank, 10, 30);
            
            // Draw suit
            g2d.setFont(new Font("Serif", Font.PLAIN, 32));
            g2d.drawString(suit, 
                         (getWidth() - fm.stringWidth(suit))/2, 
                         (getHeight() + fm.getHeight())/2);
        }
    }
}