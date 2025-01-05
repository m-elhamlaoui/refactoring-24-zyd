import java.sql.ResultSet;
import java.sql.SQLException;

public class TournoiFactory {

    TournoiDAO tournoiDAO = new TournoiDAO();
    public static Tournoi createTournoiFromResultSet(ResultSet rs) throws SQLException {
        Tournoi tournoi = new Tournoi(
                rs.getInt("id_tournoi"),
                rs.getInt("nb_matchs"),
                rs.getString("nom_tournoi"),
                rs.getInt("statut")
        );
        return tournoi;
    }
}
