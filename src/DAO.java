// DAO.java
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.JOptionPane;

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

    public void importSQL(File in) {
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
        } catch (SQLException | FileNotFoundException e) {
            handleDatabaseError(e);
        }
    }

    private void handleDatabaseError(Exception e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        e.printStackTrace();
        System.exit(0);
    }
}
