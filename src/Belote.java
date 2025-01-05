  
import java.io.File;
import java.sql.SQLException;

import javax.swing.JFrame;
public class Belote {

	public static void main(String[] args) throws SQLException {

		System.out.println("Bienvenue dans le jeu de la belote");

		DAO beloteDAO = new DAO();
		beloteDAO.importSQL(new File("create.sql"));
		Fenetre f = new Fenetre(beloteDAO.createStatement());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}


