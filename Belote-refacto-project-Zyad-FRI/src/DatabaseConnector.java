import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Singleton class for managing DB connection using .env variables.
 */
public class DatabaseConnector {
    private static Connection connection = null;

    private DatabaseConnector() {
        // Private constructor to prevent instantiation
    }

    /**
     * Reads DB credentials from the .env file.
     * Ensure .env has the lines:
     *  DB_URL=...
     *  DB_USERNAME=...
     *  DB_PASSWORD=...
     */
    private static String[] readEnvFile() throws IOException {
        String[] env = new String[3]; // [url, user, pwd]
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    switch (key) {
                        case "DB_URL":
                            env[0] = value;
                            break;
                        case "DB_USERNAME":
                            env[1] = value;
                            break;
                        case "DB_PASSWORD":
                            env[2] = value;
                            break;
                        default:
                            // ignore
                            break;
                    }
                }
            }
        }
        return env;
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); 
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("MySQL Driver not found");
            }

            String[] env = null;
            try {
                env = readEnvFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new SQLException("Could not read .env file");
            }

            if (env != null && env[0] != null) {
                String url = env[0];
                String usr = env[1];
                String pwd = env[2] == null ? "" : env[2];

                connection = DriverManager.getConnection(url, usr, pwd);
            } else {
                throw new SQLException("Database credentials missing in .env");
            }
        }
        return connection;
    }

    public static Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }
}
