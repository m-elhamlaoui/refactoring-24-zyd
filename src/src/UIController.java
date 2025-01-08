import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UIController {
    private TournoiDAO tournoiDAO = new TournoiDAO();
    private MatchDAO matchDAO = new MatchDAO();
    private EquipesDAO equipesDAO = new EquipesDAO();
    
    private Tournoi currentTournoi;
    private boolean isFirstLaunch = true;

    public UIController() {}

    public boolean isFirstLaunch() {
        return isFirstLaunch;
    }

    public void setFirstLaunch(boolean value) {
        this.isFirstLaunch = value;
    }

    // Existing methods remain unchanged
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
        if(eqs == null) return res;
        for(Equipe e : eqs) {
            int wins = equipesDAO.getMatchsGagne(currentTournoi.getId(), e.getId()).size();
            int draws= equipesDAO.getMatchsNul(currentTournoi.getId(), e.getId()).size();
            int lost = equipesDAO.getMatchsPerdu(currentTournoi.getId(), e.getId()).size();
            int played = wins + draws + lost;
            int score = (wins * 3) + draws;
            res.add(new EquipeResult(e.getNum(), e.getPlayer1(), e.getPlayer2(), score, wins, played));
        }
        // Sort res by score descending
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