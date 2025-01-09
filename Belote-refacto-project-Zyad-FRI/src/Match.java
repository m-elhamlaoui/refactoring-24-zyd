public class Match {
    private int id;
    private int team1;
    private int team2;
    private int score1;
    private int score2;
    private int tournamentNumber;
    private int tour;
    private boolean isFinished;

    public Match(int id, int team1, int team2, int score1, int score2, int tournamentNumber, boolean isFinished) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.score1 = score1;
        this.score2 = score2;
        this.tournamentNumber = tournamentNumber;
        this.isFinished = isFinished;
    }

    public Match(int team1, int team2) {
        this.team1 = team1;
        this.team2 = team2;
        this.score1 = 0;
        this.score2 = 0;
        this.isFinished = false;
    }

    // Getters
    public int getId() { return id; }
    public int getTeam1() { return team1; }
    public int getTeam2() { return team2; }
    public int getTour() { return tour; }
    public int getScore1() { return score1; }
    public int getScore2() { return score2; }
    public int getTournamentNumber() { return tournamentNumber; }
    public boolean getIsFinished() { return isFinished; }

    // Setters
    public void setTour(int tour) { this.tour = tour; }
    public void setIdTournoi(int idTournoi) { this.tournamentNumber = idTournoi; }
    public void setScore1(int score1) { 
        this.score1 = score1;
        checkIfFinished();
    }
    public void setScore2(int score2) { 
        this.score2 = score2;
        checkIfFinished();
    }

    private void checkIfFinished() {
        // A match is finished if at least one team has scored
        this.isFinished = (score1 > 0 || score2 > 0);
    }

    @Override
    public String toString() {
        return String.format("Tour %d: Équipe %d contre Équipe %d (%d-%d)",
            tour, team1, team2, score1, score2);
    }
}