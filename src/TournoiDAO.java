import java.sql.*;
import java.util.*;

public class TournoiDAO implements AbstractDAO<Tournoi>{

    private Connection connection;

    public TournoiDAO(Connection connection) {
        this.connection = connection;
    }

    public TournoiDAO() {
        try{
        this.connection=DatabaseConnector.getConnection();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Tournoi get(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tournois WHERE id_tournoi = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                //Tournoi t=new Tournoi(rs.getInt("id_tournoi"), rs.getString("nom_tournoi"),rs.getInt("nb_equipe"),rs.getInt("nb_matchs"),rs.getString("statut"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tournoi getByName(String tournoiName){
        Tournoi tournoi = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tournois WHERE nom_tournoi = ?");
            ps.setString(1, tournoiName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("tournoi trouvé");
                tournoi = new Tournoi(rs.getInt("id_tournoi"), rs.getInt("nb_matchs"), rs.getString("nom_tournoi"), rs.getInt("statut"));
                

                tournoi.setEquipes(getTeamsByTournoiId(tournoi.getId()));
                tournoi.setMatchs(getMatchesByTournoiId(tournoi.getId()));
                tournoi.setNbMatchs(getNumberOfMatches(tournoi.getId()));
                tournoi.setNbTours(getNumberofTours(tournoi.getId()));


                System.out.println("All Tournois attributes set successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournoi;
    }

    private int getNumberOfMatches(int id){
        int nbMatchs = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM matchs where id_tournoi = ?");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nbMatchs = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nbMatchs;
    }

    private int getNumberofTours(int id){
        int nbTours = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT MAX(num_tour) FROM matchs WHERE id_tournoi= ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nbTours = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nbTours;
    }


    // This method is used to get the teams of a tournament (based on it's id).
    private List<Equipe> getTeamsByTournoiId(int tournoiId) {
        List<Equipe> equipes = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM equipes WHERE id_tournoi = ?");
            ps.setInt(1, tournoiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                equipes.add(new Equipe(rs.getInt("id_equipe"), rs.getInt("num_equipe"), rs.getString("nom_j1"), rs.getString("nom_j2"), tournoiId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipes;
    }

    // This method is used to get the matches of a tournament (based on it's id).
    public List<Match> getMatchesByTournoiId(int tournoiId) {
        List<Match> matchs = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM matchs WHERE id_tournoi = ?");
            ps.setInt(1, tournoiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boolean isFinished = rs.getString("termine").equalsIgnoreCase("yes");
                matchs.add(new Match(rs.getInt("id_match"), rs.getInt("equipe1"), rs.getInt("equipe2"), rs.getInt("score1"), rs.getInt("score2"), rs.getInt("num_tour"), isFinished));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }




    @Override
    public List<Tournoi> getAll() {
        List<Tournoi> tournois = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tournoi");
            while (rs.next()) {
                Tournoi tournoi = TournoiFactory.createTournoiFromResultSet(rs);
                tournoi.setEquipes(getTeamsByTournoiId(tournoi.getId()));
                tournoi.setMatchs(getMatchesByTournoiId(tournoi.getId()));
                tournoi.setNbMatchs(getNumberOfMatches(tournoi.getId()));
                tournoi.setNbTours(getNumberofTours(tournoi.getId()));
                tournois.add(tournoi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournois;
    }

    public void create (Tournoi t) throws SQLException{
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO tournois (nom_tournoi, nb_matchs, statut) VALUES (?, ?, ?)");
            ps.setString(1, t.getNom());
            ps.setInt(2, t.getNbMatchs());
            ps.setInt(3, t.getStatut());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void update(Tournoi t) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE tournois SET nom_tournoi = ?, nb_matchs = ?, statut = ? WHERE id_tournoi = ?");
            ps.setString(1, t.getNom());
            ps.setInt(2, t.getNbMatchs());
            ps.setInt(3, t.getStatut());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM tournois WHERE id_tournoi = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps=connection.prepareStatement("DELETE FROM matchs WHERE id_tournoi = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps=connection.prepareStatement("DELETE FROM equipes WHERE id_tournoi = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByName(String tournoiName) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM tournois WHERE nom_tournoi = ?");
            ps.setString(1, tournoiName);
            ps.executeUpdate();
            ps=connection.prepareStatement("DELETE FROM matchs WHERE id_tournoi = ?");
            Tournoi t = getByName(tournoiName);
            if(t!=null&&t.getId()!=(Integer)null){
                ps.setInt(1, t.getId());
                ps.executeUpdate();
                ps=connection.prepareStatement("DELETE FROM equipes WHERE id_tournoi = ?");
                ps.setInt(1, getByName(tournoiName).getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}