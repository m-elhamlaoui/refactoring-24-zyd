import java.sql.*;
import java.util.*;

public class MatchDAO implements AbstractDAO<Match>{

    private Connection connection;

    public MatchDAO(){
        try{
            this.connection=DatabaseConnector.getConnection();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    public Match get(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM matchs WHERE id_match = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Match(rs.getInt("id_match"), rs.getInt("equipe1"), rs.getInt("equipe2"), rs.getInt("score1"), rs.getInt("score2"), rs.getInt("num_tour"), rs.getString("termine").equals("yes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Match> getAll() {
        List<Match> matches = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM matchs");
            while (rs.next()) {
                matches.add(new Match(rs.getInt("id_match"), rs.getInt("equipe1"), rs.getInt("equipe2"), rs.getInt("score1"), rs.getInt("score2"), rs.getInt("num_tour"), rs.getString("termine").equals("yes")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    @Override
    public void update(Match m) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE matchs SET equipe1 = ?, equipe2 = ?, score1 = ?, score2 = ?, num_tour = ?, termine = ? WHERE id_match = ?");
            ps.setInt(1, m.getTeam1());
            ps.setInt(2, m.getTeam2());
            ps.setInt(3, m.getScore1());
            ps.setInt(4, m.getScore2());
            ps.setInt(5, m.getTournamentNumber());
            ps.setString(6, m.getIsFinished() ? "oui" : "non");
            ps.setInt(7, m.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void create(Match m){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO matchs (id_tournoi, equipe1, equipe2, score1, score2, num_tour, termine) VALUES (?,?, ?, ?, ?, ?, ?)");
            ps.setInt(1, m.getTournamentNumber());
            ps.setInt(2, m.getTeam1());
            ps.setInt(3, m.getTeam2());
            ps.setInt(4, m.getScore1());
            ps.setInt(5, m.getScore2());
            ps.setInt(6, m.getTour());
            ps.setString(7, m.getIsFinished() ? "oui" : "non");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM matchs WHERE id_match = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMatchByTournoiIdAndTourNumber(int id, int nbtoursav) {
        try{
            PreparedStatement ps = connection.prepareStatement("DELETE FROM matchs WHERE id_tournoi = ? AND num_tour = ?");
            ps.setInt(1, id);
            ps.setInt(2, nbtoursav);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}