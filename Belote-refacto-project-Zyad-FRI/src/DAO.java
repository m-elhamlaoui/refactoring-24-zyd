import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * A high-level DAO (though the name is somewhat generic).
 * Responsible for general tasks like importing SQL scripts.
 */
public class DAO {
    private Connection connection;

    public DAO() {
        try {
            this.connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    /**
     * Execute the statements in a SQL file (used for schema creation, etc.).
     * @param in The SQL file to import
     */
    public void importSQL(File in) {
        if (in == null || !in.exists()) {
            System.out.println("SQL file not found, skipping importSQL...");
            return;
        }
        try (Scanner s = new Scanner(in)) {
            s.useDelimiter("(;(\r)?\n)|(--\n)");
            Statement st = connection.createStatement();
            while (s.hasNext()) {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/")) {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }
                if (line.trim().length() > 0) {
                    st.execute(line);
                }
            }
            st.close();
        } catch (SQLException | FileNotFoundException e) {
            handleDatabaseError(e);
        }
    }

    private void handleDatabaseError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Database error: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
        // throw new RuntimeException("Database error - see stack trace.", e);
    }
}
