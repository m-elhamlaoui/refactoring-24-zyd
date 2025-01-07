import javax.swing.*;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Main class to launch the Belote tournament application.
 * Demonstrates improved UI (with Nimbus look-and-feel),
 * external config for DB scripts, etc.
 */
public class Belote {
    public static void main(String[] args) {
        // Set a nicer look & feel
        setNimbusLookAndFeel();
        
        // Load any config from a properties file if needed
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream("belote.properties"));
        } catch (IOException e) {
            System.err.println("Could not load belote.properties: " + e.getMessage());
        }

        // Optionally load your CREATE SQL script path from .properties
        String createScriptPath = appProps.getProperty("SQL_SCRIPT_PATH", "create.sql");

        System.out.println("Bienvenue dans le jeu de la Belote!");

        DAO beloteDAO = new DAO();
        
        // If you still want to import the create script
        File scriptFile = new File(createScriptPath);
        if (scriptFile.exists()) {
            beloteDAO.importSQL(scriptFile);
        } else {
            System.out.println("create.sql not found, skipping import.");
        }

        // Initialize our main UI window
        UIController controller = new UIController(); 
        Fenetre f = new Fenetre(controller);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private static void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback if Nimbus is not available
            System.err.println("Nimbus LAF not available, using default.");
        }
    }
}
