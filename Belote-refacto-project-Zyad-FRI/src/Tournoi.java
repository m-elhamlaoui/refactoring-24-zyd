import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Tournoi {
    private String statuttnom;
    private int  statut;
    private int  Id;
    private int  nbMatchs;
    private int  nbTours;
    private String nomTournoi;
    private MatchGenerator mGen = new MatchGenerator();

    private Vector<Vector<Match>> tours = null;
    private Vector<Equipe> teams = null;
    private Vector<Match> matches  = null;

    private static TournoiDAO tournoiDAO    = new TournoiDAO();
    private static EquipesDAO equipesDAO    = new EquipesDAO();
    private static MatchDAO matchDAO        = new MatchDAO();

    public Tournoi(String nt) {
        this.nomTournoi = nt;
        Tournoi t = tournoiDAO.getByName(nt);
        if (t != null) {
            this.Id = t.getId();
            this.statut = t.getStatut();
            this.nbTours = t.getNbTours();
            this.nbMatchs = t.getNbMatchs();
            this.teams = t.getEquipes();
            this.matches = t.getMatchs();
        }
        setStatutName(this.statut);
    }

    public Tournoi(int nbMatchs, String nomTournoi, int statut) {
        this.nbMatchs = nbMatchs;
        this.nomTournoi = nomTournoi;
        this.statut = statut;
        setStatutName(statut);
    }

    public Tournoi(int Id, int nbMatchs, String nomTournoi, int statut) {
        this(nbMatchs, nomTournoi, statut);
        this.Id = Id;
    }

    private void setStatutName(int s) {
        switch(s){
            case 0: statuttnom = "Inscription des joueurs"; break;
            case 1: statuttnom = "Génération des matchs"; break;
            case 2: statuttnom = "Matchs en cours"; break;
            case 3: statuttnom = "Terminé"; break;
            default: statuttnom = "Inconnu";
        }
    }

    // Setters
    public void setEquipes(List<Equipe> eqs) {
        this.teams = new Vector<>(eqs);
    }
    public void setMatchs(List<Match> ms) {
        this.matches = new Vector<>(ms);
    }
    public void setNbTours(int nbTours) { this.nbTours = nbTours; }
    public void setNbMatchs(int nbMatchs){ this.nbMatchs = nbMatchs; }
    public void setStatut(int statut){
        this.statut = statut;
        setStatutName(statut);
        tournoiDAO.update(this);
    }

    // Getters
    public int getId() { return Id; }
    public Vector<Vector<Match>> getTours(){ return tours; }
    public int getStatut(){ return statut; }
    public String getNStatut(){ return statuttnom; }
    public String getNom() { return nomTournoi; }
    public int getNbTours(){ return nbTours; }
    public int getNbMatchs(){ return matches != null ? matches.size() : nbMatchs; }
    public Vector<Equipe> getEquipes(){ return teams; }
    public Vector<Match> getMatchs(){ return matches; }

    public Match getMatch(int index){ return matches.get(index); }
    public int getNbEquipes(){ return teams != null ? teams.size() : 0; }

    public static Tournoi createTournoi(String nom) throws Exception {
        if(nom == null || nom.isEmpty()) throw new Exception("Nom de tournoi invalide");
        if(nom.length() < 3) throw new Exception("Nom de tournoi trop court");
        if(tournoiDAO.getByName(nom) != null) {
            throw new Exception("Un tournoi du même nom existe déjà");
        }
        Tournoi t = new Tournoi(0, nom, 0);
        try {
            tournoiDAO.create(t);
        } catch(Exception e) {
            throw new Exception("Error Creating Tournoi", e);
        }
        return t;
    }

    public void genererMatchs(){
        if(this.teams == null || this.teams.isEmpty()) {
            return;
        }
        Vector<Vector<Match>> ms = mGen.generateMatches(teams);
        this.tours = ms;
        this.matches = new Vector<>();
        this.matches.clear();

        // We'll mark statut=1 => "Génération des matchs"
        if(this.statut < 2) {
            setStatut(1);
        }
    }

    public boolean ajouterTour() {
        if(this.tours == null) {
            genererMatchs();
        }
        if(this.nbTours >= getNbEquipes() - 1) {
            return false;
        }
        nbTours++;
        Vector<Match> ms = tours.get(nbTours-1);
        
        // Ensure we're using correct team IDs
        for (Match m : ms) {
            // Convert team numbers to IDs
            int team1Id = -1;
            int team2Id = -1;
            for(Equipe e : teams) {
                if(e.getNum() == m.getTeam1()) team1Id = e.getId();
                if(e.getNum() == m.getTeam2()) team2Id = e.getId();
            }
            if(team1Id != -1 && team2Id != -1) {
                m.setIdTournoi(getId());
                m.setTour(nbTours);
                Match newMatch = new Match(team1Id, team2Id);
                newMatch.setTour(nbTours);
                newMatch.setIdTournoi(getId());
                matchDAO.create(newMatch);
            }
        }
        
        if(this.matches == null) {
            this.matches = new Vector<>();
        }
        this.matches.addAll(ms);

        if(this.statut < 2) {
            setStatut(2);
        }
        return true;
    }

    public void supprimerTour(){
        if(nbTours < 1) return;
        matchDAO.deleteMatchByTournoiIdAndTourNumber(this.Id, nbTours);
        nbTours--;
    }

    public void updateMatch(Match m){
        matchDAO.update(m);
    }

    public void updateEquipe(Equipe e){
        equipesDAO.update(e);
    }

    public void ajouterEquipe(Equipe e) {
        if(this.teams == null) {
            this.teams = new Vector<>();
        }
        equipesDAO.create(e);
        this.teams.add(e);
    }

    public void supprimerEquipe(int ideq){
        int num = equipesDAO.getNumFromId(ideq, Id);
        equipesDAO.delete(ideq);
        if(num != -1) {
            equipesDAO.updateNum(Id, num);
        }
        // refresh teams from DB
        setEquipes(equipesDAO.getAll());
    }

    public void computeRank(){
        // Just a demonstration
        // In reality, we'd compute the final scoreboard
        if(teams == null) return;
        HashMap<Integer, Integer> rank = new HashMap<>();
        for (Equipe e : teams){
            int matchs_gagnes = equipesDAO.getMatchsGagne(Id, e.getId()).size();
            int matchs_nuls   = equipesDAO.getMatchsNul(Id, e.getId()).size();
            int score = matchs_nuls + (matchs_gagnes * 3);
            rank.put(e.getId(), score);
        }
        System.out.println("Ranking Map: " + rank);
    }
}
