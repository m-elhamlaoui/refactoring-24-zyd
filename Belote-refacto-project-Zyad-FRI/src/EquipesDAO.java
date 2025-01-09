import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EquipesDAO implements AbstractDAO<Equipe> {

    private Connection connection;

    public EquipesDAO() {
        try {
            this.connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Equipe get(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM equipes WHERE id_equipe = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Equipe(
                    rs.getInt("id_equipe"),
                    rs.getInt("num_equipe"),
                    rs.getString("nom_j1"),
                    rs.getString("nom_j2"),
                    rs.getInt("id_tournoi")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void create(Equipe e) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO equipes (num_equipe, id_tournoi , nom_j1, nom_j2) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, e.getNum());
            ps.setInt(2, e.getIdTournoi());
            ps.setString(3, e.getPlayer1());
            ps.setString(4, e.getPlayer2());
            ps.executeUpdate();

            // get newly generated ID
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                e.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Equipe> getAll() {
        List<Equipe> equipes = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM equipes");
            while (rs.next()) {
                equipes.add(new Equipe(
                    rs.getInt("id_equipe"),
                    rs.getInt("num_equipe"),
                    rs.getString("nom_j1"),
                    rs.getString("nom_j2"),
                    rs.getInt("id_tournoi")
                ));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipes;
    }

    @Override
    public void update(Equipe e) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE equipes SET num_equipe = ?, nom_j1 = ?, nom_j2 = ? WHERE id_equipe = ?"
            );
            ps.setInt(1, e.getNum());
            ps.setString(2, e.getPlayer1());
            ps.setString(3, e.getPlayer2());
            ps.setInt(4, e.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM equipes WHERE id_equipe = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shift team numbers down if a team is removed
     */
    public void updateNum(int id_tournoi, int numeq) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE equipes SET num_equipe = num_equipe - 1 WHERE id_tournoi = ? AND num_equipe > ?"
            );
            ps.setInt(1, id_tournoi);
            ps.setInt(2, numeq);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNumFromId(int id_equipe, int id_tournoi) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT num_equipe FROM equipes WHERE id_equipe = ? AND id_tournoi = ?");
            ps.setInt(1, id_equipe);
            ps.setInt(2, id_tournoi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("num_equipe");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Return matches won by a certain team
    public Vector<Match> getMatchsGagne(int tournoi, int equipe){
        Vector<Match> matchs = new Vector<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM matchs WHERE id_tournoi = ? AND termine = 'oui' "
              + "AND ((equipe1 = ? AND score1 > score2) OR (equipe2 = ? AND score2 > score1))"
            );
            ps.setInt(1, tournoi);
            ps.setInt(2, equipe);
            ps.setInt(3, equipe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matchs.add(new Match(
                    rs.getInt("id_match"),
                    rs.getInt("equipe1"),
                    rs.getInt("equipe2"),
                    rs.getInt("score1"),
                    rs.getInt("score2"),
                    rs.getInt("num_tour"),
                    rs.getString("termine").equals("oui")
                ));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }

    // Return matches lost by a certain team
    public Vector<Match> getMatchsPerdu(int tournoi,int equipe){
        Vector<Match> matchs = new Vector<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM matchs WHERE id_tournoi = ? AND termine = 'oui' "
              + "AND ((equipe1 = ? AND score1 < score2) OR (equipe2 = ? AND score2 < score1))"
            );
            ps.setInt(1, tournoi);
            ps.setInt(2, equipe);
            ps.setInt(3, equipe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matchs.add(new Match(
                    rs.getInt("id_match"),
                    rs.getInt("equipe1"),
                    rs.getInt("equipe2"),
                    rs.getInt("score1"),
                    rs.getInt("score2"),
                    rs.getInt("num_tour"),
                    rs.getString("termine").equals("oui")
                ));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }

    // Return matches that ended in a draw
    public Vector<Match> getMatchsNul(int tournoi,int equipe){
        Vector<Match> matchs = new Vector<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM matchs WHERE id_tournoi = ? AND termine = 'oui' "
              + "AND ((equipe1 = ? AND score1 = score2) OR (equipe2 = ? AND score2 = score1))"
            );
            ps.setInt(1, tournoi);
            ps.setInt(2, equipe);
            ps.setInt(3, equipe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matchs.add(new Match(
                    rs.getInt("id_match"),
                    rs.getInt("equipe1"),
                    rs.getInt("equipe2"),
                    rs.getInt("score1"),
                    rs.getInt("score2"),
                    rs.getInt("num_tour"),
                    rs.getString("termine").equals("oui")
                ));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }
}
