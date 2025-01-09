import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class UIController {
    private TournoiDAO tournoiDAO = new TournoiDAO();
    private MatchDAO matchDAO = new MatchDAO();
    private EquipesDAO equipesDAO = new EquipesDAO();

    private Tournoi currentTournoi;

    public UIController() {}

    public List<String> getAllTournoiNames() {
        List<String> result = new ArrayList<>();
        List<Tournoi> ts = tournoiDAO.getAll();
        for (Tournoi t: ts) {
            result.add(t.getNom());
        }
        return result;
    }

    public void createTournoi(String name) throws Exception {
        Tournoi.createTournoi(name);
    }

    public void selectTournoiByName(String name) {
        currentTournoi = new Tournoi(name);
    }

    public void deleteTournoiByName(String name) {
        tournoiDAO.deleteByName(name);
        if(currentTournoi != null && currentTournoi.getNom().equals(name)) {
            currentTournoi = null;
        }
    }

    public Tournoi getCurrentTournoi() {
        return currentTournoi;
    }

    // Equipe methods
    public void addEquipe(String j1, String j2) {
        if(currentTournoi == null) return;
        int newNum = currentTournoi.getNbEquipes() + 1;
        Equipe e = new Equipe(newNum, j1, j2);
        e.setIdTournoi(currentTournoi.getId());
        currentTournoi.ajouterEquipe(e);
    }

    public void removeEquipe(int rowIndex) {
        if(currentTournoi == null) return;
        List<Equipe> eqs = getEquipesOfCurrentTournoi();
        if(rowIndex >=0 && rowIndex < eqs.size()) {
            Equipe e = eqs.get(rowIndex);
            currentTournoi.supprimerEquipe(e.getId());
        }
    }

    public void updateEquipe(Equipe e) {
        if(currentTournoi == null) return;
        currentTournoi.updateEquipe(e);
    }

    public void validateEquipes() {
        if(currentTournoi == null) return;
        currentTournoi.genererMatchs();
        currentTournoi.setStatut(2); // "Matchs en cours"
    }

    public List<Equipe> getEquipesOfCurrentTournoi() {
        if(currentTournoi == null) return new ArrayList<>();
        return currentTournoi.getEquipes();
    }

    // Tour methods
    public boolean addTour() {
        if(currentTournoi == null) return false;
        
        // First check if we have enough teams
        List<Equipe> equipes = getEquipesOfCurrentTournoi();
        if(equipes.size() < 2) return false;
        
        // Then add the tour
        return currentTournoi.ajouterTour();
    }
    public void removeTour() {
        if(currentTournoi != null) {
            currentTournoi.supprimerTour();
        }
    }

    // For ToursPanel
    public List<TourInfo> getToursInfo() {
        List<TourInfo> list = new ArrayList<>();
        if(currentTournoi == null) return list;
        // Build info per each existing tour
        int nbTours = currentTournoi.getNbTours();
        for(int i=1; i <= nbTours; i++) {
            int totalMatches = 0;
            int finishedMatches = 0;
            Vector<Match> allMatches = currentTournoi.getMatchs();
            for(Match m : allMatches) {
                if(m.getTour() == i) {
                    totalMatches++;
                    if(m.getIsFinished()) {
                        finishedMatches++;
                    }
                }
            }
            list.add(new TourInfo(i, totalMatches, finishedMatches));
        }
        return list;
    }

    // Match methods
    public List<Match> getMatchesOfCurrentTournoi() {
        if(currentTournoi == null) return new ArrayList<>();
        return currentTournoi.getMatchs();
    }

    public void updateMatch(Match m) {
        currentTournoi.updateMatch(m);
    }

    public String getMatchStatus() {
        if(currentTournoi == null) return "";
        int total = 0;
        int finished = 0;
        for(Match m : currentTournoi.getMatchs()) {
            total++;
            if(m.getIsFinished()) {
                finished++;
            }
        }
        return finished + "/" + total + " matchs terminés";
    }

    // Results methods
    public List<EquipeResult> computeResults() {
        List<EquipeResult> res = new ArrayList<>();
        if(currentTournoi == null) return res;
        
        Vector<Equipe> eqs = currentTournoi.getEquipes();
        Vector<Match> matches = currentTournoi.getMatchs();
        
        if(eqs == null) return res;
        
        // Create a map to store team scores using team numbers instead of IDs
        Map<Integer, Integer> teamScores = new HashMap<>();
        Map<Integer, Integer> teamWins = new HashMap<>();
        Map<Integer, Integer> teamMatches = new HashMap<>();
        
        // Initialize maps with team numbers
        for(Equipe e : eqs) {
            teamScores.put(e.getNum(), 0);
            teamWins.put(e.getNum(), 0);
            teamMatches.put(e.getNum(), 0);
        }
        
        // Calculate scores from matches
        for(Match m : matches) {
            if(m.getIsFinished()) {
                // Get team numbers (not IDs)
                int team1 = m.getTeam1();
                int team2 = m.getTeam2();
                
                // Count played matches
                teamMatches.merge(team1, 1, Integer::sum);
                teamMatches.merge(team2, 1, Integer::sum);
                
                // Calculate score based on match result
                if(m.getScore1() > m.getScore2()) {
                    // Team 1 wins
                    teamScores.merge(team1, 3, Integer::sum);
                    teamWins.merge(team1, 1, Integer::sum);
                } else if(m.getScore2() > m.getScore1()) {
                    // Team 2 wins
                    teamScores.merge(team2, 3, Integer::sum);
                    teamWins.merge(team2, 1, Integer::sum);
                } else if(m.getScore1() == m.getScore2() && m.getScore1() > 0) {
                    // Draw
                    teamScores.merge(team1, 1, Integer::sum);
                    teamScores.merge(team2, 1, Integer::sum);
                }
            }
        }
        
        // Create results list
        for(Equipe e : eqs) {
            int teamNum = e.getNum();
            res.add(new EquipeResult(
                teamNum,
                e.getPlayer1(),
                e.getPlayer2(),
                teamScores.getOrDefault(teamNum, 0),
                teamWins.getOrDefault(teamNum, 0),
                teamMatches.getOrDefault(teamNum, 0)
            ));
        }
        
        // Sort by score descending
        res.sort((a, b) -> b.score - a.score);
        return res;
    }

    public String getWinner() {
        List<EquipeResult> sorted = computeResults();
        if(sorted.isEmpty()) return null;
        EquipeResult top = sorted.get(0);
        return "Équipe " + top.equipeNum + " ("+top.player1+" & "+top.player2+")";
    }
}

/**
 * Simple struct to hold per-tour data
 */
class TourInfo {
    int tourNumber;
    int totalMatches;
    int finishedMatches;
    public TourInfo(int t, int total, int fin) {
        this.tourNumber = t;
        this.totalMatches = total;
        this.finishedMatches = fin;
    }
}

/**
 * Simple struct to represent results for an Equipe
 */
class EquipeResult {
    int equipeNum;
    String player1;
    String player2;
    int score;
    int wins;
    int played;
    public EquipeResult(int equipeNum, String p1, String p2, int score, int wins, int played) {
        this.equipeNum = equipeNum;
        this.player1 = p1;
        this.player2 = p2;
        this.score = score;
        this.wins = wins;
        this.played = played;
    }
}