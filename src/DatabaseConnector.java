import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class DatabaseConnector {
    private static Connection connection = null;

    private DatabaseConnector() {
    }


        private static String[] readEnvFile() throws IOException {
        String[] env = new String[2];
        BufferedReader reader = new BufferedReader(new FileReader(".env"));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (key.equalsIgnoreCase("DB_USERNAME")) {
                    env[0] = value;
                } else if (key.equalsIgnoreCase("DB_PASSWORD")) {
                    env[1] = value;
                }
            }
        }

        reader.close();
        return env;
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String[] env = null;
            try {
                env = readEnvFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String usr=env[0];
            String pwd=env[1];
            String url = "jdbc:mysql://localhost:3306/belote"; 

            connection = DriverManager.getConnection(url, usr, pwd);
        }
        return connection;
    }

    public static Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

}
