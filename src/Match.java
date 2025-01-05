public class Match {
    private int id, team1, team2, score1, score2, tournamentNumber,tour;
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



    public Match (int team1, int team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    public int getId() {
        return id;
    }

    public int getTeam1() {
        return team1;
    }

    public int getTeam2() {
        return team2;
    }

    public int getTour() {
        return tour;
    }

    public void setTour(int tour) {
        this.tour = tour;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public int getTournamentNumber() {
        return tournamentNumber;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public void setIdTournoi(int idTournoi) {
        this.tournamentNumber = idTournoi;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }
    @Override
    public String toString() {
        int firstTeam = Math.min(team1, team2);
        int secondTeam = Math.max(team1, team2);

        return String.format("  %d contre %d", firstTeam, secondTeam);
    }
}
