import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournoiDAO implements AbstractDAO<Tournoi> {
    private Connection connection;

    public TournoiDAO(Connection connection) {
        this.connection = connection;
    }

    public TournoiDAO() {
        try {
            this.connection = DatabaseConnector.getConnection();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Tournoi get(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM tournois WHERE id_tournoi=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Tournoi tournoi = new Tournoi(
                    rs.getInt("id_tournoi"),
                    rs.getInt("nb_matchs"),
                    rs.getString("nom_tournoi"),
                    rs.getInt("statut")
                );
                tournoi.setEquipes(getTeamsByTournoiId(tournoi.getId()));
                tournoi.setMatchs(getMatchesByTournoiId(tournoi.getId()));
                tournoi.setNbMatchs(getNumberOfMatches(tournoi.getId()));
                tournoi.setNbTours(getNumberOfTours(tournoi.getId()));
                rs.close();
                ps.close();
                return tournoi;
            }
            rs.close();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tournoi getByName(String tournoiName) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM tournois WHERE nom_tournoi=?"
            );
            ps.setString(1, tournoiName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Tournoi tournoi = new Tournoi(
                    rs.getInt("id_tournoi"),
                    rs.getInt("nb_matchs"),
                    rs.getString("nom_tournoi"),
                    rs.getInt("statut")
                );
                tournoi.setEquipes(getTeamsByTournoiId(tournoi.getId()));
                tournoi.setMatchs(getMatchesByTournoiId(tournoi.getId()));
                tournoi.setNbMatchs(getNumberOfMatches(tournoi.getId()));
                tournoi.setNbTours(getNumberOfTours(tournoi.getId()));
                rs.close();
                ps.close();
                return tournoi;
            }
            rs.close();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Tournoi> getAll() {
        List<Tournoi> tournois = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tournois");
            while(rs.next()) {
                Tournoi tournoi = new Tournoi(
                    rs.getInt("id_tournoi"),
                    rs.getInt("nb_matchs"),
                    rs.getString("nom_tournoi"),
                    rs.getInt("statut")
                );
                tournoi.setEquipes(getTeamsByTournoiId(tournoi.getId()));
                tournoi.setMatchs(getMatchesByTournoiId(tournoi.getId()));
                tournoi.setNbMatchs(getNumberOfMatches(tournoi.getId()));
                tournoi.setNbTours(getNumberOfTours(tournoi.getId()));
                tournois.add(tournoi);
            }
            rs.close();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return tournois;
    }

    @Override
    public void update(Tournoi t) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE tournois SET nom_tournoi=?, nb_matchs=?, statut=? WHERE id_tournoi=?"
            );
            ps.setString(1, t.getNom());
            ps.setInt(2, t.getNbMatchs());
            ps.setInt(3, t.getStatut());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM tournois WHERE id_tournoi=?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            // Also remove matches and teams
            ps = connection.prepareStatement("DELETE FROM matchs WHERE id_tournoi=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = connection.prepareStatement("DELETE FROM equipes WHERE id_tournoi=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByName(String tournoiName) {
        Tournoi t = getByName(tournoiName);
        if(t != null) {
            delete(t.getId());
        }
    }

    public void create(Tournoi t) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(
                "INSERT INTO tournois (nom_tournoi, nb_matchs, statut) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, t.getNom());
            ps.setInt(2, t.getNbMatchs());
            ps.setInt(3, t.getStatut());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if(keys.next()) {
                int id = keys.getInt(1);
                // set back in object
            }
            keys.close();
            ps.close();
        } catch(SQLException e) {
            if(ps != null) ps.close();
            throw e;
        }
    }

    private List<Equipe> getTeamsByTournoiId(int tournoiId) {
        List<Equipe> equipes = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM equipes WHERE id_tournoi=?"
            );
            ps.setInt(1, tournoiId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                equipes.add(new Equipe(
                    rs.getInt("id_equipe"),
                    rs.getInt("num_equipe"),
                    rs.getString("nom_j1"),
                    rs.getString("nom_j2"),
                    rs.getInt("id_tournoi")
                ));
            }
            rs.close();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return equipes;
    }

    private List<Match> getMatchesByTournoiId(int tournoiId) {
        List<Match> matchs = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM matchs WHERE id_tournoi=?"
            );
            ps.setInt(1, tournoiId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                boolean isFinished = rs.getString("termine").equalsIgnoreCase("oui");
                matchs.add(new Match(
                    rs.getInt("id_match"),
                    rs.getInt("equipe1"),
                    rs.getInt("equipe2"),
                    rs.getInt("score1"),
                    rs.getInt("score2"),
                    rs.getInt("num_tour"),
                    isFinished
                ));
            }
            rs.close();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }

    private int getNumberOfMatches(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM matchs WHERE id_tournoi=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count;
            }
            rs.close();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getNumberOfTours(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT MAX(num_tour) FROM matchs WHERE id_tournoi=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int maxTour = rs.getInt(1);
                rs.close();
                ps.close();
                return maxTour;
            }
            rs.close();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
